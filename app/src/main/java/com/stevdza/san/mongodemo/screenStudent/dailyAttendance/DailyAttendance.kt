package com.stevdza.san.mongodemo.screenStudent.dailyAttendance

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.StaffAttendancePdf
import com.stevdza.san.mongodemo.model.StudentAttendancePdf
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.AttendanceCard
import com.stevdza.san.mongodemo.screenStudent.component.BottomBar
import com.stevdza.san.mongodemo.screenStudent.component.StudentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBarWithDate
import com.stevdza.san.mongodemo.screenStudent.timeInAndOut.SetTimeInAndOutStudentVM
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.SearchTopBar
import com.stevdza.san.mongodemo.screenTeacher.timeInAndOut.SetTimeInAndOutVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.MonthlyAttendanceReportDoc
import com.stevdza.san.mongodemo.util.MonthlyAttendanceReportDocSchool
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DailyAttendance(
    onBackClick:()->Unit
){
    val dailyAttendanceVM: DailyAttendanceVM = viewModel()
    val timeInAndOutVM: SetTimeInAndOutStudentVM = viewModel()

    val currentTime = LocalDateTime.now()
    val timeFormat = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))

    val students = dailyAttendanceVM.students.value.filter { it.studentStatus?.status == StudentStatusType.ACTIVE.status }
    val dailyAttendanceList = dailyAttendanceVM.attendanceList.value
        .filter { it.date == formattedDate }
    val listOfStudentId: List<String> = dailyAttendanceList.map { it.studentId }
    val listOfAbsentStudents = students.filter { student ->
        student.studentApplicationID !in listOfStudentId
    }

    val context = LocalContext.current

    val pdfDocumentsGenerator = MonthlyAttendanceReportDocSchool(context)

    // Setting Time In And Out
    val data= timeInAndOutVM.data.value
    val timeInOut = data.firstOrNull()
    val timeIn = timeInOut?.timeIn ?: ""
    val timeInList = timeIn.split(":")
    var minIn by remember { mutableStateOf("0") }
    var hourIn by remember { mutableStateOf("0") }
    if(timeIn.isNotEmpty()){
        minIn = timeInList.get(1)
        hourIn = timeInList.get(0)
    }

    val timeOut = timeInOut?.timeOut ?: ""
    val timeOutList = timeOut.split(":")
    var minOut by remember { mutableStateOf("0") }
    var hourOut by remember { mutableStateOf("0") }
    if(timeIn.isNotEmpty()){
        minOut = timeOutList.get(1)
        hourOut = timeOutList.get(0)
    }

    val scope = rememberCoroutineScope()
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.data?.let {
                context.contentResolver.openOutputStream(it)?.let {
                    scope.launch {
                        pdfDocumentsGenerator.generateInvoicePdf(
                            outputStream = it,
                            studentAttendancePdf = StudentAttendancePdf(
                                schoolNme = "Pure Knowledge Academy",
                                title = "Student Attendance Report For $formattedDate",
                                date = formattedDate,
                                attendance = dailyAttendanceList,
                                absentDays = "12",
                                presentDays = "50",
                                signatureUrl = "",
                                hourIn = hourIn.toInt(),
                                hourOut = hourOut.toInt(),
                                minIn = minIn.toInt(),
                                minOut = minOut.toInt(),
                                presentDaysPercent = "",
                                absentDaysPercent = "",
                            )
                        )
                    }
                }
            }
        }
    )

    var onSearch by remember { mutableStateOf(true) }
    var presentAndAbsentState by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf("") }

    val absentStudent = listOfAbsentStudents.sortedBy { it._id }

    val dailyAttendanceSearch = dailyAttendanceVM.attendanceList.value
        .filter { it.date == formattedDate && it.studentName == value}

    val absentStudentOnSearch = listOfAbsentStudents.filter { it.surname == value }



    DailyAttendanceScreen(
        onBackClick = { onBackClick() },
        title = "Daily Report",
        date = formattedDate,
        value = value,
        onValueChange = {value = it},
        onSearchClick = {onSearch = !onSearch},
        attendance =
        if(onSearch){
            dailyAttendanceList.reversed()

        }else{
//            testAttendanceList
            dailyAttendanceSearch
        },
        onFirstBtnClick = { presentAndAbsentState = true },
        onSecondBtnClick = { presentAndAbsentState = false },
        firstText = "Present",
        secondText = "Absent",
        presentAndAbsentState = presentAndAbsentState,
        absentStudent= if(onSearch){
            absentStudent
        }else{
            absentStudentOnSearch
        },
        attendanceVM = dailyAttendanceVM,
        label = "Enter Student Name Here",
        ondateClick ={},
        valueAbsent = value,
        onValueAbsentChange = {value = it },
        onSearchAbsentClick = { onSearch = !onSearch },
        labelAbsent = "Enter Student Name Here",
        onDownloadClick = {
            Intent(Intent.ACTION_CREATE_DOCUMENT)
                .apply {
                    type = "application/pdf"
                    putExtra(
                        Intent.EXTRA_TITLE,
                        "Student Attendance $formattedDate.pdf"
                    )
                }
                .also {
                    createDocumentLauncher.launch(it)
                }
        },
        hourIn = hourIn.toInt(),
        hourOut =  hourOut.toInt(),
        minIn = minIn.toInt(),
        minOut = minOut.toInt()
    )

}


@Composable
fun DailyAttendanceScreen(
    onBackClick:()->Unit,
    title: String,
    date: String,
    value: String,
    onValueChange:(String)->Unit,
    onSearchClick:()->Unit,
    attendance: List<AttendanceStudent>,
    absentStudent: List<StudentData>,
    onFirstBtnClick:()->Unit,
    onSecondBtnClick:()->Unit,
    firstText:String,
    secondText:String,
    presentAndAbsentState: Boolean,
    attendanceVM: DailyAttendanceVM,
    label: String,
    ondateClick: ()->Unit,
    valueAbsent: String,
    onValueAbsentChange:(String)->Unit,
    onSearchAbsentClick: () -> Unit,
    labelAbsent: String,
    onDownloadClick:()->Unit,
    hourIn : Int,
    hourOut: Int ,
    minIn : Int,
    minOut : Int
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(10.dp)
    ) {

        Column(modifier = Modifier.weight(1f)) {
            TopBarWithDate(
                onBackClick = { onBackClick() },
                title = title,
                date = date, ondateClick = {ondateClick()})
        }
        Column(modifier = Modifier.weight(8f)) {

            LazyColumn{
                item{
                    if (presentAndAbsentState){
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            SearchTopBar(
                                value =value,
                                onValueChange = {onValueChange(it) },
                                onSearchClick ={ onSearchClick()},
                                label = label,
                                modifier = Modifier.weight(9f)
                            )
                            Icon(modifier = Modifier
                                .padding(end = 5.dp)
                                .clickable {onDownloadClick()}
                                .weight(1f),
                                imageVector = Icons.Default.Download, contentDescription ="Download" )

                        }

                    }else{
                        SearchTopBar(
                            value =valueAbsent,
                            onValueChange = {onValueAbsentChange(it) },
                            onSearchClick ={ onSearchAbsentClick()},
                            label = labelAbsent
                        )
                    }

                }
                if (presentAndAbsentState){
                    items(attendance){
                        AttendanceCard(
                            studentName = it.studentName,
                            studentClass = it.cless,
                            broughtInBy = it.studentIn?.studentBroughtBy ?: "",
                            timeIn = it.studentIn?.time ?: "",
                            broughtOutBy = it.studentOut?.studentBroughtBy ?: "",
                            timeOut = it.studentOut?.time ?: "",
                            hourIn = hourIn,
                            hourOut =hourOut ,
                            minIn = minIn,
                            minOut = minOut
                        )
                        Log.e(TAG, "DailyAttendanceScreen: ${it._id.toHexString()}",)
                    }


                }
                else{
                    items(absentStudent){
                        val bit = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                        if (bit != null) {
                            StudentCard(
                                student = it,
                                bitmap = bit.asImageBitmap(),
                                onStudentClick = {}
                            )
//                        }
                    }
                }

            }

        }
        BottomBar(
            modifier = Modifier.weight(1f),
            onFirstBtnClick = { onFirstBtnClick() },
            onSecondBtnClick = { onSecondBtnClick() },
            firstText = firstText,
            secondText = secondText
        )
    }
}