package com.toharifqi.instageram.createstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.camerax.CameraActivity
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.rotateBitmap
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.customview.InstaGeramEditText
import com.toharifqi.instageram.databinding.ActivityCreateStoryBinding
import java.io.File
import javax.inject.Inject

class CreateStoryActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CreateStoryViewModel>
    private val viewModel: CreateStoryViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityCreateStoryBinding
    private var imageFile: File? = null
    private var userToken: String? = null

    companion object {
        const val CAMERA_RESULT = 200
        const val IS_BACK_CAMERA = "is_back_camera"
        const val PHOTO_FILE = "photo_file"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@CreateStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.title_create_story)
        }

        binding.postButton.isEnabled = false

        viewModel.getToken()
        observeViewModel()
        setUpClickListener()
        setEditTextListener()
    }

    private fun setEditTextListener() {
        with(binding) {
            descriptionEditTxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setPostButtonEnable(descriptionEditTxt)
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun setPostButtonEnable(editText: InstaGeramEditText) {
        with(binding) {
            val result = editText.text
            postButton.isEnabled =
                (result != null
                        && result.toString().isNotEmpty()
                        && editText.error == null
                        && imageFile != null)
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            token.observe(this@CreateStoryActivity) {
                userToken = it
            }
            postResult.observe(this@CreateStoryActivity) {
                when (it) {
                    is Success -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(
                            this@CreateStoryActivity,
                            getString(R.string.text_toast_story_posted),
                            Toast.LENGTH_SHORT
                        ).show()
                        clearContent()
                    }
                    is Error   -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(this@CreateStoryActivity, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun clearContent() {
        imageFile = null
        with(binding) {
            photoImage.setImageResource(R.drawable.photo_placeholder)
            descriptionEditTxt.text?.clear()
        }
    }

    private fun setUpClickListener() {
        with(binding) {
            cameraButton.setOnClickListener { openCamera() }
            galleryButton.setOnClickListener { openGallery() }
            postButton.setOnClickListener { postStory() }
        }
    }

    private fun openCamera() {
        Intent(this@CreateStoryActivity, CameraActivity::class.java).run {
            launcherIntentCamera.launch(this)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_RESULT) {
            val myFile = it.data?.getSerializableExtra(PHOTO_FILE) as File
            val isBackCamera = it.data?.getBooleanExtra(IS_BACK_CAMERA, true) as Boolean

            imageFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(imageFile?.path),
                isBackCamera
            )

            binding.photoImage.setImageBitmap(result)
        }
    }

    private fun openGallery() {
        TODO("Not yet implemented")
    }

    private fun postStory() {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.text_toast_permission_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
