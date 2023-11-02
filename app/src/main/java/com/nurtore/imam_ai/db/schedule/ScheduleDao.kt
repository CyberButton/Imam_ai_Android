package com.nurtore.imam_ai.db.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM prayer_times WHERE date = :date")
    suspend fun getPrayerTimesByDate(date: String): PrayerTime

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(prayerTimes: PrayerTime)

    @Query("DELETE FROM prayer_times")
    suspend fun deleteAllPrayerTimes()

}