package com.stevdza.san.mongodemo.screenStudent.parentView

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.StudentData
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonBinary


class DashboardVM (): ViewModel() {


    var objectId = mutableStateOf("")
    var familyID = mutableStateOf("")
    var studentName = mutableStateOf("")
    var pics = mutableStateOf<Bitmap?>(null)
    var byte = mutableStateOf<BsonBinary?>(null)
    var date = mutableStateOf("")
    var student = mutableStateOf<StudentData?>(null)
    var studentList = mutableStateOf(emptyList<StudentData>())
    var studentListForEachFamily = mutableStateOf(emptyList<StudentData>())
    var attendanceList = mutableStateOf(emptyList<AttendanceStudent>())


    var studentID = mutableStateOf("")
    var timeIn = mutableStateOf<String?>("")
    var timeOut = mutableStateOf<String?>("")


    init {

        viewModelScope.launch {
            AlkhairDB.getAttendance().collect() {
                attendanceList.value = it
            }
        }
        viewModelScope.launch {
            AlkhairDB.getStudentData().collect() {
                studentList.value = it
            }
        }

    }

    fun getStudent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getStudentData().collect() {
                    studentList.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
            }


        }
    }

    fun getAttendance() {
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    AlkhairDB.getAttendance().collect() {
                            attendanceList.value = it
                        }
                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
                }


        }
    }

    fun getWIthFamilyID() {
        viewModelScope.launch(Dispatchers.IO) {
            if (familyID.value.isNotEmpty()) {
                try {
                    AlkhairDB.getStudentWithFamilyID(familyId = familyID.value)
                        .collect() {
                            studentListForEachFamily.value = it
                        }
                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
                }
            }

        }
    }

    fun byteArrayToBitmap(bin: BsonBinary): Bitmap? {
        val byteArray = bin.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

}