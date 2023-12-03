package com.stevdza.san.mongodemo.screenStudent.onboardedParent

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.Parent
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonBinary
import org.mongodb.kbson.BsonObjectId

class OnBoardedParentVM () : ViewModel() {

    var parentData = mutableStateOf(emptyList<Parent>())
    var parentData2 = mutableStateOf(emptyList<Parent>())
    var surname = mutableStateOf("")
    var objectId = mutableStateOf("")
    var parent = mutableStateOf<Parent?>(null)


    init {
        getAllParent()
    }

     fun getAllParent() {
        viewModelScope.launch {
            AlkhairDB.getParentData().collect {
                parentData.value = it
            }
        }
    }


    fun getParentWithName() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getParentWithSurname(surname = surname.value ).collect {
                    parentData2.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting Member with Id: $e",)
            }
        }
    }

    fun getParentWithID() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getParentWithID(_id =BsonObjectId(hexString = objectId.value)).let {
                    parent.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting Member with Id: $e",)
            }
        }
    }



    fun byteArrayToBitmap(binData: BsonBinary): Bitmap? {
        // Get the byte array from the BinData
        val byteArray = binData.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

}