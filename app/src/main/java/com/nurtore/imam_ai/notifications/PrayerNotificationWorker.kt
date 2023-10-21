package com.nurtore.imam_ai.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data

class PrayerNotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val prayerName = inputData.getString("prayerName")

        if (prayerName != null) {
            showPrayerNotification(applicationContext, prayerName)
            return Result.success()
        }

        return Result.failure()
    }
}
