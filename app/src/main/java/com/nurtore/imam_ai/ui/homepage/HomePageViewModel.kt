package com.nurtore.imam_ai.ui.homepage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.GoogleApi.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.schedule.PrayerTime
import com.nurtore.imam_ai.db.schedule.ScheduleDao
import com.nurtore.imam_ai.model.LocationData
import com.nurtore.imam_ai.model.prayerApiResponse.PrayerTimeResponse
import com.nurtore.imam_ai.utils.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class HomePageViewModel(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
    private val repo: Repo,
    private val calendar: Calendar,//remove?
    private val dao: ScheduleDao,
    private val prefs: SharedPrefs,


) : ViewModel() {

    private val _locationData = MutableLiveData<LocationData>()
    val locationData: LiveData<LocationData> get() = _locationData

    private val _currentPrayerTimes = MutableLiveData<PrayerTime>()
    val currentPrayerTimes: LiveData<PrayerTime> get() = _currentPrayerTimes

    fun loadCurrentPrayerTimes() {
        Log.d("TEST PRAYER", "db read...")
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        viewModelScope.launch(Dispatchers.IO) {
            Thread.sleep(1000)
        // check if dao.getPrayerTimesByDate(currentDate.toString()) is not null or empty
            _currentPrayerTimes.postValue(dao.getPrayerTimesByDate(currentDate.toString()))
        }
        Log.d("TEST PRAYER", "db read finished")
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

        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!

        Log.d("TEST PRAYER", "lat = ${location.latitude} long = ${location.longitude}")

        val city = addresses[0].locality
        val country = addresses[0].countryName

        prefs.locationCity = city
        prefs.locationCountry = country

        prefs.latitude = location.latitude.toString()
        prefs.longitude = location.longitude.toString()

        _locationData.value = LocationData(country, city)
    }

    fun fetchPrayerSchedule() {
        Log.d("TEST PRAYER", "api call init")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TEST PRAYER", "api call start")
                val url = getPrayerUrl()
                Log.d("TEST PRAYER", url)
                val response = repo.getPrayerSchedule(url)
                if (response.isSuccessful && (response.body() != null)) {
                    Log.d("TEST PRAYER", "api call Success")
                    Log.d("TEST PRAYER", response.body()!!.data[0].date.gregorian.date)
                    updateLocalPrayerSchedule(response.body()!!)
                } else {
                    Log.e("TEST PRAYER", "api call RESPONSE BODY IS NULL")
                }
            } catch (e: Exception) {
                Log.e("TEST PRAYER",e.toString())
            }
        }
        Log.d("TEST PRAYER", "api call finished")
    }

    private fun updateLocalPrayerSchedule(newSchedule: PrayerTimeResponse) {
        Log.d("TEST PRAYER", "db populate start")
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAllPrayerTimes()
            for (data in newSchedule.data) {
                dao.insertPrayerTimes(PrayerTime(
                    date = data.date.gregorian.date,
                    asr = data.timings.asr.substring(0,5),
                    dhuhr = data.timings.dhuhr.substring(0,5),
                    fajr = data.timings.fajr.substring(0,5),
                    isha = data.timings.isha.substring(0,5),
                    maghrib = data.timings.maghrib.substring(0,5),
                    sunrise = data.timings.sunrise.substring(0,5)
                ))
            }
        }
        Log.d("TEST PRAYER", "db populate finished")

        loadCurrentPrayerTimes()

    }

    private fun getPrayerUrl(): String {

        requestLocationUpdates()

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
        val method = prefs.calculationType!!
//        val latitude = prefs.latitude
//        val longitude = prefs.longitude
        val address = "${prefs.locationCity!!},${prefs.locationCountry!!}"
//        "Sultanahmet%20Mosque,%20Istanbul,%20Turkey"

        Log.d("TEST PRAYER", "$year/$month?address=$address&method=$method")

//        Log.d("TEST PRAYER", "$year/$month?latitude=$latitude&longitude=$longitude&method=$method")
//        return "https://api.aladhan.com/v1/calendar/$year/$month?latitude=$latitude&longitude=$longitude&method=$method"
        return "https://api.aladhan.com/v1/calendarByAddress/$year/$month?address=$address&method=$method"
    }


}
