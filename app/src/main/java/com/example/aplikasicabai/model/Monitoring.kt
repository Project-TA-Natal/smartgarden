package com.example.aplikasicabai.model

data class Monitoring(
    var kelembapan: Number ?= null,
    var suhu: Number ?= null,
    var phTanah: Number ?= null,
    var pompaAir: Boolean ?= null,
    var kipasAngin: Boolean ?= null,
    var penetralPh: Boolean ?= null
)