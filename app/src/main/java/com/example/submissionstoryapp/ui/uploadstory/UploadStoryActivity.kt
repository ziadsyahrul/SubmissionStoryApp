package com.example.submissionstoryapp.ui.uploadstory

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.coroutineScope
import com.example.submissionstoryapp.databinding.ActivityUploadStoryBinding
import com.example.submissionstoryapp.ui.main.MainActivity
import com.example.submissionstoryapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private var job: Job = Job()
    private lateinit var prefs: PreferencesManager
    private val viewModel: UploadViewModel by viewModels()
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var location: Location? = null

    companion object {
        private const val REQUEST_CODE_PERMISSION = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.btnToCamera.setOnClickListener { takePhoto() }
        binding.btnToGallery.setOnClickListener { gallery() }
        binding.btnUpload.setOnClickListener { upload() }

    }

    private fun upload() {
        if (getFile != null || !TextUtils.isEmpty(binding.etDescription.text.toString())) {
            uploadStory(prefs.token)
        } else {
            Toast.makeText(this, "Mohon mengisi deskripsi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadStory(token: String) {
        val file = reduceFileImage(getFile as File)

        val reqImage = file.asRequestBody("image/jpg".toMediaType())
        val imgMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            reqImage
        )

        val desc = binding.etDescription.text.toString().trim()
        var lat: String? = null
        var lon: String? = null

        if (location != null) {
            lat = location?.latitude.toString()
            lon = location?.longitude.toString()
        }

        lifecycle.coroutineScope.launchWhenResumed {
            if (job.isActive) job.cancel()
            job = launch {
                viewModel.uploadStory(token, desc, lat, lon, imgMultipart).collectLatest { result ->
                    when (result) {
                        is NetworkResource.SUCCESS -> {
                            Toast.makeText(
                                this@UploadStoryActivity,
                                "Success add story",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@UploadStoryActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                        is NetworkResource.LOADING -> {

                        }

                        is NetworkResource.ERROR -> {
                            Toast.makeText(
                                this@UploadStoryActivity,
                                "Gagal menambahkan story, coba lagi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }
    }

    private fun gallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this,
                "com.example.submissionstoryapp",
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }

    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val selectedImg: Uri = it.data?.data as Uri
                val myFile = uriToFile(selectedImg, this)
                getFile = myFile
                binding.imgUploadPreview.setImageURI(selectedImg)
            }
        }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)
                val rotate = rotateBitmap(BitmapFactory.decodeFile(myFile.path), true)
                val temp = getImageUri(rotate, this)
                getFile = uriToFile(temp, this)
                binding.imgUploadPreview.setImageBitmap(rotate)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}