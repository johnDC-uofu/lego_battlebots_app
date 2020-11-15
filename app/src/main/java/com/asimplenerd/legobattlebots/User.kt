package com.asimplenerd.legobattlebots

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var email: String? = "",
    var password: String? = "",
    var wins: String? = "",
    var losses: String? = ""
)
