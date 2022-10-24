package com.toharifqi.instageram.createstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.toharifqi.instageram.BaseApplication
import com.toharifqi.instageram.R
import com.toharifqi.instageram.camera.CameraActivity
import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.reduceFileImage
import com.toharifqi.instageram.common.rotateBitmap
import com.toharifqi.instageram.common.uriToFile
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.customview.InstaGeramEditText
import com.toharifqi.instageram.databinding.ActivityCreateStoryBinding
import com.toharifqi.instageram.storylist.StoryListActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class CreateStoryActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CreateStoryViewModel>
    private val viewModel: CreateStoryViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityCreateStoryBinding
    private var imageFile: File? = null
    private var userToken: String? = null
    private var isBackCamera: Boolean? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userToken = intent.getStringExtra(TOKEN_EXTRA) ?: ""

        observeViewModel()
        setUpViewsListener()
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
            postResult.observe(this@CreateStoryActivity) {
                when (it) {
                    is Success -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(
                            this@CreateStoryActivity,
                            getString(R.string.text_toast_story_posted),
                            Toast.LENGTH_SHORT
                        ).show()
                        Intent(this@CreateStoryActivity, StoryListActivity::class.java).run {
                            startActivity(this)
                        }
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

    private fun setUpViewsListener() {
        with(binding) {
            cameraButton.setOnClickListener { openCamera() }
            galleryButton.setOnClickListener { openGallery() }
            postButton.setOnClickListener { postStory() }
            switchIncludeLocation.setOnCheckedChangeListener { _, isEnabled ->
                if (isEnabled) getCurrentLocation() else location = null
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                } else {
                    Toast.makeText(
                        this@CreateStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            binding.switchIncludeLocation.isChecked = false
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getCurrentLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getCurrentLocation()
                }
                else -> {}
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
            isBackCamera = it.data?.getBooleanExtra(IS_BACK_CAMERA, true)

            imageFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(imageFile?.path),
                isBackCamera as Boolean
            )

            binding.photoImage.setImageBitmap(result)
        }
    }

    private fun openGallery() {
        Intent().run {
            action = ACTION_GET_CONTENT
            type = FILE_TYPE
            val chooser = Intent.createChooser(this, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            imageFile = uriToFile(selectedImg, this@CreateStoryActivity)
            binding.photoImage.setImageURI(selectedImg)
        }
    }

    private fun postStory() {
        binding.postButton.isEnabled = false
        if (imageFile != null) {
            val file = reduceFileImage(imageFile as File, isBackCamera)

            val description =
                binding.descriptionEditTxt.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            var lat: RequestBody? = null
            var lng: RequestBody? = null
            if (location != null) {
                lat =
                    location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lng =
                    location?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }


            binding.progressCircular.visibility = View.VISIBLE
            viewModel.postStory(
                userToken,
                imageMultipart,
                description,
                lat,
                lng
            )
        }
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

    companion object {
        const val CAMERA_RESULT = 200
        const val IS_BACK_CAMERA = "is_back_camera"
        const val PHOTO_FILE = "photo_file"
        const val FILE_TYPE = "image/*"
        const val TOKEN_EXTRA = "auth_token_extra"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
