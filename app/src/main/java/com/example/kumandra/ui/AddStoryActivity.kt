package com.example.kumandra.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kumandra.createCustomTempFile
import com.example.kumandra.data.local.ClassesModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.databinding.ActivityAddStoryBinding
import com.example.kumandra.reduceFileImage
import com.example.kumandra.uriToFile
import com.example.kumandra.viewmodel.AddStoryViewModel
import com.example.kumandra.viewmodel.BuildingViewModel
import com.example.kumandra.viewmodel.ClassesViewModel
import com.example.kumandra.viewmodel.DetailFacilViewModel
import com.example.kumandra.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var latLng: LatLng? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var buildingViewModel: BuildingViewModel
    private lateinit var classesViewModel: ClassesViewModel
    private lateinit var detailFacilViewModel: DetailFacilViewModel


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                binding.ivAdd.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
            getFile = myFile
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.ivAdd.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.buttonCamera.setOnClickListener { startTakePhoto() }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonUpload.setOnClickListener { submit() }

        viewModelConfig()
    }

    private fun viewModelConfig(){
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[AddStoryViewModel::class.java]
        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        addStoryViewModel.msg.observe(this) {
            AlertDialog.Builder(this).apply {
                setTitle("STATE")
                setMessage(it)
                setPositiveButton("OK") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }

        buildingViewModel.building.observe(this, Observer { building ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, building.map { it.nama_bulding })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerBuilding.adapter = adapter
        })
        buildingViewModel.fetchBuildings()

        classesViewModel.classes.observe(this, Observer { classes ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes.map { it.nama_classes })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerClasses.adapter = adapter
        })
        classesViewModel.fetchClasses()

        detailFacilViewModel.detailFacil.observe(this, Observer { detailFacil ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, detailFacil.map { it.nama_detail_facilities })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDetailFacil.adapter = adapter
        })
        detailFacilViewModel.fetchDetailFacil()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbAdd.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Tidak Mendapatkan Permission", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity, "com.example.kumandra", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)

        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun submit() {
        val desc = binding.etDesc.text.toString()
        val selectedBuilding = binding.spinnerBuilding.selectedItem.toString()
        val selectedBuildingId = buildingViewModel.building.value?.find {
            it.nama_bulding == selectedBuilding
        }?.id_building
        val selectedClasses = binding.spinnerClasses.selectedItem.toString()
        val selectedClassesId = classesViewModel.classes.value?.find {
            it.nama_classes == selectedClasses
        }?.id_classes
        val selectedDetailFacil = binding.spinnerDetailFacil.selectedItem.toString()
        val selectedDetailFacilId = detailFacilViewModel.detailFacil.value?.find {
            it.nama_detail_facilities == selectedDetailFacil
        }?.id_detail_facilities

        when {
            desc.isEmpty() -> binding.etDesc.error = "Fill description"
            getFile == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Choose the image first",
                    Toast.LENGTH_LONG
                ).show()
            }
            selectedBuildingId == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Choose the building here",
                    Toast.LENGTH_LONG
                ).show()
            }
            selectedClassesId == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Choose the class here",
                    Toast.LENGTH_LONG
                ).show()
            }
            selectedDetailFacilId == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Choose the facility here",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val description =
                    binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())
                val id_building =
                    selectedBuildingId.toString().toRequestBody("text/plain".toMediaType())
                val id_classes =
                    selectedClassesId.toString().toRequestBody("text/plain".toMediaType())
                val id_detailFacil =
                    selectedDetailFacilId.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                addStoryViewModel.uploadStory(TOKEN, imageMultipart, id_building,id_classes,id_detailFacil,description, latLng)

            }
        }
    }


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        var TOKEN = "token"
    }
}