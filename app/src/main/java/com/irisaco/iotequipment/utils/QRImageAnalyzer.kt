package com.irisaco.iotequipment.utils

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.irisaco.iotequipment.ui.components.QRCodeBottomDialog

class QRImageAnalyzer(
    private val fragmentManager: FragmentManager
) : ImageAnalysis.Analyzer {

    private var isScanned = false
    private var manualScanRequested = false
    private val dialog = QRCodeBottomDialog()


    override fun analyze(image: ImageProxy) {

        if (isScanned && !manualScanRequested ) {
            image.close()
            return
        }
        val shouldProcess = manualScanRequested || !isScanned
        manualScanRequested = false

        if (shouldProcess) {
            scanBarcode(image)
        } else {
            image.close()
        }
    }

    fun scanLatestFrameManually() {
        manualScanRequested = true
    }

    @OptIn(ExperimentalGetImage::class)
    private fun scanBarcode(image: ImageProxy) {
        val mediaImage: Image = image.image ?: run {
            image.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            image.imageInfo.rotationDegrees)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
            .build()

        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    isScanned = true
                    showResult(barcodes)
                }
            }
            .addOnFailureListener {
                Toast.makeText(dialog.activity, "Failed to read code", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                image.close()
                scanner.close()
            }
    }
    fun scanBitmap(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
            .build()

        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    isScanned = true
                    showResult(barcodes)
                }
            }
            .addOnFailureListener {
                Toast.makeText(dialog.activity, "Failed to read code", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                scanner.close()
            }
    }


    private fun showResult(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue ?: continue
            Log.d("QRScanner", "Read value: $rawValue")
            if (!dialog.isAdded) {
                dialog.setEquipmentId(rawValue)
                dialog.setOnDismissListener {
                    isScanned = false
                }
                dialog.show(fragmentManager, "EQUIPMENT_ID")
            }
            break
        }
    }



}