package com.stevdza.san.mongodemo.screenTeacher.timeInAndOut

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.TimeInOut
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class SetTimeInAndOutVM (): ViewModel() {


    var timeIn = mutableStateOf("")
    var timeOut = mutableStateOf("")
    var setTimeInOut = mutableStateOf<TimeInOut?>(null)
    var objectId = mutableStateOf("")
    var data = mutableStateOf(emptyList<TimeInOut>())


    init {

        viewModelScope.launch {
            AlkhairDB.getTimeInOutData().collect {
                data.value = it
            }
        }

    }

    fun getTimeInOutWithId() {
        viewModelScope.launch {
            AlkhairDB.getTimeINOutWithID(_id = BsonObjectId(hexString = objectId.value)).let {
                setTimeInOut.value = it
            }
        }
    }


    fun updateTimeIn(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,

        ) {
        viewModelScope.launch {
            if (objectId.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateTimeIn(timeInOut = TimeInOut().apply {
                            _id = BsonObjectId(hexString = this@SetTimeInAndOutVM.objectId.value)
                            timeIn = this@SetTimeInAndOutVM.timeIn.value

                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }
    }


    fun updateTimeOut(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,

        ) {
        viewModelScope.launch {
            if (objectId.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateTimeOut(timeInOut = TimeInOut().apply {
                            _id = BsonObjectId(hexString = this@SetTimeInAndOutVM.objectId.value)
                            timeOut = this@SetTimeInAndOutVM.timeOut.value

                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }
    }



    fun deleteTerm(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,

    ) {
        viewModelScope.launch {
            if (objectId.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        viewModelScope.launch {
                            if (objectId.value.isNotEmpty()) {
                                AlkhairDB.deleteTimeInOut(id = BsonObjectId(hexString = objectId.value))
                            }
                        }
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }

    }


}