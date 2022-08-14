package com.example.aplikasicabai.dao

import androidx.room.*
import com.example.aplikasicabai.model.MonitoringConfig
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface MonitoringConfigDao {
    @Query("SELECT * FROM monitoring_configuration where id=:id")
    fun getDetailMonitoringConfig(id: Int): MonitoringConfig

    @Insert(onConflict = REPLACE)
    fun createMonitoringConfig(monitoringConfig: MonitoringConfig): Long

    @Update
    fun updateMonitoringConfig(monitoringConfig: MonitoringConfig): Int

    @Delete
    fun deleteMonitoringConfig(monitoringConfig: MonitoringConfig): Int
}