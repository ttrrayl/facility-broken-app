package com.example.kumandra.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.bumptech.glide.Glide
import com.example.kumandra.R
import com.example.kumandra.createCustomTempFile
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.databinding.ActivityAddStoryBinding
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.reduceFileImage
import com.example.kumandra.rotateBitmap
import com.example.kumandra.uriToFile
import com.example.kumandra.viewmodel.AddStoryViewModel
import com.example.kumandra.viewmodel.BuildingViewModel
import com.example.kumandra.viewmodel.ClassesViewModel
import com.example.kumandra.viewmodel.DetailFacilViewModel
import com.example.kumandra.viewmodel.ViewModelFactory
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
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var buildingViewModel: BuildingViewModel
    private lateinit var classesViewModel: ClassesViewModel
    private lateinit var detailFacilViewModel: DetailFacilViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var location: Location
    private var detailReport: Report? = null
    private var isEdit: Boolean = false

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


        detailReport = intent.getParcelableExtra(report)
        if (detailReport != null){
            isEdit = true
        }
        val actionBarTitle: String
        val btnTitle: String
        if (isEdit){
            actionBarTitle = getString(R.string.change)
            btnTitle = getString(R.string.update)
            //?
            if (detailReport != null){
                detailReport.let { note ->
                    if (note != null) {
                        Glide.with(this)
                            .load(note.image_url)
                            .into(binding.ivAdd)
//                        val myFile = File(note.image_url)
//                        getFile = myFile
//                        val selectedImg: Uri = note.image_url as Uri
//                        selectedImg.let { uri ->
//                            val myFile = uriToFile(uri, this@AddStoryActivity)
//                            getFile = myFile
//                            binding.ivAdd.setImageURI(uri)
//                        }
                        binding.detailFacilInputLayout.editText?.setText(note.nama_detail_facilities)
                        binding.buildingInputLayout.editText?.setText(note.nama_building)
                        binding.classesInputLayout.editText?.setText(note.nama_classes)
                        binding.etDesc.setText(note.description)
                    }
                }
            }
        } else {
            actionBarTitle = getString(R.string.add)
            btnTitle = getString(R.string.save)
        }
        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.buttonUpload.text = btnTitle

        binding.buttonCamera.setOnClickListener { startTakePhoto() }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonUpload.setOnClickListener { submit() }

        viewModelConfig()
        updateUIBuilding()

        binding.buildingInputLayout.setOnClickListener{
            updateUIBuilding()
        }
//        binding.classesInputLayout.setOnClickListener{
//            val selectedBuilding = binding.buildingInputLayout.editText?.text.toString()
//            if (selectedBuilding.isEmpty()) {
//                binding.buildingInputLayout.editText?.error = "filling first"
//            }
//            val selectedClassesId =
//                buildingViewModel.builds.value?.data?.building?.find { building ->
//                    building.nama_building == selectedBuilding
//                }?.id_building
//            updateUIClasses(selectedClassesId.toString())
//        }
//
//        binding.detailFacilInputLayout.setOnClickListener {
//            val selectedClasses = binding.classesInputLayout.editText?.text.toString()
//            if (selectedClasses.isEmpty()){
//                binding.classesInputLayout.editText?.error = "filling first"
//            }
//            val selectedClassesId = classesViewModel.listClasses.value?.data?.find {
//                it.nama_classes == selectedClasses
//            }?.id_classes
//            updateUIFacil(selectedClassesId.toString())
//        }

//        binding.autBuilding.setOnItemClickListener { parent, view, position, id ->
//            //  val selected = it.data?.building?.get(position)
//            val selectedBuilding = binding.buildingInputLayout.editText?.text.toString()
//            if (selectedBuilding.isEmpty()) {
//                binding.buildingInputLayout.editText?.error = "filling first"
//            }
//            val selectedClassesId =
//                buildingViewModel.builds.value?.data?.building?.find { building ->
//                    building.nama_building == selectedBuilding
//                }?.id_building
//            updateUIClasses(selectedClassesId.toString())
//        }

//        binding.classesInputLayout.editText?.setOnClickListener {
//
//            binding.autClasses.setOnItemClickListener{ parent,view,position, id ->
//                val selectedClasses = binding.classesInputLayout.editText?.text.toString()
//                if (selectedClasses.isEmpty()){
//                    binding.classesInputLayout.editText?.error = "filling first"
//                }
//                val selectedClassesId = classesViewModel.classes.value?.data?.find {
//                    it.nama_classes == selectedClasses
//                }?.id_classes
//                updateUIFacil(selectedClassesId.toString())
//            }
//        }
//
//        binding.autClasses.setOnItemClickListener{ parent,view,position, id ->
//            val selectedClasses = binding.classesInputLayout.editText?.text.toString()
//            if (selectedClasses.isEmpty()){
//                binding.classesInputLayout.editText?.error = "filling first"
//            }
//            val selectedClassesId = classesViewModel.classes.value?.data?.find {
//                it.nama_classes == selectedClasses
//            }?.id_classes
//            updateUIFacil(selectedClassesId.toString())
//        }
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

        buildingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[BuildingViewModel::class.java]

        classesViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[ClassesViewModel::class.java]

        detailFacilViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[DetailFacilViewModel::class.java]

        classesViewModel.getClasses(null)
        detailFacilViewModel.getDetailFacil(null)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbAdd.visibility =
            if (isLoading) View.VISIBLE else View.GONE
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

    private fun getExifData(context: Context, imageUri: Uri): Pair<Double?,Double?>{
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val exif = ExifInterface(inputStream!!)
            val latLong = FloatArray(2)
            if (exif.getLatLong(latLong)) {
                return Pair(latLong[0].toDouble(), latLong[1].toDouble())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Pair(null, null)
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

    private fun updateUIBuilding(){
        buildingViewModel.getBuilding()
        buildingViewModel.builds.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.i("BUILDING", it.message.toString())
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.building?.forEach { building ->
                        items.add(building.nama_building)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.buildingInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }
                else -> {}
            }
            binding.autBuilding.setOnItemClickListener{ parent,view,position, id ->
                val selected = it.data?.building?.get(position)
                val selectedBuilding = binding.buildingInputLayout.editText?.text.toString()
                if (selectedBuilding.isEmpty()){
                    binding.buildingInputLayout.editText?.error ="filling first"
                }
                val selectedClassesId = buildingViewModel.builds.value?.data?.building?.find { building ->
                    building.nama_building == selectedBuilding
                }?.id_building
                updateUIClasses(selected?.id_building.toString())
            }
        }
    }

    private fun updateUIClasses(idBuilding:String){
        classesViewModel.getClasses(idBuilding)
        classesViewModel.listClasses.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.forEach { classes ->
                        items.add(classes.nama_classes)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.classesInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }
                else -> {}
            }
            binding.autClasses.setOnItemClickListener{ parent,view,position, id ->
                val selected = it.data?.get(position)
                val selectedClasses = binding.classesInputLayout.editText?.text.toString()
                if (selectedClasses.isEmpty()){
                    binding.classesInputLayout.editText?.error ="filling first"
                }
                val selectedClassesId = classesViewModel.classes.value?.data?.classes?.find { building ->
                    building.nama_classes == selectedClasses
                }?.id_classes
                updateUIFacil(selected?.id_classes.toString())
            }
        }
    }

    private fun updateUIFacil(idClasses: String){
        detailFacilViewModel.getDetailFacil(idClasses)
        detailFacilViewModel.listFacil.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.forEach { detailFacil ->
                        items.add(detailFacil.nama_detail_facilities)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.detailFacilInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }

                else -> {}
            }
        }
    }

    private fun submit() {
        val desc = binding.etDesc.text.toString()

        val selectedBuilding = binding.buildingInputLayout.editText?.text.toString()
        val selectedBuildingId = buildingViewModel.builds.value?.data?.building?.find {
            it.nama_building == selectedBuilding
        }?.id_building

        val selectedClasses = binding.classesInputLayout.editText?.text.toString()
        Log.i("NAMA KELAS", "kelas: $selectedClasses")
        val selectedClassesId = classesViewModel.classes.value?.data?.classes?.find {
            it.nama_classes == selectedClasses
        }?.id_classes
        Log.i("NAMA KELAS", "kelasID: $selectedClassesId")

        val selectedDetailFacil = binding.detailFacilInputLayout.editText?.text.toString()
        val selectedDetailFacilId =
            detailFacilViewModel.facil.value?.data?.detail_facilities?.find {
                it.nama_detail_facilities == selectedDetailFacil
            }?.id_detail_facilities


        when {
            desc.isEmpty() -> binding.etDesc.error = "Fill description"
            selectedBuilding.isEmpty() -> binding.buildingInputLayout.editText?.error = "Pilih gedung perkuliahan dahulu"
            selectedClasses.isEmpty() -> binding.classesInputLayout.editText?.error = "Pilih ruang kelas dahulu"
            selectedDetailFacil.isEmpty() -> binding.detailFacilInputLayout.editText?.error = "Pilih fasilitas dahulu"

//            getFile == null -> {
//                Toast.makeText(
//                    this@AddStoryActivity,
//                    "Choose the image first",
//                    Toast.LENGTH_LONG
//                ).show()
//            }

            else -> {
                val idStudent =
                    IDSTUDENT.toString().toRequestBody("text/plain".toMediaType())
//                val file = reduceFileImage(getFile as File)
//                val (imageLatitude, imageLongitude) = getExifData(this, file.toUri())
//                if (imageLatitude != null && imageLongitude != null) {
//                    latitude = imageLatitude
//                    longitude = imageLongitude
//
//                } else {
//                    latitude = location.latitude.toDouble()
//                    longitude = location.longitude.toDouble()
//                }
                latitude = location.latitude.toDouble()
                longitude = location.longitude.toDouble()
                val lat =
                    latitude.toString().toRequestBody("text/plain".toMediaType())
                val lon =
                    longitude.toString().toRequestBody("text/plain".toMediaType())
                val description =
                    binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())
                val idBuilding =
                    selectedBuildingId.toString().toRequestBody("text/plain".toMediaType())
                val idClasses =
                    selectedClassesId.toString().toRequestBody("text/plain".toMediaType())
                val idDetailFacil =
                    selectedDetailFacilId.toString().toRequestBody("text/plain".toMediaType())
//                val requestImageFile = file.asRequestBody("pictures/jpeg".toMediaType())
//                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
//                    "pictures",
//                    file.name,
//                    requestImageFile
//                )
//                val imageMultipart: MultipartBody.Part
//                if (getFile != null) {
//                    val file = reduceFileImage(getFile as File)
//                    val requestImageFile = file.asRequestBody("pictures/jpeg".toMediaType())
//                    imageMultipart = MultipartBody.Part.createFormData(
//                        "pictures",
//                        file.name,
//                        requestImageFile
//                    )
//                }

                if (!isEdit) {
                    if (getFile == null) {
                        Toast.makeText(
                            this@AddStoryActivity,
                            "Choose the image first",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val file = reduceFileImage(getFile as File)
                        val requestImageFile = file.asRequestBody("pictures/jpeg".toMediaType())
                        val imageMultipar = MultipartBody.Part.createFormData(
                            "pictures",
                            file.name,
                            requestImageFile
                        )
                        addStoryViewModel.uploadStory(
                            TOKEN,
                            imageMultipar,
                            idStudent,
                            idBuilding,
                            idClasses,
                            idDetailFacil,
                            description,
                            lat,
                            lon
                        )
                    }
                } else {
                    if (getFile != null) {
                        val file = reduceFileImage(getFile as File)
                        val requestImageFile = file.asRequestBody("pictures/jpeg".toMediaType())
                       val  imageMultipar = MultipartBody.Part.createFormData(
                            "pictures",
                            file.name,
                            requestImageFile
                        )
                        detailReport?.let {
                            addStoryViewModel.updateReport(
                                TOKEN,
                                it.id_report,
                                imageMultipar,
                                idBuilding,
                                idClasses,
                                idDetailFacil,
                                description
                            )
                        }
                        Log.d("EDIT REPORT1", "idBuilding: ${selectedBuildingId.toString()} idClasses: ${selectedClassesId.toString()} idDetail:${selectedDetailFacilId.toString()} description: ${description.toString()}")
                    } else{
                        detailReport?.let {
                            addStoryViewModel.updateReport(
                                TOKEN,
                                it.id_report,
                                null,
                                idBuilding,
                                idClasses,
                                idDetailFacil,
                                description
                            )
                        }
                        Log.d("EDIT REPORT2", "idBuilding: ${selectedBuildingId.toString()} idClasses: ${selectedClassesId.toString()} idDetail:${selectedDetailFacilId.toString()} description: ${desc.toString()}")
                    }
                }
            }
        }
    }

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
        var IDSTUDENT = ""
        const val report = "report"
    }
}