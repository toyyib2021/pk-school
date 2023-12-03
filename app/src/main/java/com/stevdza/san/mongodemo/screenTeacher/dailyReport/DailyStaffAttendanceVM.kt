package com.stevdza.san.mongodemo.screenTeacher.dailyReport

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentIn
import com.stevdza.san.mongodemo.model.StudentOut
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.TeacherAbsentAttendance
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonBinary
import org.mongodb.kbson.BsonObjectId

class DailyStaffAttendanceVM () : ViewModel() {

    val objectID = mutableStateOf("")
    val staffId = mutableStateOf("")
    val name = mutableStateOf("")
    val position = mutableStateOf("")
    val date = mutableStateOf("")
    val term = mutableStateOf("")
    var session = mutableStateOf("")
    val month = mutableStateOf("")
    val reason = mutableStateOf("")

    val attendanceSearchName = mutableStateOf("")
    val absentNameSearch = mutableStateOf("")

    var studentBroughtInBy = mutableStateOf("")
    var timeIn = mutableStateOf("")

    var studentBroughtOutBy = mutableStateOf("")
    var timeOut = mutableStateOf("")

    var staffs = mutableStateOf(emptyList<TeacherProfile>())
    var staffAttendance = mutableStateOf(emptyList<StaffAttendance>())
    var absentStaff = mutableStateOf(emptyList<TeacherAbsentAttendance>())




    init {
        viewModelScope.launch {
            AlkhairDB.getTeacherAttendanceData().collect{
                staffAttendance.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.getTeacherData().collect{
                staffs.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.getTeacherAbsentData().collect{
                absentStaff.value = it
            }
        }
    }


    fun getStaffWithSurname() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getTeacherWithSurname(surname =absentNameSearch.value ).collect {
                    staffs.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting Member with Id: $e",)
            }
        }
    }



    fun getStaffWithName() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getStaffWithName(name =attendanceSearchName.value ).collect {
                    staffAttendance.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting Member with Id: $e",)
            }
        }
    }



    fun insertAbsentStaff(
        onError:()-> Unit,
        onSuccess:()-> Unit,
        emptyFeilds:()-> Unit,
    ) {
        viewModelScope.launch {
            if(
                name.value.isNotEmpty() && position.value.isNotEmpty() && date.value.isNotEmpty()
                && month.value.isNotEmpty() && session.value.isNotEmpty()
                && reason.value.isNotEmpty()
            ){
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertTeacherAbsent(teacherAbsentAttendance = TeacherAbsentAttendance().apply {
                            name =this@DailyStaffAttendanceVM.name.value
                            position =this@DailyStaffAttendanceVM.position.value
                            date = this@DailyStaffAttendanceVM.date.value
                            month =this@DailyStaffAttendanceVM.month.value
                            term = this@DailyStaffAttendanceVM.term.value
                            staffId = this@DailyStaffAttendanceVM.staffId.value
                            session = this@DailyStaffAttendanceVM.session.value
                            reason = this@DailyStaffAttendanceVM.reason.value
                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "insertStudent: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeilds()
            }
        }
    }


    fun deleteAbsentTeacher(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,
    ) {
        viewModelScope.launch {
            if (objectID.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        viewModelScope.launch {
                            if (objectID.value.isNotEmpty()) {
                                AlkhairDB.deleteTeacherAbsent(id = BsonObjectId(hexString = objectID.value))
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


    fun updateStudentStatus(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectID.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateTeacherAbsent(teacherAbsentAttendance = TeacherAbsentAttendance().apply {
                            _id = BsonObjectId(hexString = objectID.value)
                            reason = this@DailyStaffAttendanceVM.reason.value
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


    fun byteArrayToBitmap(bin: BsonBinary): Bitmap? {
        val byteArray = bin.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


}

