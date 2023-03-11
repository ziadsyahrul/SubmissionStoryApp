package com.example.submissionstoryapp.ui.maps

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityMapsBinding
import com.example.submissionstoryapp.utils.NetworkResource
import com.example.submissionstoryapp.utils.PreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.jar.Manifest

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var prefs: PreferencesManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        prefs = PreferencesManager(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        getMyLocation()
        getMarkerLocation()

    }

    private fun getMarkerLocation() {
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.getLocation(prefs.token).collectLatest { results ->
                    when (results) {
                        is NetworkResource.SUCCESS -> {
                            results.data?.listStory?.forEach { story ->
                                if (story.latitude != null && story.longitude != null) {
                                    val latLng = LatLng(story.latitude, story.longitude)
                                    mMap.addMarker(
                                        MarkerOptions().position(latLng).title(story.name)
                                            .snippet("${story.latitude}, ${story.longitude}")
                                    )
                                }
                            }
                        }
                        is NetworkResource.LOADING -> {

                        }
                        is NetworkResource.ERROR -> {
                            Toast.makeText(
                                this@MapsActivity,
                                "terjadi kesalahan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    val latlon = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, 10f))
                }else{
                    Toast.makeText(this, "aktifkan lokasi di handphone mu", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            requestPermissionLauncer.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncer =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }

        }
}