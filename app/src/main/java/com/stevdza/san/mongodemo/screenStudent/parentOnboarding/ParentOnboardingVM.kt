package com.stevdza.san.mongodemo.screenStudent.parentOnboarding

import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.Parent
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class ParentOnboardingVM (): ViewModel() {

    var surname = mutableStateOf("")
    var otherNames = mutableStateOf("")
    var phone = mutableStateOf("")
    var relationship = mutableStateOf("")
    var address = mutableStateOf("")
    var owner_id = mutableStateOf("")
    var gender = mutableStateOf("")
    var pics = mutableStateOf("")
    var bitmap = mutableStateOf<Bitmap?>(null)






    fun insertParentPass(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            pics.value = bitmap.value?.let { bitmapToBase64(it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                phone.value.isNotEmpty() && relationship.value.isNotEmpty() &&
                address.value.isNotEmpty() && bitmap.value != null
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {

                        AlkhairDB.insertParent(parent = Parent().apply {
                            surname = this@ParentOnboardingVM.surname.value
                            otherNames = this@ParentOnboardingVM.otherNames.value
                            phone = this@ParentOnboardingVM.phone.value
                            relationship = this@ParentOnboardingVM.relationship.value
                            address = this@ParentOnboardingVM.address.value
                            gender = this@ParentOnboardingVM.gender.value
                            pics = this@ParentOnboardingVM.pics.value
                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "insertAdmin: ${e.message}",)
                    onError()
                }

            }
        }
    }


    fun bitmapToByteArray(): ByteArray {
        // Convert the Bitmap to a byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.value?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()

        // Create a BsonBinary instance with subtype 0x00 (generic binary data)
//        return BsonBinary(BsonBinarySubType.BINARY, byteArray)
    }

}
