package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserSession
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        configViewModel()
    }

    private fun configViewModel() {
        val pref = UserSession.getInstance(dataStore)

        mainViewModel = ViewModelProvider(this, ViewModelFactory(this, pref))[MainViewModel::class.java]
        mainViewModel.getStoriesMap(AddStoryActivity.TOKEN)
        mainViewModel.listStoryMap.observe(this){
            if (it.isEmpty()){
                AlertDialog.Builder(this@MapsActivity).apply {
                    setTitle("Sorry :(")
                    setMessage("There is nothing here")
                    setPositiveButton("OK"){_,_ ->
                        startActivity(Intent(context, MainActivity::class.java))
                        finish()
                    }
                    create()
                    show()
                }
            }
            listStoryMap(it)
        }
    }

    private fun listStoryMap(itemList: List<ListStoryItem>?){
        itemList?.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }
}