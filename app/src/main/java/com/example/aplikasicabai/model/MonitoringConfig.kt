package com.example.aplikasicabai.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "monitoring_configuration")
@Parcelize
data class MonitoringConfig(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "bb_kelembapan")
    var batasBawahKelembapan: String,

    @ColumnInfo(name = "ba_kelembapan")
    var batasAtasKelembapan: String,

    @ColumnInfo(name = "bb_suhu")
    var batasBawahSuhu: String,

    @ColumnInfo(name = "ba_suhu")
    var batasAtasSuhu: String,

    @ColumnInfo(name = "bb_ph")
    var batasBawahPh: String,

    @ColumnInfo(name = "ba_ph")
    var batasAtasPh: String,

    @ColumnInfo(name = "bmin_ph")
    var batasMinimalPh: String,

    @ColumnInfo(name = "bmax_ph")
    var batasMaximalPh: String
) : Parcelable