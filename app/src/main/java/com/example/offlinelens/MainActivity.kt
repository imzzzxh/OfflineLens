package com.example.offlinelens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.offlinelens.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var analyzer: QRCodeAnalyzer

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "需要相机权限才能使用扫码功能", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        analyzer = QRCodeAnalyzer { barcode ->
            runOnUiThread {
                handleScanResult(barcode)
            }
        }

        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, analyzer)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun handleScanResult(barcode: Barcode) {
        val rawValue = barcode.rawValue ?: return

        when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                AlertDialog.Builder(this)
                    .setTitle("扫描结果 · 网址")
                    .setMessage(rawValue)
                    .setPositiveButton("打开网址") { _, _ ->
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(this, "无法打开此链接", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("复制") { _, _ ->
                        copyToClipboard(rawValue)
                    }
                    .setOnDismissListener { analyzer.resumeScanning() }
                    .show()
            }
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi?.ssid ?: "未知"
                val password = barcode.wifi?.password ?: "无密码"
                AlertDialog.Builder(this)
                    .setTitle("扫描结果 · Wi-Fi")
                    .setMessage("Wi-Fi 名称: $ssid\n密码: $password")
                    .setPositiveButton("复制密码") { _, _ ->
                        copyToClipboard(password)
                    }
                    .setNegativeButton("关闭", null)
                    .setOnDismissListener { analyzer.resumeScanning() }
                    .show()
            }
            else -> {
                // 纯文本、JSON 或特定协议（无网秒解析）
                AlertDialog.Builder(this)
                    .setTitle("扫描结果 · 离线文本")
                    .setMessage(rawValue)
                    .setPositiveButton("复制内容") { _, _ ->
                        copyToClipboard(rawValue)
                    }
                    .setNegativeButton("关闭", null)
                    .setOnDismissListener { analyzer.resumeScanning() }
                    .show()
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ScanResult", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
