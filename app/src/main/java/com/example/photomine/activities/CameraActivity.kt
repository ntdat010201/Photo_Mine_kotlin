package com.example.photomine.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photomine.databinding.ActivityCameraBinding
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.Const.REQUEST_CAMERA_PERMISSION
import com.example.photomine.viewmodel.ViewModel
import com.google.common.util.concurrent.ListenableFuture
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val viewModel: ViewModel by viewModel()
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        requestCameraPermission()
    }

    private fun initView() {
    }

    private fun initListener() {
        binding.buttonCapture.setOnClickListener {
            takePicture()
        }
    }


    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

    }

    private fun bindCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    // Thông báo người dùng rằng quyền không được cấp
                    Toast.makeText(this, "Quyền camera không được cấp", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    //lưu ảnh vào MediaStore

    private fun saveImageToMediaStore(bitmap: Bitmap): Uri? {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg"
            ) // Tên hiển thị
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // Định dạng file
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {// android 9 trở xuống
                put(
                    MediaStore.Images.Media.DATA,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/image_${System.currentTimeMillis()}.jpg"
                )
            } else {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
                ) // Thư mục lưu
            }
        }

        // Chèn thông tin file vào MediaStore
        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            // Mở OutputStream để ghi ảnh vào URI vừa tạo
            contentResolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                } // Nén và ghi ảnh
            }
        }

        return uri // Trả về URI của ảnh vừa lưu
    }


    fun onPictureTaken(bitmap: Bitmap) {
        // Gọi hàm lưu ảnh vào MediaStore và lấy URI
        val imageUri = saveImageToMediaStore(bitmap)
        Log.d("DAT", "onPictureTaken:$imageUri ")
        // Nếu lưu ảnh thành công, thêm ảnh vào ViewModel
        if (imageUri != null) {
            val dateAdded = System.currentTimeMillis() // Thời gian hiện tại
            val size = bitmap.byteCount.toLong() // Kích thước ảnh
            val quality = "High" // Chất lượng ảnh, bạn có thể điều chỉnh theo nhu cầu
            val modelImage = ModelImage(0, imageUri.toString(), dateAdded, size, quality)

            viewModel.addImage(modelImage) // Thêm vào ViewModel
        }
    }


    private fun takePicture() {
        // Tạo file ảnh
        val photoFile = File(
            externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg"
        )
        Log.d("DAT", "takePicture: $photoFile")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Chuyển đổi file thành bitmap
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    Log.d("DAT", "onImageSaved: $bitmap")
                    // Gọi hàm xử lý ảnh đã chụp
                    onPictureTaken(bitmap)

                    Toast.makeText(this@CameraActivity, "Ảnh đã được lưu", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Lỗi chụp ảnh: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}