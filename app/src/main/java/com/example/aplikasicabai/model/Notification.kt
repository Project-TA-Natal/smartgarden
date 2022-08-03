package com.example.aplikasicabai.model

data class Notification(
    var id: String ?= null,
    var message: String ?= null,
    var date: String ?= null,
    var isRead: Boolean ?= null,
)