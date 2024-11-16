package com.example.quizpr

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Location : AppCompatActivity(), GoogleMap.OnMapClickListener {
    private lateinit var googleMap: GoogleMap
    private lateinit var editLocation: EditText
    private lateinit var addressEditText: EditText
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var satelliteButton: ImageButton
    private lateinit var terrainButton: ImageButton
    private lateinit var hybridButton: ImageButton
    private lateinit var normalButton: ImageButton
    private var lattitudee: Double = 0.0
    private var longitudee: Double = 0.0
    private var currentZoomLevel: Float = 17f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            this.googleMap = googleMap
            googleMap.setOnMapClickListener(this)
        }

        editLocation = findViewById(R.id.editTextText)
        addressEditText = findViewById(R.id.addressEditText)
        text1 = findViewById(R.id.textView)
        text2 = findViewById(R.id.textView2)

        val searchButton: Button = findViewById(R.id.button)
        searchButton.setOnClickListener {
            val locationName = editLocation.text.toString()
            addressEditText.text.clear()
            getLocationFromAddress(locationName)
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
                lattitudee = address.latitude
                longitudee = address.longitude

                val latLng = LatLng(lattitudee, longitudee)
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(location)
                )
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        17f
                    )
                )

                text1.text = "Latitude: $lattitudee"
                text2.text = "Longitude: $longitudee"
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
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                addressEditText.setText(address.getAddressLine(0))

                text1.text = "Latitude: ${latLng.latitude}"
                text2.text = "Longitude: ${latLng.longitude}"

                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(address.getAddressLine(0))
                )
                googleMap.moveCamera(
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
}