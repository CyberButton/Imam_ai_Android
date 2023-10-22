package com.nurtore.imam_ai.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

fun schedulePrayerNotifications(prayerTimes: Map<String, String>, context:Context) {
//    val currentTime = System.currentTimeMillis()
//
//    for ((prayerName, prayerTime) in prayerTimes) {
//        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
//        val date = sdf.parse(prayerTime)
//        val calendar = Calendar.getInstance().apply {
//            time = date
//            set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
//            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
//            set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
//        }
//
//        val prayerTimeMillis = calendar.timeInMillis
//        val delay = prayerTimeMillis - currentTime
//
//        if (delay > 0) {
//            val inputData = Data.Builder()
//                .putString("prayerName", prayerName)
//                .build()
//
//            val prayerWorkRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
//                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//                .setInputData(inputData)
//                .build()
//
//            WorkManager.getInstance(context).enqueue(prayerWorkRequest)
//        }

//            val inputData = Data.Builder()
//                .putString("test", "NURTORE")
//                .build()
//
//                val prayerWorkRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
//                .setInitialDelay(5, TimeUnit.SECONDS)
//                //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .setInputData(inputData)
//                .build()

    //}
}
