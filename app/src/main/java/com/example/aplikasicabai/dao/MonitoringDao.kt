package com.example.aplikasicabai.dao

import androidx.room.*
import com.example.aplikasicabai.model.Monitoring
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface MonitoringDao {
    @Query("SELECT * FROM monitoring where id=:id")
    fun getDetailMonitoring(id: Int): Monitoring

    @Insert(onConflict = REPLACE)
    fun createMonitoring(monitoring: Monitoring): Long

    @Update
    fun updateMitoring(monitoring: Monitoring): Int

    @Delete
    fun deleteMonitoring(monitoring: Monitoring): Int

}