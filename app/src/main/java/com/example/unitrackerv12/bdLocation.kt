package com.example.unitrackerv12

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class bdLocation(
    var uName: String?,
    var Latitude: Double? = 0.0,
    var Longitude: Double? = 0.0
)