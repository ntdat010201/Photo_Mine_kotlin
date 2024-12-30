package com.example.photomine.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photomine.R
import com.example.photomine.databinding.ActivityCameraBinding
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.Const.REQUEST_CAMERA_PERMISSION
import com.example.photomine.utils.Const.REQUEST_RECORD_AUDIO_PERMISSION
import com.example.photomine.viewmodel.ViewModel
import com.google.common.util.concurrent.ListenableFuture
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: ViewModel by viewModel()

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private var isBackCamera = true
    private var isPhotoMode = true

    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo
    private var isFlashOn = false

    private lateinit var videoCapture: VideoCapture<Recorder>
    private var recording: Recording? = null

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
//        requestAudioPermission()
    }

    private fun initView() {
    }

    private fun initListener() {

        binding.tvToggle.setOnClickListener {
            toggleMore()
        }

        binding.buttonCapture.setOnClickListener {
            if (isPhotoMode) {
                takePicture()
            } else {
                if (recording == null) {
                    startRecording()
                    binding.buttonCapture.setImageResource(R.drawable.view_rounded_video_pause)
                } else {
                    stopRecording()
                    binding.buttonCapture.setImageResource(R.drawable.view_rounded_video_play)
                }
            }
        }

        binding.btnCameraSwitch.setOnClickListener {
            toggleCamera()
        }

        binding.flashButton.setOnClickListener {
            toggleFlash()
        }

    }

    ///////////
    private fun toggleMore() {
        isPhotoMode = !isPhotoMode
        if (isPhotoMode) {
            binding.tvToggle.text = "ảnh"
            binding.buttonCapture.setImageResource(R.drawable.view_rounded)
        } else {
            binding.tvToggle.text = "video"
            binding.buttonCapture.setImageResource(R.drawable.view_rounded_video_play)
        }
        bindCamera(cameraProviderFuture.get())
    }

    private fun toggleCamera() {
        isBackCamera = !isBackCamera // Thay đổi trạng thái camera
        bindCamera(cameraProviderFuture.get()) // Gọi lại bindCamera với camera mới
        isFlashOn = false // Tắt flash khi đổi camera
        binding.flashButton.setImageResource(R.drawable.ic_flash_off)
        cameraControl.enableTorch(false)
    }

    private fun toggleFlash() {
        if (!isFlashAvailable()) {
            Toast.makeText(this, "Thiết bị không hỗ trợ đèn flash", Toast.LENGTH_SHORT).show()
            return
        }
        isFlashOn = !isFlashOn
        cameraControl.enableTorch(isFlashOn)
        binding.flashButton.setImageResource(if (isFlashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off)
        Toast.makeText(this, if (isFlashOn) "Flash bật" else "Flash tắt", Toast.LENGTH_SHORT).show()
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    ////////////

    private fun bindCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = if (isBackCamera) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

//        val recorder = Recorder.Builder().setQualitySelector(
//            QualitySelector.from(
//                Quality.HIGHEST,
//                FallbackStrategy.lowerQualityOrHigherThan(Quality.LOWEST)
//            )
//        ).build()
//
//        videoCapture = VideoCapture.withOutput(recorder)
//

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, videoCapture
            )
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // video


    private fun startRecording() {
        val videoFile = File(
            externalMediaDirs.first(),
            "Video_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.mp4"
        )
        val outputOptions = FileOutputOptions.Builder(videoFile).build()


        recording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .apply {
                // Thêm âm thanh nếu cần
                if (ActivityCompat.checkSelfPermission(
                    this@CameraActivity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
                withAudioEnabled()

            }

            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        Toast.makeText(this, "Bắt đầu quay video", Toast.LENGTH_SHORT).show()
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (recordEvent.hasError()) {
                            Log.e("CameraX", "Lỗi quay video: ${recordEvent.error}")
                        } else {
                            // Lưu video sau khi quay xong

                            Log.d("DAT", "startRecording: $videoFile")
                            onVideoTaken(videoFile)

                            Toast.makeText(this, "Video đã được lưu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun onVideoTaken(videoFile: File) {
        // Gọi hàm lưu video vào MediaStore và lấy URI
        val videoUri = saveVideoToMediaStore(videoFile)
        Log.d("DAT", "onVideoTaken: $videoUri")

        // Nếu lưu video thành công, thêm video vào ViewModel
        if (videoUri != null) {
            val dateAdded = System.currentTimeMillis() // Thời gian hiện tại
            val size = videoFile.length() // Kích thước video
            val quality = "High" // Chất lượng video (tùy chỉnh nếu cần)

            // Tạo đối tượng model video
            val modelVideo = ModelImage(0, videoUri.toString(), dateAdded, size, quality)

            // Thêm video vào ViewModel
            viewModel.addVideo(modelVideo)
        }
    }

    private fun saveVideoToMediaStore(videoFile: File): Uri? {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Video.Media.DISPLAY_NAME, "video_${System.currentTimeMillis()}.mp4"
            ) // Tên file
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4") // Định dạng video
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES
                ) // Thư mục Movies
            } else {
//                put(MediaStore.Video.Media.DATA, videoFile.absolutePath) // Android 9 trở xuống
                put(
                    MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                        .absolutePath + "/video${System.currentTimeMillis()}.mp4"
                )
            }
        }

        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                videoFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("DAT", "saveVideoToMediaStore: $uri")
            return uri
        }
        return null
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }


    //check Permission

    private fun requestCameraPermission() {
        if (allPermissionsGranted()) {
            startCamera() // Nếu quyền đã được cấp
        } else {
            requestPermissions() // Nếu chưa được cấp
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Yêu cầu quyền
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CAMERA_PERMISSION)
    }

    // Xử lý kết quả yêu cầu quyền
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startCamera() // Nếu quyền được cấp, khởi chạy camera
            } else {
                handlePermissionDenied() // Nếu quyền bị từ chối
            }
        }
    }
    // Hành động khi quyền bị từ chối
    private fun handlePermissionDenied() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
        ) {
            // Hiển thị lý do tại sao cần quyền
            AlertDialog.Builder(this)
                .setTitle("Quyền cần thiết")
                .setMessage("Ứng dụng cần quyền Camera và Âm thanh để hoạt động. Vui lòng cấp quyền.")
                .setPositiveButton("OK") { _, _ ->
                    requestPermissions() // Yêu cầu quyền lại
                }
                .setNegativeButton("Thoát") { _, _ ->
                    finish()
                }
                .show()
        } else {
            // Quyền bị từ chối vĩnh viễn
            Toast.makeText(
                this,
                "Quyền bị từ chối vĩnh viễn. Hãy cấp quyền trong Cài đặt.",
                Toast.LENGTH_LONG
            ).show()
            finish()
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
                    MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .absolutePath + "/image_${System.currentTimeMillis()}.jpg"
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
            externalMediaDirs.first(),
            "Photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

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

    private fun isFlashAvailable(): Boolean {
        return cameraInfo.hasFlashUnit()
    }


}