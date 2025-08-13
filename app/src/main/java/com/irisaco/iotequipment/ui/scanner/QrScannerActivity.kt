package com.irisaco.iotequipment.ui.scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Size
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.utils.LocaleHelper
import com.irisaco.iotequipment.utils.QRImageAnalyzer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class QrScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var btnScan: ImageButton
    private lateinit var btnScanLocal: ImageButton
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageAnalyzer: QRImageAnalyzer

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>

    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qr_scanner)

        applyWindowInsets()
        initViews()
        initListeners()
        initGalleryPicker()
        initPermissionLaunchers()

        cameraExecutor = Executors.newSingleThreadExecutor()
        imageAnalyzer = QRImageAnalyzer(supportFragmentManager)

        checkCameraPermissionAndStart()
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("app_settings", MODE_PRIVATE)
            .getString("language", "fa") ?: "fa"
        val contextWithLocale = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(contextWithLocale)
    }

    private fun applyWindowInsets() {
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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initListeners() {
        btnScan.setOnClickListener {
            imageAnalyzer.scanLatestFrameManually()
        }

        btnScanLocal.setOnClickListener {
            checkGalleryPermissionAndPickImage()
        }
    }

    private fun initGalleryPicker() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        imageAnalyzer.scanBitmap(bitmap)
                    } catch (e: IOException) {
                        showToast(getString(R.string.qr_msg_camera_fail))
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun initPermissionLaunchers() {
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                showPermissionDialog(
                    getString(R.string.qr_btn_camera),
                    getString(R.string.qr_msg_camera_permission_denied)
                )
            }
        }

        galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openGallery()
            } else {
                showPermissionDialog(
                    getString(R.string.qr_btn_gallery),
                    getString(R.string.qr_msg_permission_denied)
                )
            }
        }
    }

    private fun checkCameraPermissionAndStart() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(permission)
        }
    }

    private fun startCamera() {
        mainScope.launch {
            val cameraProvider = ProcessCameraProvider.getInstance(this@QrScannerActivity).await()
            bindCamera(cameraProvider)
        }
    }

    private fun bindCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(cameraExecutor, imageAnalyzer)
            }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            this,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalysis
        )
    }

    private fun checkGalleryPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            galleryPermissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun showPermissionDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.qr_permission_required_title, title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.qr_action_go_to_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.qr_action_cancel), null)
            .show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        mainScope.cancel()
    }
}
