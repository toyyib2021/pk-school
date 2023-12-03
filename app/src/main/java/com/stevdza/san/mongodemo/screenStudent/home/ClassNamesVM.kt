package com.stevdza.san.mongodemo.screenStudent.home

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.ClassName
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class ClassNamesVM (): ViewModel() {

    var classNames = mutableStateOf(emptyList<ClassName>())
    var names = mutableStateOf("")
    var objectId = mutableStateOf("")

    init {
        getAllClasses()
    }

     fun getAllClasses() {
        viewModelScope.launch {
            AlkhairDB.getClassNames().collect {
                classNames.value = it
            }
        }
    }


    fun insertClassName(
        onError:()-> Unit,
        onSuccess:()-> Unit,
        emptyFeild:()-> Unit,
    ) {
        viewModelScope.launch {
            if (names.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertClassNames(className = ClassName().apply {
                            className = this@ClassNamesVM.names.value

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

            }else{
                emptyFeild()
            }
        }
    }


    fun updateClassNames(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,

    ) {
        viewModelScope.launch {
            if (names.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateClassNames(className = ClassName().apply {
                            _id = BsonObjectId(hexString = this@ClassNamesVM.objectId.value)
                            className = this@ClassNamesVM.names.value

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


    fun deleteClassName(
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
                                AlkhairDB.deleteClassNames(id = BsonObjectId(hexString = objectId.value))
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