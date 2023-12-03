package com.stevdza.san.mongodemo.screenTeacher.dashboard

import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.model.TeacherAbsentAttendance
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StaffDashboardVM (): ViewModel() {

    val staffId = mutableStateOf("")
    var teacherData = mutableStateOf(emptyList<TeacherProfile>())
    var teacherAttendanceData = mutableStateOf(emptyList<StaffAttendance>())
    var absentStaff = mutableStateOf(emptyList<TeacherAbsentAttendance>())


    init {

        getAllTeacher()
        getAllTeacherAttendance()
        getAllAbsentTeacherAttendance()
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

    fun getAllAbsentTeacherAttendance() {
        viewModelScope.launch {
            AlkhairDB.getTeacherAbsentData().collect {
                absentStaff.value = it
            }
        }
    }

}