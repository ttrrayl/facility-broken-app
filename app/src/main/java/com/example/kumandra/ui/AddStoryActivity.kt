package com.example.kumandra.ui

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kumandra.R
import com.example.kumandra.createCustomTempFile
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.databinding.ActivityAddStoryBinding
import com.example.kumandra.checkPermissionsGranted
import com.example.kumandra.reduceFileImage
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
    private var latLng: LatLng? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var buildingViewModel: BuildingViewModel
    private lateinit var classesViewModel: ClassesViewModel
    private lateinit var detailFacilViewModel: DetailFacilViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null


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

//        if (!allPermissionsGranted()) {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//            )
//        } else{
//            getLastLocation()
//        }

        if (!REQUIRED_CAMERA_PERMISSIONS.checkPermissionsGranted(baseContext)){
            ActivityCompat.requestPermissions(
                this, REQUIRED_CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSIONS
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.buttonCamera.setOnClickListener { startTakePhoto() }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonUpload.setOnClickListener { submit() }

        viewModelConfig()
        updateUIBuilding()
        updateUIClasses()
        updateUIFacil()
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



//        buildingViewModel.building.observe(this) { building ->
//           val adapter: ArrayAdapter<String> = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_item,
//                building.map { it.nama_bulding })
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.spinnerBuilding.adapter = adapter
//        }


        classesViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[ClassesViewModel::class.java]

//        classesViewModel.classes.observe(this) { classes ->
//          val adapter: ArrayAdapter<String> = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_item,
//                classes.map { it.nama_classes })
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.spinnerClasses.adapter = adapter
//        }



        detailFacilViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserSession.getInstance(dataStore))
        )[DetailFacilViewModel::class.java]
//        val adapterDetailFacil: ArrayAdapter<String> =
//            ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
//        adapterDetailFacil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerDetailFacil.adapter = adapterDetailFacil
//        detailFacilViewModel.detailFacil.observe(this) { detailFacil ->
//          val adapter: ArrayAdapter<String> = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_item,
//                detailFacil.map { it.nama_detail_facilities })
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.spinnerDetailFacil.adapter = adapter
//        }
//        detailFacilViewModel.fetchDetailFacil()
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
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {
            if (!REQUIRED_CAMERA_PERMISSIONS.checkPermissionsGranted(baseContext)) {
                Toast.makeText(
                    this, "Tidak mendapatkan izin kamera", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        } else if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (!REQUIRED_LOCATION_PERMISSIONS.checkPermissionsGranted(baseContext)) {
                Toast.makeText(
                    this,"Tidak mendapatkan izin lokasi", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    }

//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }

            else -> {
                // No location access granted.
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getMyLastLocation(){
        if (REQUIRED_LOCATION_PERMISSIONS.checkPermissionsGranted(baseContext)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
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


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                location?.let {
//                    latitude = it.latitude
//                    longitude = it.longitude
//                }
//            }
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
            }
        }
    }

    private fun updateUIClasses(){
        classesViewModel.getClasses()
        classesViewModel.classes.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.classes?.forEach { classes ->
                        items.add(classes.nama_classes)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.classesInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }
            }
        }
    }

    private fun updateUIFacil(){
        detailFacilViewModel.getDetailFacil()
        detailFacilViewModel.detailFacil.observe(this){
            when(it){
                is Results.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is Results.Loading -> {
                }
                is Results.Success -> {
                    val items = mutableListOf<String>()
                    it.data?.detail_facilities?.forEach { detailFacil ->
                        items.add(detailFacil.nama_detail_facilities)
                    }
                    val adapter = ArrayAdapter(this, R.layout.item_dropdown,items)
                    (binding.detailFacilInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                        adapter
                    )
                }
            }
        }
    }

    private fun submit() {
        val desc = binding.etDesc.text.toString()
        val (imageLatitude, imageLongitude) = getExifData(this, getFile?.toUri()!!)
        if (imageLatitude != null && imageLongitude != null){
            latitude = imageLatitude
            longitude = imageLongitude
        }
        val selectedBuilding = binding.buildingInputLayout.editText?.text.toString()

//        val selectedItem = spinner.selectedItem
//        selectedItem?.let {
//            val selectedValue = it.toString()
//            // Gunakan selectedValue
//        } ?: run {
//            // Handle null case, mungkin tampilkan pesan kesalahan kepada pengguna
//        }
        val selectedBuildingId = buildingViewModel.builds.value?.data?.building?.find{
            it.nama_building == selectedBuilding
        }?.id_building

        val selectedClasses = binding.classesInputLayout.editText?.text.toString()
        val selectedClassesId = classesViewModel.classes.value?.data?.classes?.find{
            it.nama_classes == selectedClasses
        }?.id_classes

        val selectedDetailFacil = binding.detailFacilInputLayout.editText?.text.toString()
        val selectedDetailFacilId = detailFacilViewModel.detailFacil.value?.data?.detail_facilities?.find{
            it.nama_detail_facilities == selectedDetailFacil
        }?.id_detail_facilities

//        val selectedClasses = binding.spinnerClasses.selectedItem
//        selectedClasses?.toString() ?: run{
//            Toast.makeText(this, "null CLASSES", Toast.LENGTH_SHORT)
//        }
//        val selectedClassesId = classesViewModel.classes.value?.find {
//            it.nama_classes == selectedClasses
//        }?.id_classes
//
//        val selectedDetailFacil = binding.spinnerDetailFacil.selectedItem
//        selectedClasses?.toString() ?: run{
//            Toast.makeText(this, "null DETAIL FACIL", Toast.LENGTH_SHORT)
//        }
//        val selectedDetailFacilId = detailFacilViewModel.detailFacil.value?.find {
//            it.nama_detail_facilities == selectedDetailFacil
//        }?.id_detail_facilities

        when {
            desc.isEmpty() -> binding.etDesc.error = "Fill description"
            getFile == null -> {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Choose the image first",
                    Toast.LENGTH_LONG
                ).show()
            }

//            selectedBuildingId == null -> {
//                Toast.makeText(
//                    this@AddStoryActivity,
//                    "Choose the building here",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            selectedClassesId == null -> {
//                Toast.makeText(
//                    this@AddStoryActivity,
//                    "Choose the class here",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            selectedDetailFacilId == null -> {
//                Toast.makeText(
//                    this@AddStoryActivity,
//                    "Choose the facility here",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
            else -> {
                val file = reduceFileImage(getFile as File)
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
                val requestImageFile = file.asRequestBody("pictures/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "pictures",
                    file.name,
                    requestImageFile
                )
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
               addStoryViewModel.uploadStory(TOKEN, imageMultipart, idBuilding,idClasses,idDetailFacil,description, lat, lon)

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
    }
}