package com.example.offlinelens

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(
    private val onQRCodeDetected: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {

    // 离线扫码识别客户端
    private val scanner = BarcodeScanning.getClient()
    private var isScanning = true

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScanning) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        isScanning = false // 暂停后续帧触发，防止重复弹窗
                        onQRCodeDetected(barcodes[0])
                    }
                }
                .addOnFailureListener {
                    // 识别失败或无二维码，继续检测下一帧
                }
                .addOnCompleteListener {
                    // 必须释放当前帧资源
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun resumeScanning() {
        isScanning = true
    }
}
