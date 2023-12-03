package com.stevdza.san.mongodemo.screenStudent.oldStudentScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.StudentData
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonBinary


class OldStudentVM (): ViewModel() {


    var studentData = mutableStateOf(emptyList<StudentData>())
    var attendanceData = mutableStateOf(emptyList<AttendanceStudent>())

    init {
        viewModelScope.launch {
            AlkhairDB.getStudentData().collect {
                studentData.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.getAttendance().collect {
                attendanceData.value = it
            }
        }
    }

    fun byteArrayToBitmap(bin: BsonBinary): Bitmap? {
        val byteArray = bin.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }



}