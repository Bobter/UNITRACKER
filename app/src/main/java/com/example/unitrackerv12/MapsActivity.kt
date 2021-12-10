package com.example.unitrackerv12

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.unitrackerv12.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import com.example.unitrackerv12.Group
import com.example.unitrackerv12.User
import com.example.unitrackerv12.Position



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var databaseRef: DatabaseReference

    //PEDIR PERMISOS
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap.isMyLocationEnabled = true
                getLocationAccess()
            }
            else {
                Toast.makeText(this, "User has not granted location access permission", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    
    //COMPROBAR PERMISOS
    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //
        databaseRef = Firebase.database.reference
        databaseRef.addValueEventListener(logListener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //
        mMap.uiSettings.isZoomControlsEnabled = true

        getLocationAccess()
        //createMarker()
    }

    fun createMarker(){
        val ZoomLevel = 15f
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
            ?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,ZoomLevel))
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    //FIREBASE: Enviar datos a la bd
    private fun getLocationUpdates() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        //-------------------------------FIREBASE---------------------------------------------
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()){
                    val location = locationResult.lastLocation
                    var position = Position(location.longitude, location.latitude)

                    // ONLY FOR TEST - REMOVE THIS LINE WHEN SignIn is implemented
                    auth.signInWithEmailAndPassword("test@example.com", "password")

                    val user: User = User.getCurrentUser()
                    user.addPosition(position)


                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                        mMap.addMarker(markerOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }

                /*
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    lateinit var databaseRef: DatabaseReference
                    databaseRef = FirebaseDatabase.getInstance().getReference("uLocation")
                    val uLocation = UserLocation(location.latitude, location.longitude)
                    databaseRef.child("uLocation").child("userlocation").setValue(uLocation)
                        .addOnSuccessListener {
                            Toast.makeText(applicationContext, "Guardado", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                        }


                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                        mMap.addMarker(markerOptions)
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
                 */
            }
        }
    }

    //FIREBASE: Pedir los datos y error en leer bdFirebase
    val logListener = object : ValueEventListener {
        //FIREBASE: Pedir datos a la bd
        //val user: User = User.getCurrentUser()

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                //MODIFICAR referencia en bdFirebase
                val ulocation = dataSnapshot.child("userlocation").getValue(bdLocation::class.java)
                var Lat=ulocation?.Latitude
                var Long=ulocation?.Longitude
                var uName=ulocation?.uName

                if (Lat !=null  && Long != null) {
                    val Loc = LatLng(Lat, Long)

                    val markerOptions = MarkerOptions().position(Loc).title(uName)
                    mMap.addMarker(markerOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 10f))
                    Toast.makeText(applicationContext, "Usuario encontrado", Toast.LENGTH_LONG).show()
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(applicationContext, "Error al leer datos", Toast.LENGTH_LONG)
                .show()
        }
    }

}
