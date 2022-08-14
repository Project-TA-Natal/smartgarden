package com.example.aplikasicabai.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "monitoring")
@Parcelize
data class Monitoring(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "kelembapan") var kelembapan: String,
    @ColumnInfo(name = "suhu") var suhu: String,
    @ColumnInfo(name = "ph_tanah") var phTanah: String,
    @ColumnInfo(name = "pompa_air") var pompaAir: Boolean,
    @ColumnInfo(name = "kipas_angin") var kipasAngin: Boolean,
    @ColumnInfo(name = "penetral_ph") var penetralPh: Boolean
) : Parcelable