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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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

    // Variable to store current zoom level
    private var currentZoomLevel: Float = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Initialize views
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            this.googleMap = googleMap
            googleMap.setOnMapClickListener(this) // Set click listener for map
        }

        // Initialize EditTexts and TextViews
        editLocation = findViewById(R.id.editTextText)
        addressEditText = findViewById(R.id.addressEditText)
        text1 = findViewById(R.id.textView)
        text2 = findViewById(R.id.textView2)

        // Handle the Search button click
        val searchButton: Button = findViewById(R.id.button)
        searchButton.setOnClickListener {
            val locationName = editLocation.text.toString()
            // Clear the address EditText before performing the search
            addressEditText.text.clear()
            // Fetch latitude and longitude from the address
            getLocationFromAddress(locationName)
        }

        val fab: FloatingActionButton = findViewById(R.id.floatingActionButton)
        val mapOptionsLayout: LinearLayout = findViewById(R.id.mapOptionsLayout)

        fab.setOnClickListener {
            mapOptionsLayout.visibility =
                if (mapOptionsLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Switch to Satellite view
        satelliteButton = findViewById(R.id.satelliteButton)
        satelliteButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        // Switch to Terrain view
        terrainButton = findViewById(R.id.terrainButton)
        terrainButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }

        // Switch to Hybrid view
        hybridButton = findViewById(R.id.hybridButton)
        hybridButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }

        // Switch to Normal view
        normalButton = findViewById(R.id.normalButton)
        normalButton.setOnClickListener {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

    }

    // Map click listener (reverse geocode the clicked location)
    override fun onMapClick(latLng: LatLng) {
        // Preserve the current zoom level
        currentZoomLevel = googleMap.cameraPosition.zoom
        getAddressFromLatLng(latLng)
    }

    // Fetch latitude and longitude from location name (Geocoding)
    private fun getLocationFromAddress(location: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(location, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                lattitudee = address.latitude
                longitudee = address.longitude

                // Update the map with the new location
                val latLng = LatLng(lattitudee, longitudee)
                googleMap.clear() // Clear any previous markers
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(location)
                ) // Add marker at new location
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        currentZoomLevel // Use preserved zoom level
                    )
                ) // Move camera to the new location

                // Update latitude and longitude on screen
                text1.text = "Latitude: $lattitudee"
                text2.text = "Longitude: $longitudee"

                // Set the address in the EditText
                addressEditText.setText(address.getAddressLine(0))
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch address from latitude and longitude (Reverse Geocoding)
    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(this)
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]

                // Update the address EditText with the fetched address
                addressEditText.setText(address.getAddressLine(0))

                // Update the latitude and longitude on the screen
                text1.text = "Latitude: ${latLng.latitude}"
                text2.text = "Longitude: ${latLng.longitude}"

                // Optionally add a marker on the map
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(address.getAddressLine(0))
                )
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        currentZoomLevel
                    )
                ) // Use preserved zoom level
            } else {
                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show()
        }
    }
}