package com.stevdza.san.mongodemo.screenStudent.parentAssociateOnboarding

import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AssociateParent
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonBinary
import org.mongodb.kbson.BsonBinarySubType
import java.io.ByteArrayOutputStream

class ParentAssociateOnboardingVM(): ViewModel() {

    var objectId = mutableStateOf("")
    var surname = mutableStateOf("")
    var otherNames = mutableStateOf("")
    var phone = mutableStateOf("")
    var relationship = mutableStateOf("")
    var address = mutableStateOf("")
    var gender = mutableStateOf("")
    var familyId = mutableStateOf("")
    var pics = mutableStateOf("")
    var bitmap = mutableStateOf<Bitmap?>(null)


    fun insertParentAssociate(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            pics.value = bitmap.value?.let { bitmapToBase64(bitmap = it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                phone.value.isNotEmpty() && relationship.value.isNotEmpty() &&
                address.value.isNotEmpty() && bitmap.value != null && familyId.value.isNotEmpty()
                && gender.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertAssociateParent(associateParent = AssociateParent().apply {
                            surname = this@ParentAssociateOnboardingVM.surname.value
                            otherNames = this@ParentAssociateOnboardingVM.otherNames.value
                            phone = this@ParentAssociateOnboardingVM.phone.value
                            relationship = this@ParentAssociateOnboardingVM.relationship.value
                            address = this@ParentAssociateOnboardingVM.address.value
                            gender = this@ParentAssociateOnboardingVM.gender.value
                            familyId = this@ParentAssociateOnboardingVM.familyId.value
                            pics = this@ParentAssociateOnboardingVM.pics.value
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


    fun bitmapToByteArray(): BsonBinary {
        // Convert the Bitmap to a byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.value?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        // Create a BsonBinary instance with subtype 0x00 (generic binary data)
        return BsonBinary(BsonBinarySubType.BINARY, byteArray)
    }

}
