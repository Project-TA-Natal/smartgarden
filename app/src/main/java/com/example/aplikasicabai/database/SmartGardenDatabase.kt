package com.example.aplikasicabai.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplikasicabai.dao.MonitoringConfigDao
import com.example.aplikasicabai.dao.MonitoringDao
import com.example.aplikasicabai.model.Monitoring
import com.example.aplikasicabai.model.MonitoringConfig

@Database(entities = [Monitoring::class, MonitoringConfig::class], version = 1)
abstract class SmartGardenDatabase: RoomDatabase() {
    abstract fun monitoringDao(): MonitoringDao
    abstract fun monitoringConfigDao(): MonitoringConfigDao

    companion object {
        private var INSTANCE: SmartGardenDatabase? = null

        fun getInstance(context: Context): SmartGardenDatabase? {
            if (INSTANCE == null) {
                synchronized(SmartGardenDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        SmartGardenDatabase::class.java, "SmartGarden")
                        .build()
                }
            }
            return  INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}