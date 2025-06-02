package com.irisaco.iotequipment

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
import com.irisaco.iotequipment.ui.components.BottomDialog


class QRImageAnalyzer(private val fragmentManager: FragmentManager) : ImageAnalysis.Analyzer {

    private var isScanned = false
    private val dialog = BottomDialog()

    override fun analyze(image: ImageProxy) {
        if (!isScanned) {
            scanBarcode(image)
        } else {
            image.close()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun scanBarcode(image: ImageProxy) {
        val mediaImage: Image = image.image ?: run {
            image.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
            .build()

        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    isScanned = true
                    readBarcode(barcodes)
                }
            }
            .addOnFailureListener {
                Toast.makeText(dialog.activity, "Failed to read code", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                image.close()
            }
    }

    private fun readBarcode(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue ?: continue
            Log.d("QRScanner", "Read value: $rawValue")

            if (rawValue.matches(Regex("\\d+"))) {
                if (!dialog.isAdded) {
                    dialog.setEquipmentId(rawValue)
                    dialog.show(fragmentManager, "EQUIPMENT_ID")
                }
            } else {
                Log.d("QRScanner", "Not an ID: $rawValue")
            }

            break
        }
    }
}