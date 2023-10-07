package com.usth.jobadmin.model

import com.usth.jobadmin.util.Constants.Companion.ROLE_TYPE_ADMIN

data class Admin(
    var uid : String = "",
    var email : String = "",
    var username : String = "",
    var imageUrl : String = "",
    val roleType : String = ROLE_TYPE_ADMIN
)