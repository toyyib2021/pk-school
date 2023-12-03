package com.stevdza.san.mongodemo.util.conversion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun convertBase64ToBitmap(base64String: String): Bitmap {

    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}