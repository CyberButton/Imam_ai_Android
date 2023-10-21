package com.nurtore.imam_ai.ui.homepage

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.schedule.PrayerTime
import com.nurtore.imam_ai.db.schedule.ScheduleDao
import com.nurtore.imam_ai.model.LocationData
import com.nurtore.imam_ai.model.prayerApiResponse.PrayerTimeResponse
import com.nurtore.imam_ai.utils.Constants.Companion.PRAYER_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class HomePageViewModel(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
    private val repo: Repo,
    private val calendar: Calendar,//remove?
    private val dao: ScheduleDao

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

    fun fetchPrayerSchedule() {
        Log.d("TEST PRAYER", "init")
        var time = "not known yet"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TEST PRAYER", "start")
                val response = repo.getPrayerSchedule(PRAYER_URL)
                if (response.isSuccessful && (response.body() != null)) {
                    time = response.body()!!.data[0].timings.fajr
                    Log.d("TEST PRAYER", "Success: $time")
                    updateLocalPrayerSchedule(response.body()!!)
                } else {
                    Log.e("TEST PRAYER", "RESPONSE BODY IS NULL")
                }
            } catch (e: Exception) {
                Log.e("TEST PRAYER",e.toString())
            }
        }
        Log.d("TEST PRAYER", "finished")
    }

    private fun updateLocalPrayerSchedule(newSchedule: PrayerTimeResponse) {
        Log.d("TEST PRAYER", "db populate start")
        viewModelScope.launch(Dispatchers.IO) {
            for (data in newSchedule.data) {
                dao.insertPrayerTimes(PrayerTime(
                    date = data.date.gregorian.date,
                    asr = data.timings.asr,
                    dhuhr = data.timings.dhuhr,
                    fajr = data.timings.fajr,
                    isha = data.timings.isha,
                    maghrib = data.timings.maghrib
                ))
            }
        }
        Log.d("TEST PRAYER", "db populate finished")
    }

}
