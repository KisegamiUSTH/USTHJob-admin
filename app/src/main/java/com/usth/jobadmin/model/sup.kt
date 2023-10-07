package com.usth.jobadmin.model

import android.os.Parcelable
import com.usth.jobadmin.util.Constants.Companion.ROLE_TYPE_SUP
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sup(
    var uid : String = "",
    var email : String = "",
    var username : String = "",
    var mobile : String = "",
    var dob : String = "",
    var gender : String = "",
    var imageUri : String = "",
    var stream : String = "",
    var qualification : String = "",
    var experience : String = "",
    var biography : String = "",
    val roleType : String = ROLE_TYPE_SUP
): Parcelable