package com.example.kumandraPJ.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.kumandraPJ.R
import com.example.kumandraPJ.createCustomTempFile
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.databinding.ActivityAddStoryBinding
import com.example.kumandraPJ.checkPermissionsGranted
import com.example.kumandraPJ.data.remote.response.Report
import com.example.kumandraPJ.reduceFileImage
import com.example.kumandraPJ.uriToFile
import com.example.kumandraPJ.viewmodel.AddStoryViewModel
import com.example.kumandraPJ.viewmodel.StatusViewModel
import com.example.kumandraPJ.viewmodel.ClassesViewModel
import com.example.kumandraPJ.viewmodel.DetailFacilViewModel
import com.example.kumandraPJ.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
 //   private lateinit var user: StudentModel
    private var latLng: LatLng? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var statusViewModel: StatusViewModel
    private lateinit var classesViewModel: ClassesViewModel
    private lateinit var detailFacilViewModel: DetailFacilViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var detailReport: Report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!REQUIRED_CAMERA_PERMISSIONS.checkPermissionsGranted(baseContext)){
            ActivityCompat.requestPermissions(
                this, REQUIRED_CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSIONS
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.buttonUpload.setOnClickListener { submit() }

        viewModelConfig()
        updateUIStatus()
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
                    it.data?.response?.forEach { status ->
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

    private fun submit() {
        val desc = binding.etResp.text.toString()
        val selectedStatus = binding.statusInputLayout.editText?.text.toString()

        val selectedStatusId = statusViewModel.status.value?.data?.response?.find{
            it.nama_status == selectedStatus
        }?.id_status

        detailReport = intent.getParcelableExtra<Report>(DetailStoryActivity.STORY_DETAIL) as Report

        when {
            desc.isEmpty() -> binding.etResp.error = "Kolom tidak boleh kosong"

            else -> {
                val idReport = detailReport.id_report
                val idPj = detailReport.id_pj
                val respon = binding.etResp.text.toString()
                val idStatus = selectedStatusId.toString()
//                AlertDialog.Builder(this).apply {
//                    setTitle("STATE")
//                    setMessage(latLng.toString())
//                    setPositiveButton("OK") { _, _ ->
//                        val intent = Intent(context, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                        startActivity(intent)
//                        finish()
//                    }
//                    create()
//                    show()
//                }
//                val report = arrayOf(idBuilding,idClasses,idDetailFacil,description, latLng).toString()
//                Log.d("addReport", "report : $report")
               addStoryViewModel.sendResponse(TOKEN, idReport, idPj, idStatus, respon)

            }
        }
    }

//    fun getExifData(context: Context, imageUri: Uri): Pair<Double?, Double?> {
//        try {
//            val inputStream = context.contentResolver.openInputStream(imageUri)
//            val exif = ExifInterface(inputStream!!)
//            val latLong = FloatArray(2)
//            if (exif.getLatLong(latLong)) {
//                return Pair(latLong[0].toDouble(), latLong[1].toDouble())
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return Pair(null, null)
//    }



    companion object {
        private val REQUIRED_LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        private val REQUIRED_CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_LOCATION_PERMISSIONS = 11
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
        var TOKEN = "token"
        var IDSTUDENT = 0
        var REPORT = "report"
    }
}