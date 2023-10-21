package com.nurtore.imam_ai.ui.homepage

import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.schedule.ScheduleDao
import java.util.Calendar

class HomePageViewModelFactory(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
    private val repo: Repo,
    private val calendar: Calendar,
    private val dao: ScheduleDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomePageViewModel(fusedLocationClient, geocoder, repo, calendar, dao) as T
    }
}