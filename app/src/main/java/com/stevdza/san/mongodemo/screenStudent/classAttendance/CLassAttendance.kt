package com.stevdza.san.mongodemo.screenStudent.classAttendance

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StudentAttendancePdf
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.dailyAttendance.DailyAttendanceScreen
import com.stevdza.san.mongodemo.screenStudent.dailyAttendance.DailyAttendanceVM
import com.stevdza.san.mongodemo.screenStudent.timeInAndOut.SetTimeInAndOutStudentVM
import com.stevdza.san.mongodemo.util.MonthlyAttendanceReportDocSchool
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ClassAttendance(
    className: String,
    onBackClick:()->Unit
){
    val dailyAttendanceVM: DailyAttendanceVM = viewModel()
    val timeInAndOutVM: SetTimeInAndOutStudentVM = viewModel()

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val scope = rememberCoroutineScope()
    val currentDate = LocalDate.now()
    var dateValue by remember { mutableStateOf(currentDate.toString()) }
    val currentTime = LocalDateTime.now()
    val timeFormat = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))


    val i = parseDate(dateValue, "yyyy-MM-dd")
    Log.e(TAG, "ClassAttendance: $i", )
    val datey = i?.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))

    val students = dailyAttendanceVM.students.value.filter {
        it.cless == className && it.studentStatus?.status == StudentStatusType.ACTIVE.status}
    val dailyAttendanceList = dailyAttendanceVM.attendanceList.value
        .filter { it.date == datey && it.cless == className}


    Log.e(TAG, "currentDate: $currentDate", )
    val listOfStudentId: List<String> = dailyAttendanceList.map { it.studentId }
    val listOfAbsentStudents = students.filter { student ->
        student.studentApplicationID !in listOfStudentId
    }



    val absentStudent = listOfAbsentStudents.sortedBy { it._id }


    var presentAndAbsentState by remember { mutableStateOf(true) }
    var onSearch by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf("") }

    val dailyAttendanceSearch = dailyAttendanceVM.attendanceList.value
        .filter { it.date == datey && it.cless == className && it.studentName == value}
    val absentStudentOnSearch = listOfAbsentStudents.filter { it.surname == value }

    dailyAttendanceVM.attendanceList.value.forEach {
        Log.e(TAG, "ClassAttendance: ${it.session}", )
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
                                title = "Student Attendance Report For $datey",
                                date = datey ?: "",
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

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent ={
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                backgroundColor =  Color.Blue
            ) {

                Column( modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Spacer(modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth())
                    TextFieldForm(value = dateValue , label = "Enter a Date (YYYY-MM-DD):", onValueChange = {dateValue = it},
                        backgroundColor = if (dateValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            Color.White
                        }else{
                            Color.Red
                        },
                        cursorColor = if (dateValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            Color.Black
                        }else{
                            Color.White
                        },
                        labelColor =  Color.White,
                        cardBackgroundColor =  if (dateValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            Color.White
                        }else{
                            Color.Red
                        },
                    )
                    Spacer(modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth())

                }
            }
        },
        content = { contentPadding ->
            DailyAttendanceScreen(
                onBackClick = { onBackClick() },
                title = className,
                date = datey?: "",
                value = value,
                onValueChange = {value = it},
                onSearchClick = { onSearch =!onSearch },
                attendance =
                if(onSearch){
                    dailyAttendanceList.reversed()
                }else{
                    dailyAttendanceSearch
                },
                onFirstBtnClick = { presentAndAbsentState = true },
                onSecondBtnClick = { presentAndAbsentState = false },
                firstText = "Present",
                secondText = "Absent",
                presentAndAbsentState = presentAndAbsentState,
                absentStudent=
                if(onSearch){
                    absentStudent
                }else{
                    absentStudentOnSearch
                },
                attendanceVM = dailyAttendanceVM,
                label = "Enter Student Name Here",
                ondateClick = {
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            sheetState.expand()
                        } else {
                            sheetState.collapse()
                        }
                    }
                },
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
                                "Class Attendance for $datey.pdf"
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
        },
        sheetPeekHeight = 0.dp
    )



}

fun parseDate(dateString: String, pattern: String): LocalDate? {
    return try {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        LocalDate.parse(dateString, formatter)
    } catch (e: DateTimeParseException) {
        null // Parsing failed
    }
}