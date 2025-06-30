package com.irisaco.iotequipment.ui.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.utils.QRImageAnalyzer
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var btnScan: ImageButton
    private lateinit var btnScanLocal: ImageButton
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageAnalyzer: QRImageAnalyzer

    private val mainScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qr_scanner)

        setupInsets()
        initViews()
        setupGalleryPicker()

        cameraExecutor = Executors.newSingleThreadExecutor()
        imageAnalyzer = QRImageAnalyzer(supportFragmentManager)

        checkCameraPermission()

        btnScan.setOnClickListener {
            imageAnalyzer.scanLatestFrameManually()
        }

        btnScanLocal.setOnClickListener {
            val permission = getReadStoragePermission()

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_IMAGE_PERMISSION)
            }
        }




    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        previewView = findViewById(R.id.previewView)
        btnScan = findViewById(R.id.btn_scanBarcode)
        btnScanLocal = findViewById(R.id.btn_scanLocal)
        window.setFlags(1024, 1024)
    }

    private fun setupGalleryPicker() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if (result.resultCode == RESULT_OK && data != null) {
                val imageUri: Uri? = data.data
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imageAnalyzer.scanBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(this, "Unable to read image", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }
    private fun getReadStoragePermission(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun setupCamera() {
        mainScope.launch {
            val cameraProvider = ProcessCameraProvider.getInstance(this@QrScannerActivity).await()
            bindCamera(cameraProvider)
        }
    }



    private fun bindCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageCapture = ImageCapture.Builder().build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(cameraExecutor, imageAnalyzer)
            }

        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            setupCamera()
        } else if (requestCode == REQUEST_IMAGE_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else if (requestCode == REQUEST_IMAGE_PERMISSION) {
            Toast.makeText(this, "Permission denied to access images", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        mainScope.cancel()
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 101
        private const val REQUEST_IMAGE_PERMISSION = 102
    }
}
