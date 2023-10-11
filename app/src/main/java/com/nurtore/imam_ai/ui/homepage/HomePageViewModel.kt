package com.nurtore.imam_ai.ui.homepage

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nurtore.imam_ai.model.LocationData
import java.util.Locale

class HomePageViewModel (
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) : ViewModel() {
    private val _locationData = MutableLiveData<LocationData>()
    val locationData: LiveData<LocationData> get() = _locationData

    //private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    init {
        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        // Check and request permissions...
        // ...

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    getLocationData(it)
                }
            }
    }

    private fun getLocationData(location: Location) {
        //val geocoder = Geocoder(context, Locale.getDefault())

//        for future updates:
//        geocoder.getFromLocation(latitude,longitude,maxResult,object : Geocoder.GeocodeListener{
//            override fun onGeocode(addresses: MutableList<Address>) {
//                // code
//            }
//            override fun onError(errorMessage: String?) {
//                super.onError(errorMessage)
//            }
//        })
        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
        val city = addresses[0].locality
        val country = addresses[0].countryName
        _locationData.value = LocationData(country, city)
    }
}
