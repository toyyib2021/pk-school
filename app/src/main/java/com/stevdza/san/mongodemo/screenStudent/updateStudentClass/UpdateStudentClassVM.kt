package com.stevdza.san.mongodemo.screenStudent.updateStudentClass

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.StudentData
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UpdateStudentClassVM() : ViewModel() {

    val cless = mutableStateOf("")

    fun insertStudent(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            if (
               cless.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertStudent(student = StudentData().apply {

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
}