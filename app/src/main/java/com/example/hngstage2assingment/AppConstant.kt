package com.example.hngstage2assingment

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView
import coil.load
import pub.devrel.easypermissions.EasyPermissions

object AppConstant {

    const val STORAGE_PERM_REQUEST_CODE = 111
    const val CAMERA_PERM_REQUEST_CODE = 112

    fun getImageLoader(imageView: ImageView, imgBitmap: Bitmap) {
        imageView.load(imgBitmap) {
            crossfade(true)
            crossfade(1000)
            placeholder(R.drawable.image_loader_place_holder)
        }
    }

    fun checkForPermission(context: Context, perms: String) = EasyPermissions.hasPermissions(context, perms)
    fun requestForPermission(host: Activity, rationale: String, requestCode: Int, perms: String) {
        EasyPermissions.requestPermissions(host, rationale, requestCode, perms)
    }

    fun validateInputs(firstName: String, lastName: String, profilePicture: Bitmap) =
        !(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && profilePicture.byteCount > 0)
}
