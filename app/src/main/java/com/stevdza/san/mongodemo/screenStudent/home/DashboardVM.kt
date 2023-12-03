package com.stevdza.san.mongodemo.screenStudent.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatus
import kotlinx.coroutines.launch


class DashboardVM () : ViewModel() {

    var students = mutableStateOf(emptyList<StudentData>())
    var attendanceList = mutableStateOf(emptyList<AttendanceStudent>())
    var classNames = mutableStateOf(emptyList<ClassName>())
    var student = mutableStateOf<StudentData?>(null)
    var names = mutableStateOf("")
    var objectId = mutableStateOf("")
    var studentId = mutableStateOf("")

    var surname = mutableStateOf("Toyyib")
    var otherNames = mutableStateOf("Khadija")
    var cless = mutableStateOf("Basic One")
    var arms = mutableStateOf("White")
    var dateOfBirth = mutableStateOf("12/02/2023")
    var gender = mutableStateOf("Female")
    var dateCreated = mutableStateOf("12/02/2023")
    var statusDate = mutableStateOf("12/02/2023")
    var studentApplicationID = mutableStateOf("AIA/23/208")
    var familyId = mutableStateOf("64516262661sd1616s")
    var studentStatus = mutableStateOf("Active")
    var pics = mutableStateOf("")

    init {
        getAllStudent()
        getAttendance()
        getAllClasses()
//        addStudent()
    }

    fun addStudent(){
        viewModelScope.launch {
            AlkhairDB.insertStudent(student = StudentData().apply {
                surname = this@DashboardVM.surname.value
                otherNames = this@DashboardVM.otherNames.value
                cless= this@DashboardVM.cless.value
                gender = this@DashboardVM.gender.value
                familyId = this@DashboardVM.familyId.value
                datOfBirth = this@DashboardVM.dateOfBirth.value
                arms = this@DashboardVM.arms.value
                dateCreated = this@DashboardVM.dateCreated.value
                studentApplicationID = this@DashboardVM.studentApplicationID.value
                pics = this@DashboardVM.pics.value
                studentStatus = StudentStatus().apply {
                    status = this@DashboardVM.studentStatus.value
                    date = this@DashboardVM.statusDate.value
                }
            })
        }
    }


     fun getAllStudent() {
        viewModelScope.launch {
            AlkhairDB.getStudentData().collect {
                students.value = it
            }
        }
    }

    fun getAttendance() {
        viewModelScope.launch {
            AlkhairDB.getAttendance().collect {
                attendanceList.value = it
            }
        }
    }

    fun getAllClasses() {
        viewModelScope.launch {
            AlkhairDB.getClassNames().collect {
                classNames.value = it
            }
        }
    }

    fun getAStudent(){
        AlkhairDB.getStudentWithChildId(studentId = studentId.value).let {
            student.value = it
        }
    }

}