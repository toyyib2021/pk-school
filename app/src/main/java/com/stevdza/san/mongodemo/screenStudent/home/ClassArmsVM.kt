package com.stevdza.san.mongodemo.screenStudent.home

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.ClassArms
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class ClassArmsVM (): ViewModel() {

    var armsNames = mutableStateOf(emptyList<ClassArms>())
    var arms = mutableStateOf("")
    var objectId = mutableStateOf("")

    init {
        getAllClasses()
    }

     fun getAllClasses() {
        viewModelScope.launch {
            AlkhairDB.getClassArms().collect {
                armsNames.value = it
            }
        }
    }


    fun insertArmsName(
        onError:()-> Unit,
        onSuccess:()-> Unit,
        emptyFeild:()-> Unit,
    ) {
        viewModelScope.launch {
            if (arms.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertClassArms(classArms = ClassArms().apply {
                            armsName = this@ClassArmsVM.arms.value

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
            if (arms.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateClassArms(classArms = ClassArms().apply {
                            _id = BsonObjectId(hexString = this@ClassArmsVM.objectId.value)
                            armsName = this@ClassArmsVM.arms.value

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
                                AlkhairDB.deleteClassArms(id = BsonObjectId(hexString = objectId.value))
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