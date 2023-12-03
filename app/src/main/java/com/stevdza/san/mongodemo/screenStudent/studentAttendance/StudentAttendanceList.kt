package com.stevdza.san.mongodemo.screenStudent.studentAttendance

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.CurrentTermKey
import com.stevdza.san.mongodemo.model.StudentAttendancePdf
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.component.TopBarWithDownload
import com.stevdza.san.mongodemo.screenStudent.home.DashboardVM
import com.stevdza.san.mongodemo.screenStudent.home.SessionVM
import com.stevdza.san.mongodemo.screenStudent.timeInAndOut.SetTimeInAndOutStudentVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.MonthlyAttendanceReportDocSchool
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StudentAttendanceList(
    className: String,
    navToStudentAttendance: (String)->Unit,
    onBackClick:()->Unit
){

    val dasboardVM: DashboardVM = viewModel()
    val sessionVM: SessionVM = viewModel()
    val timeInAndOutVM: SetTimeInAndOutStudentVM = viewModel()
    val context = LocalContext.current
    val studentClass =
        dasboardVM.students.value.filter { cless ->
            cless.cless == className && cless.studentStatus?.status == StudentStatusType.ACTIVE.status
        }

    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))

    val currentTermKey = CurrentTermKey(context)
    val currentTerm = currentTermKey.getKey.collectAsState(initial = "")

    val sessions = sessionVM.sessions.value
    val w= sessions.firstOrNull()
    val currentSessions = w?.year?: ""

    val attendanceListForATermInAClass = dasboardVM.attendanceList.value.filter {
        it.term == currentTerm.value && it.session == currentSessions
                && it.cless == className}



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
                                title = "$className Attendance Report For ${currentTerm.value}",
                                date = formattedDate,
                                attendance = attendanceListForATermInAClass,
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

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        TopBarWithDownload(onBackClick = { onBackClick() }, title = className, onDownloadClick = {
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
        })
        LazyColumn(){
            items(studentClass.reversed()){student ->
                // list of attendance for a student
                val attendanceListForAStudent = dasboardVM.attendanceList.value.filter {
                    it.term == currentTerm.value && it.session == currentSessions
                            && it.studentId == student.studentApplicationID}
                val totalNoOfPresent = attendanceListForAStudent.size

                // Total Days that the school opened
                val listOfEachDaySchoolOpened = dasboardVM.attendanceList.value.filter {
                    it.term == currentTerm.value && it.session == currentSessions}
                val listOfEachDaySchoolOpenedForATerm = listOfEachDaySchoolOpened.distinctBy { it.date }
                val totalNoOfDaysSchoolOpened = listOfEachDaySchoolOpenedForATerm.size
                val percent = (totalNoOfPresent.toDouble().div(totalNoOfDaysSchoolOpened)) * 100
                Log.e(TAG, "percent: $percent", )

                Log.e(TAG, "StudentAttendanceList: ${student.studentApplicationID}", )
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable { navToStudentAttendance(student.studentApplicationID) },) {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(7f)) {
                            Text(text = "${student.surname} ${student.otherNames}",
                                fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = student.studentApplicationID,
                                fontSize = 13.sp, )
                        }
                        Card(elevation = 5.dp) {
                            Text(modifier = Modifier.padding(7.dp),
                                text = "$totalNoOfPresent / $totalNoOfDaysSchoolOpened")

                        }

                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
                    Divider(modifier = Modifier
                        .fillMaxWidth(), thickness = 2.dp)
                }


            }
        }
    }

}