package com.nurtore.imam_ai.utils.permissions

import android.Manifest
import android.os.PowerManager
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionsViewmodel: ViewModel() {

    private val _currentPermissionsStatus = MutableLiveData<ImamAiPermissions>()
    val currentPermissionsStatus: LiveData<ImamAiPermissions> get() = _currentPermissionsStatus

    init {

    }

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

}

class ImamAiPermissions(
    val locationPermission: Boolean?,
    val notificationPermission: Boolean?,
    val batteryPermission: Boolean?,
    val locationPermissionCode: String = Manifest.permission.ACCESS_FINE_LOCATION,
    val notificationPermissionCode: String = Manifest.permission.POST_NOTIFICATIONS,
    val batteryPermissionCode: String = Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    ) {
    init {

    }
}