package com.stevdza.san.mongodemo.screenTeacher.siginAndOut

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class SignInAndOutVM(): ViewModel() {

    var objectid = mutableStateOf("")
    var objectIdEdit = mutableStateOf("")
    var name = mutableStateOf("")
    var staffId = mutableStateOf("")
    var position = mutableStateOf("")
    var month = mutableStateOf("")
    var session = mutableStateOf("")
    var term = mutableStateOf("")
    var timeIn = mutableStateOf("")
    var timeOut = mutableStateOf("")
    var date = mutableStateOf("")


    var teacherData = mutableStateOf(emptyList<TeacherProfile>())
    var teacherAttendanceData = mutableStateOf(emptyList<StaffAttendance>())


    init {
        getAllTeacher()
        getAllTeacherAttendance()
    }

    fun getAllTeacher() {
        viewModelScope.launch {
            AlkhairDB.getTeacherData().collect {
                teacherData.value = it
            }
        }
    }

    fun getAllTeacherAttendance() {
        viewModelScope.launch {
            AlkhairDB.getTeacherAttendanceData().collect {
                teacherAttendanceData.value = it
            }
        }
    }

    fun signInStaff(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            if (
                name.value.isNotEmpty()  && position.value.isNotEmpty() && month.value.isNotEmpty()
                && session.value.isNotEmpty() && term.value.isNotEmpty() && timeIn.value.isNotEmpty()
                && date.value.isNotEmpty() && staffId.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertTeacherAttendance(teacherAttendance = StaffAttendance().apply {
                            name = this@SignInAndOutVM.name.value
                            staffId = this@SignInAndOutVM.staffId.value
                            position = this@SignInAndOutVM.position.value
                            month = this@SignInAndOutVM.month.value
                            session = this@SignInAndOutVM.session.value
                            term = this@SignInAndOutVM.term.value
                            timeIn = this@SignInAndOutVM.timeIn.value
                            date = this@SignInAndOutVM.date.value

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


    fun signOutStaff(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectIdEdit.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateTeacherAttendance(teacherAttendance = StaffAttendance().apply {
                            _id = BsonObjectId(hexString = objectIdEdit.value)
                            timeOut = this@SignInAndOutVM.timeOut.value
                        })
                    }
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