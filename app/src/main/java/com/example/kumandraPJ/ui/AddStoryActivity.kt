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
import android.util.Log
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

        detailReport = intent.getParcelableExtra<Report>(REPORT) as Report
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
                    it.data?.status?.forEach { status ->
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

        val selectedStatusId = statusViewModel.status.value?.data?.status?.find{
            it.nama_status == selectedStatus
        }?.id_status

        when {
            selectedStatus.isEmpty() -> binding.statusInputLayout.editText?.error = "Perbarui status laporan"
            desc.isEmpty() -> binding.etResp.error = "Kolom tidak boleh kosong"

            else -> {
                val idReport = detailReport.id_report.toString()
                val idPj = detailReport.id_pj.toString()
                val respon = binding.etResp.text.toString()
                val idStatus = selectedStatusId.toString()
               addStoryViewModel.sendResponse(TOKEN, idReport, idPj, idStatus, respon)

            }
        }
    }



    companion object {
        var TOKEN = "token"
        const val REPORT = "report"
    }
}