package com.stevdza.san.mongodemo.screenStudent.attendanceIn

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentIn
import com.stevdza.san.mongodemo.model.StudentOut
import com.stevdza.san.mongodemo.model.StudentSelectable
import com.stevdza.san.mongodemo.util.Constants.SECOND_TERM
import com.stevdza.san.mongodemo.util.Constants.THIRD_TERM
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class AttendanceInVM(): ViewModel() {

    val objectID = mutableStateOf("")
    val familyId = mutableStateOf("")
    val date = mutableStateOf("3433")

    var studentData = mutableStateOf(emptyList<StudentData>())
    var stu = mutableListOf<StudentSelectable>()
    var getAttendanceIDForSigningOut = mutableStateOf<List<AttendanceStudent>>(emptyList<AttendanceStudent>())
    var stuOut = mutableListOf<StudentSelectable>()


    val term = mutableStateOf(SECOND_TERM)
    val termTest = mutableStateOf(THIRD_TERM)
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

    var students =   mutableStateOf(emptyList<StudentData>())
    var attendanceList = mutableStateOf(emptyList<AttendanceStudent>())
    val dateDelete = mutableStateOf("")

    val sessionTest = mutableStateOf("2022/2023")
    val clessTest = mutableStateOf("Basic One")

    val familyIdTest = mutableStateOf("653768fb89b98e0fb6e50b65")
    val studentNameTest = mutableStateOf("Sanusi Suliyat")
    val studentIdTest = mutableStateOf("AAA/23/004")




    init {
        getAllAttendance()
        getAllStudent()


    }

    var open = MutableLiveData<Boolean>()

    fun startThread() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // Do the background work here
                // I'm adding delay
                delay(3000)
            }

            closeDialog()
        }
    }

     fun closeDialog() {
        open.value = false
    }

     fun getAllAttendance() {
        viewModelScope.launch {
            AlkhairDB.getAttendance().collect {
                attendanceList.value = it
            }
        }
    }


    fun getAllStudent() {
        viewModelScope.launch {
            AlkhairDB.getStudentData().collect {
                students.value = it
            }
        }
    }





    fun insertAttendance(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {


        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    val stuOnlyTrue = stu.filter { it.selected.value }
                    stuOnlyTrue.forEach {
                        if (
                            familyId.value.isNotEmpty() && date.value.isNotEmpty() &&
                            term.value.isNotEmpty() && session.value.isNotEmpty()
                        ) {
                            AlkhairDB.insertAttendance(attendanceModel = AttendanceStudent().apply {
                                familyId = this@AttendanceInVM.familyId.value
                                date = this@AttendanceInVM.date.value
                                term = this@AttendanceInVM.term.value
                                session = this@AttendanceInVM.session.value
                                cless = it.student.cless
                                studentName = "${it.student.surname} ${it.student.otherNames}"
                                studentId = it.student.studentApplicationID
                                studentIn = StudentIn().apply {
                                    studentBroughtBy = this@AttendanceInVM.studentBroughtInBy.value
                                    time = this@AttendanceInVM.timeIn.value
                                }
                                    })
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
                        Log.e(ContentValues.TAG, "insertAdmin: ${e.message}",)
                        onError()
                    }

        }
    }


    fun updateAttendance(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                    val result = withContext(Dispatchers.IO) {
//                        if (objectID.value.isNotEmpty()
//                        ) {
                        val stuOnlyTrue = stuOut.filter { it.selected.value } // Student Selected
                        val studentIdFromSelectedStudent = stuOnlyTrue.map { it.student.studentApplicationID } // Student ID from Selected student
                        val signingOutStudent = getAttendanceIDForSigningOut.value.filter { it.studentId in studentIdFromSelectedStudent }

                        signingOutStudent.forEach {
                            AlkhairDB.updateAttendance(attendanceModel = AttendanceStudent().apply {
                                    _id = BsonObjectId(hexString = it._id.toHexString())
                                    studentOut = StudentOut().apply {
                                        studentBroughtBy = this@AttendanceInVM.studentBroughtOutBy.value
                                        time = this@AttendanceInVM.timeOut.value
                                    }
                                })
                            }
//                        }

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





    fun deleteAttendance1() {
        viewModelScope.launch {
            if (date.value.isNotEmpty()) {
                AlkhairDB.deleteAttendance(date = this@AttendanceInVM.dateDelete.value)
            }
        }
    }




}