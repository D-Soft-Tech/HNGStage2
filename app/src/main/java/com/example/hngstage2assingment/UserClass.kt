package com.example.hngstage2assingment

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserClass(
    var firstName: String,
    var lastName: String,
    var profilePic: Bitmap
) : Parcelable
