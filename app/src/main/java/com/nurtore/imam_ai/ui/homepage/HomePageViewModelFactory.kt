package com.nurtore.imam_ai.ui.homepage

import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient

class HomePageViewModelFactory(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomePageViewModel(fusedLocationClient, geocoder) as T
    }
}