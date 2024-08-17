package com.example.kumandraPJ.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.kumandraPJ.R
import com.example.kumandraPJ.createCustomTempFile
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.databinding.ActivityAddStoryBinding
import com.example.kumandraPJ.data.remote.response.Report
import com.example.kumandraPJ.reduceFileImage
import com.example.kumandraPJ.rotateBitmap
import com.example.kumandraPJ.uriToFile
import com.example.kumandraPJ.viewmodel.AddStoryViewModel
import com.example.kumandraPJ.viewmodel.StatusViewModel
import com.example.kumandraPJ.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var statusViewModel: StatusViewModel
    private lateinit var detailReport: Report
    private var isFinish: Boolean = false

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var location: Location

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "tidak mendapatkan izin camera",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                val result = rotateBitmap(BitmapFactory.decodeFile(file.path))
                binding.ivAdd.setImageBitmap(result)
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
                this,
                REQUIRED_CAMERA_PERMISSIONS,
                REQUEST_CODE_CAMERA_PERMISSIONS
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()


        detailReport = intent.getParcelableExtra<Report>(REPORT) as Report
        binding.buttonUpload.setOnClickListener {
                submitProcess()
        }
        binding.buttonCamera.setOnClickListener { startTakePhoto() }
        binding.buttonGallery.setOnClickListener { startGallery() }

        if (detailReport.id_status == "3"){
            isFinish = true
        } else if (detailReport.id_status == "2"){
            isFinish = false
        }

        viewModelConfig()
        updateUIStatus()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.addRespon)

        if (detailReport.id_status != "2" ){
            binding.buttonUpload.text = getString(R.string.finish)
            binding.levelInputLayout.visibility = View.GONE
            binding.tvLevel.visibility = View.GONE
        }

        val level = mutableListOf<String>("Darurat","Biasa")
        val adapter = ArrayAdapter(this, R.layout.item_dropdown,level)
        (binding.levelInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLastLocation(){
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    location = loc
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location not found",
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
        }
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

        statusViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[StatusViewModel::class.java]

    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbAdd.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateUIStatus(){
        statusViewModel.getStatus()
        statusViewModel.status.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.status?.filter { status ->
                        status.id_status_respon != "1" &&  status.id_status_respon != "2"
                    }?.forEach { status ->
                        items.add(status.nama_status)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.statusInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }
            }
        }
    }

    private fun submitProcess() {
        val desc = binding.etResp.text.toString()
        val selectedStatus = binding.statusInputLayout.editText?.text.toString()
        val selectedStatusId = statusViewModel.status.value?.data?.status?.find{
            it.nama_status == selectedStatus
        }?.id_status_respon
        val selectedLevel = binding.levelInputLayout.editText?.text.toString()

        when {
            selectedStatus.isEmpty() -> binding.statusInputLayout.editText?.error = "Perbarui status laporan"
            desc.isEmpty() -> binding.etResp.error = "Kolom tidak boleh kosong"
//            getFile == null ->
//                Toast.makeText(
//                    this@AddStoryActivity,
//                    "Choose the image first",
//                    Toast.LENGTH_LONG
//                ).show()

            else -> {
//                val respon = binding.etResp.text.toString()
//                val idStatus = selectedStatusId.toString()
                latitude = location.latitude.toDouble()
                longitude = location.longitude.toDouble()

                val idReport = detailReport.id_report.toString().toRequestBody("text/plain".toMediaType())
                val idPj = detailReport.id_pj.toString().toRequestBody("text/plain".toMediaType())
                val respon = binding.etResp.text.toString().toRequestBody("text/plain".toMediaType())
                val idStatus = selectedStatusId.toString().toRequestBody("text/plain".toMediaType())
                val level = selectedLevel.toString().toRequestBody("text/plain".toMediaType())
                val lat =
                    latitude.toString().toRequestBody("text/plain".toMediaType())
                val lon =
                    longitude.toString().toRequestBody("text/plain".toMediaType())
                val file = reduceFileImage(getFile as File)
                val requestImageFile = file.asRequestBody("pictures/jpeg".toMediaType())


                if (!isFinish){
                    val imageMultipar = MultipartBody.Part.createFormData(
                        "pictures_process",
                        file.name,
                        requestImageFile
                    )
                    addStoryViewModel.sendResponse(
                        TOKEN,
                        imageMultipar,
                        null,
                        idReport,
                        idPj,
                        idStatus,
                        level,
                        respon,
                        lat,
                        lon )
                } else {
                    val imageMultipar = MultipartBody.Part.createFormData(
                        "pictures_done",
                        file.name,
                        requestImageFile
                    )
                    addStoryViewModel.sendResponse(
                        TOKEN,
                        null,
                        imageMultipar,
                        idReport,
                        idPj,
                        idStatus,
                        level,
                        respon,
                        lat,
                        lon )
                }


            }
        }
    }

//    private fun submitFinish() {
//        val desc = binding.etResp.text.toString()
//        val selectedStatus = binding.statusInputLayout.editText?.text.toString()
//        val selectedStatusId = statusViewModel.status.value?.data?.status?.find{
//            it.nama_status == selectedStatus
//        }?.id_status_respon
//        val selectedLevel = binding.levelInputLayout.editText?.text.toString()
//
//        when {
//            selectedStatus.isEmpty() -> binding.statusInputLayout.editText?.error = "Perbarui status laporan"
//            desc.isEmpty() -> binding.etResp.error = "Kolom tidak boleh kosong"
//
//            else -> {
//                val idReport = detailReport.id_report
//                val idPj = detailReport.id_pj
//                val respon = binding.etResp.text.toString()
//                val idStatus = selectedStatusId.toString()
//                addStoryViewModel.sendResponse(TOKEN, idReport, idPj, idStatus, selectedLevel, respon)
//
//            }
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // or onBackPressed() to handle back navigation
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val REQUIRED_CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
        var TOKEN = "token"
        const val REPORT = "report"
    }
}