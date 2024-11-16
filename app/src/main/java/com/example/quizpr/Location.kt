package com.example.quizpr

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Location : AppCompatActivity(), GoogleMap.OnMapClickListener {
    private lateinit var googleMap: GoogleMap
    private lateinit var editLocation: EditText
    private lateinit var addressEditText: EditText
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentZoomLevel: Float = 17f
    private lateinit var satelliteButton: ImageButton
    private lateinit var terrainButton: ImageButton
    private lateinit var hybridButton: ImageButton
    private lateinit var normalButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            this.googleMap = googleMap
            googleMap.setOnMapClickListener(this)
        }
        editLocation = findViewById(R.id.editTextText)
        addressEditText = findViewById(R.id.addressEditText)
        text1 = findViewById(R.id.textView)
        text2 = findViewById(R.id.textView2)
        val searchButton: ImageButton = findViewById(R.id.button)
        searchButton.setOnClickListener {
            val locationName = editLocation.text.toString().trim()
            if (locationName.isEmpty()) {
                Toast.makeText(this, "Enter a location to search", Toast.LENGTH_SHORT).show()
            } else {
                addressEditText.text.clear()
                getLocationFromAddress(locationName)
            }
        }
        val getLocationButton: ImageButton = findViewById(R.id.currentLocationIcon)
        getLocationButton.setOnClickListener {
            getCurrentLocation()
        }
        val fab: FloatingActionButton = findViewById(R.id.floatingActionButton)
        val mapOptionsLayout: LinearLayout = findViewById(R.id.mapOptionsLayout)
        fab.setOnClickListener {
            mapOptionsLayout.visibility =
                if (mapOptionsLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        satelliteButton = findViewById(R.id.satelliteButton)
        satelliteButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        terrainButton = findViewById(R.id.terrainButton)
        terrainButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        hybridButton = findViewById(R.id.hybridButton)
        hybridButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        normalButton = findViewById(R.id.normalButton)
        normalButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onMapClick(latLng: LatLng) {
        currentZoomLevel = googleMap.cameraPosition.zoom
        getAddressFromLatLng(latLng)
    }

    private fun getLocationFromAddress(location: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(location, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng).title(location))
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                googleMap.animateCamera(cameraUpdate, 1500, null) // Duration: 1.5 seconds
                text1.text = "Latitude: ${address.latitude}"
                text2.text = "Longitude: ${address.longitude}"
                addressEditText.setText(address.getAddressLine(0))
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                addressEditText.setText(address.getAddressLine(0))
                text1.text = "Latitude: ${latLng.latitude}"
                text2.text = "Longitude: ${latLng.longitude}"
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(address.getAddressLine(0))
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        currentZoomLevel
                    )
                )
            } else {
                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Enable location services", Toast.LENGTH_SHORT).show()
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                updateUI(latLng)
            } else {
                requestNewLocation()
            }
        }
    }

    private fun requestNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    updateUI(latLng)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateUI(latLng: LatLng) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng).title("Current Location"))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        googleMap.animateCamera(cameraUpdate, 1500, null)
        text1.text = "Latitude: ${latLng.latitude}"
        text2.text = "Longitude: ${latLng.longitude}"
        val geocoder = Geocoder(this)
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            addressEditText.setText(addresses[0].getAddressLine(0))
        } else {
            Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}