package com.stevdza.san.mongodemo.screenStudent.dailyAttendance

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentIn
import com.stevdza.san.mongodemo.model.StudentOut
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonBinary

class DailyAttendanceVM () : ViewModel() {

    val objectID = mutableStateOf("")
    val familyId = mutableStateOf("")
    val date = mutableStateOf("")
    val term = mutableStateOf("")
    val session = mutableStateOf("")
    val cless = mutableStateOf("")
    var studentName = mutableStateOf("")
    var studentId = mutableStateOf("")
    val studentIn = mutableStateOf<StudentIn?>(null)
    val studentOut = mutableStateOf<StudentOut?>(null)


    var studentBroughtInBy = mutableStateOf("")
    var timeIn = mutableStateOf("")

    var studentBroughtOutBy = mutableStateOf("")
    var timeOut = mutableStateOf("")

    var students = mutableStateOf(emptyList<StudentData>())
    var attendanceList = mutableStateOf(emptyList<AttendanceStudent>())


    init {
        viewModelScope.launch {
            AlkhairDB.getAttendance().collect{
                attendanceList.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.getStudentData().collect{
                students.value = it
            }
        }
    }

    fun byteArrayToBitmap(bin: BsonBinary): Bitmap? {
        val byteArray = bin.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


}

