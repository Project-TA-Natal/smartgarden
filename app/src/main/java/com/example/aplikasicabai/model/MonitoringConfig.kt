package com.example.aplikasicabai.model

data class MonitoringConfig(
    var batasBawahKelembapan: Number ?= null,
    var batasAtasKelembapan: Number ?= null,
    var batasBawahSuhu: Number ?= null,
    var batasAtasSuhu: Number ?= null,
    var batasBawahPh: Number ?= null,
    var batasAtasPh: Number ?= null,
    var batasMinimalPh: Number ?= null,
    var batasMaximalPh: Number ?= null
)