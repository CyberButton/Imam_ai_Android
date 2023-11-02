package com.nurtore.imam_ai.ui.kibla

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.nurtore.imam_ai.db.schedule.PrayerTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class KiblaSearchViewModel(context: Context) : ViewModel(), SensorEventListener {

//    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var currentAltitude: Double = 0.0

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    private val _rotationDegree = MutableLiveData<Float>()
    val rotationDegree: LiveData<Float> get() = _rotationDegree

    val hasLocation = MutableLiveData<Boolean>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // INCORRECT, WILL ONLY WORK FROM SECOND LAUCH
    init {
        initializeCompass(context)
//        val location: Location? = if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("KIBLA-LOCATION", "ACCESS PROVIDED")
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        } else {
//            //requestLocationPermission(context)
//            Log.d("KIBLA-LOCATION", "ACCESS DENIED")
//            null
//        }
//        Log.d("KIBLA-LOCATION", location.toString())
//        if (location != null) {
//
//            hasLocation.postValue(true)
//
//            sensorManager.registerListener(this, accelerometer, 100000)
//            sensorManager.registerListener(this, magnetometer, 100000)
//
//            currentLatitude = location.latitude
//            currentLongitude = location.longitude
//            currentAltitude = location.altitude
//        } else {
//            hasLocation.postValue(false)
//        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        viewModelScope.launch(Dispatchers.Default) {
            when(event?.sensor?.type) {
                Sensor.TYPE_ACCELEROMETER -> gravity = lowPass(event.values, gravity)
                Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = lowPass(event.values, geomagnetic)
            }
            val azimuth = calculateAzimuth()
            _rotationDegree.postValue(azimuth)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
           Log.d("COMPASS", "LOW ACCURACY")
        }
    }

    private fun getCurrentLocation() {

        val locationTask: Task<Location> = fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY, null)
        locationTask.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations, this can be null.
            location?.let {
                // Use the current location here
                currentLatitude = it.latitude
                currentLongitude = it.longitude
                currentAltitude = it.altitude
                hasLocation.postValue(true)
                _isLoading.postValue(false)
            } ?: run {
                // Handle location being null here
                hasLocation.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    fun initializeCompass(context: Context) {
        _isLoading.postValue(true)
        sensorManager.unregisterListener(this)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("KIBLA-LOCATION", "ACCESS PROVIDED")
            getCurrentLocation()
        } else {
            //requestLocationPermission(context)
            Log.d("KIBLA-LOCATION", "ACCESS DENIED")
            _isLoading.postValue(false)
        }
    }

    private fun getTrueNorthCorrection(azimuth: Float, latitude: Double, longitude: Double, altitude: Double): Float {
        val geomagneticField = GeomagneticField(
            latitude.toFloat(),
            longitude.toFloat(),
            altitude.toFloat(),
            System.currentTimeMillis()
        )

        val declination = geomagneticField.declination
        return azimuth + declination
    }

    private fun calculateQiblaDirection(yourLatitude: Double, yourLongitude: Double): Float {
        val kaabaLatitude = 21.4225
        val kaabaLongitude = 39.8262

        val direction = atan2(
            sin(Math.toRadians(kaabaLongitude - yourLongitude)),
            cos(Math.toRadians(yourLatitude)) * tan(Math.toRadians(kaabaLatitude)) -
                    sin(Math.toRadians(yourLatitude)) * cos(Math.toRadians(kaabaLongitude - yourLongitude))
        )

        return Math.toDegrees(direction).toFloat()
    }

    private fun calculateAzimuth(): Float {
        if (gravity != null && geomagnetic != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                val magneticAzimuth = (-orientation[0] * 180f / Math.PI).toFloat() // This is the direction of magnetic north

                // Correct the azimuth to get true north direction
                val trueAzimuth = getTrueNorthCorrection(magneticAzimuth, currentLatitude, currentLongitude, currentAltitude)

                // Calculate Qibla direction from true north
                val qiblaDirectionFromNorth = calculateQiblaDirection(currentLatitude, currentLongitude)

                // Adjust azimuth to always point towards Qibla
                val adjustedAzimuth = trueAzimuth + qiblaDirectionFromNorth

                return adjustedAzimuth
            }
        }
        return 0f
    }

    private fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input

        for (i in input.indices) {
            output[i] = output[i] + 0.1f * (input[i] - output[i])
        }
        return output
    }

    private fun requestLocationPermission(activity: Context) {
        ActivityCompat.requestPermissions(
            activity as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1002
    }


    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
