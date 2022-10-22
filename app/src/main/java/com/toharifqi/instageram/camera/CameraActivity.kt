package com.toharifqi.instageram.camera

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.toharifqi.instageram.BuildConfig
import com.toharifqi.instageram.R
import com.toharifqi.instageram.common.createFile
import com.toharifqi.instageram.createstory.CreateStoryActivity
import com.toharifqi.instageram.createstory.CreateStoryActivity.Companion.IS_BACK_CAMERA
import com.toharifqi.instageram.createstory.CreateStoryActivity.Companion.PHOTO_FILE
import com.toharifqi.instageram.databinding.ActivityCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        setUpClickListener()
    }

    private fun setUpClickListener() {
        with(binding) {
            fabCamera.setOnClickListener { takePhoto() }
            homeButton.setOnClickListener { onBackPressed() }
            switchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        setUpFullScreen()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(application)

        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this@CameraActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        getString(R.string.text_toast_fail_to_take_photo),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra(PHOTO_FILE, photoFile)
                    intent.putExtra(
                        IS_BACK_CAMERA,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(CreateStoryActivity.CAMERA_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    getString(R.string.text_toast_fail_to_open_camera),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setUpFullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}
