package com.stevdza.san.mongodemo.screenTeacher.staffProfileHistory

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StaffAttendancePdf
import com.stevdza.san.mongodemo.screenStudent.component.InAndOutTableHeader
import com.stevdza.san.mongodemo.screenStudent.component.NextAndBackBtn
import com.stevdza.san.mongodemo.screenStudent.component.StaffAttendanceList
import com.stevdza.san.mongodemo.screenStudent.component.TopBarWithDownload
import com.stevdza.san.mongodemo.screenStudent.home.SessionVM
import com.stevdza.san.mongodemo.screenTeacher.dashboard.StaffDashboardVM
import com.stevdza.san.mongodemo.screenTeacher.timeInAndOut.SetTimeInAndOutVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.StaffMonthlyAttendanceReportDoc
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun PastStaffAttendanceReportMonth(
    staffId: String,
    session: String,
    onBackClick:(String)->Unit
){

    val staffDashboardVm: StaffDashboardVM = viewModel()
    val timeInAndOutVM: SetTimeInAndOutVM = viewModel()
    val sessionVM: SessionVM = viewModel()
    val context = LocalContext.current
    var month by remember { mutableStateOf("") }
    var post by remember { mutableStateOf("") }

    // Setting Time In And Out
    val data= timeInAndOutVM.data.value
    val timeInOut = data.lastOrNull()
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
    Log.e(ContentValues.TAG, "StaffDailyAttendance: timeInMin-> $minIn", )
    Log.e(ContentValues.TAG, "StaffDailyAttendance: timeInHour-> $hourIn", )

    val months = listOf<String>(
        " January", " February", " March", " April", " May", " June",
        " July", " August", " September", " October", " November", " December"
    )




    var term by remember { mutableStateOf(Constants.FIRST_TERM) }
    var s by remember { mutableStateOf("") }
    var studentNames by remember { mutableStateOf("") }

    val attendanceListForAStudent =  staffDashboardVm.teacherAttendanceData.value.filter {
        it.month == month && it.session == session
                && it.staffId == staffId}
    val totalNoOfPresent = attendanceListForAStudent.size

    val staffData = staffDashboardVm.teacherData.value
    val staff = staffData.find { it.staffId == staffId }
    staff?.let {
        studentNames = "${it.surname} ${it.otherNames}"
        s = it.staffId
        post = it.position
    }


    // Total Days that the school opened
    val attendanceListForASession =  staffDashboardVm.teacherAttendanceData.value.filter {
        it.session == session  && it.month == month}
    val listOfEachDaySchoolOpened = attendanceListForASession.distinctBy { it.date }
    val totalNoOfDaysSchoolOpened = listOfEachDaySchoolOpened.size

    // Absent Days
    val totalDaysAbsent = totalNoOfDaysSchoolOpened - totalNoOfPresent

    // Percentage Calculation
    val presentPercentageInFloat = (totalNoOfPresent.toDouble() / totalNoOfDaysSchoolOpened) * 100
    val absentPercentageInFloat = (totalDaysAbsent.toDouble() / totalNoOfDaysSchoolOpened) * 100

    val presentPercentage = String.format("%.2f", presentPercentageInFloat)
    val absentPercentage = String.format("%.2f", absentPercentageInFloat)

    // Download PDF

    val pdfDocumentsGenerator = StaffMonthlyAttendanceReportDoc(context)
    val scope = rememberCoroutineScope()
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.data?.let {
                context.contentResolver.openOutputStream(it)?.let {
                    scope.launch {
                        pdfDocumentsGenerator.generateInvoicePdf(
                            outputStream = it,
                            staffAttendancePdf = StaffAttendancePdf(
                                schoolNme = studentNames,
                                title = "Attendance Report For the Month of $month",
                                date = formattedDate,
                                attendance = attendanceListForAStudent,
                                absentDays = totalDaysAbsent.toString(),
                                presentDays = totalNoOfPresent.toString(),
                                signatureUrl = "",
                                hourIn = hourIn.toInt(),
                                hourOut = hourOut.toInt(),
                                minIn = minIn.toInt(),
                                minOut = minOut.toInt(),
                                presentDaysPercent = presentPercentage,
                                absentDaysPercent = absentPercentage,
                            )
                        )
                    }
                }
            }
        }
    )


    BackHandler() {
        onBackClick(s)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        var startIndex by remember { mutableIntStateOf(0) }
        val endIndex = minOf(startIndex + 30, attendanceListForAStudent.size)
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(3f)
        ) {
            TopBarWithDownload(onBackClick = { onBackClick(s) }, title = session,
                onDownloadClick = {
                    if (attendanceListForAStudent.isNotEmpty()){
                        Intent(Intent.ACTION_CREATE_DOCUMENT)
                            .apply {
                                type = "application/pdf"
                                putExtra(
                                    Intent.EXTRA_TITLE,
                                    "$studentNames Attendance for $month.pdf"
                                )
                            }
                            .also {
                                createDocumentLauncher.launch(it)
                            }
                    }else{
                        Toast.makeText(context, "No Attendance Available", Toast.LENGTH_SHORT).show()
                    }

                })
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth(), text = studentNames, fontSize = 15.sp, textAlign = TextAlign.Center)
                Text(modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth(), text = post, fontSize = 15.sp, textAlign = TextAlign.Center)
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                thickness = 3.dp
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .horizontalScroll(state = rememberScrollState())
            ) {

                months.forEach {
                    Card(
                        modifier = Modifier
                            .clickable { month = it }
                            .padding(horizontal = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = if (month == it){
                            Blue
                        }else{
                            Color.White
                        },
                        contentColor = if (month == it){
                            Color.White
                        }else{
                            Color.Black
                        }
                    ) {
                        Text(modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(),
                            text = it, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }


            }
        }


        Log.e(ContentValues.TAG, "attendanceListForAStudent: $attendanceListForAStudent",)
        LazyColumn(modifier = Modifier
            .padding(10.dp)
            .weight(6f)
        ){
            item{
                InAndOutTableHeader()
            }

            items(attendanceListForAStudent.subList(startIndex, endIndex).reversed()){
                StaffAttendanceList(name = it.name, position = it.position,
                    date = it.date, timeIn = it.timeIn, timeOut = it.timeOut,
                    hourIn = hourIn.toInt(),
                    hourOut =  hourOut.toInt(),
                    minIn = minIn.toInt(),
                    minOut =minOut.toInt()
                )


            }
            item{
                NextAndBackBtn(
                    onBackClick = {
                        if (startIndex < 30) {
                            startIndex = 0
                        } else{
                            startIndex -= 30
                        }
                    },
                    onNextClick = {
                        if (startIndex + 30 < attendanceListForAStudent.size) {
                            startIndex += 30
                        } else if (startIndex < attendanceListForAStudent.size) {
                            startIndex = attendanceListForAStudent.size
                        }
                    },
                    startIndex = startIndex,
                    listSize = attendanceListForAStudent.size
                )
            }

        }


        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1.5f)
            .padding(10.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier
                    .weight(6f),
                    text = "", )
                Text(modifier = Modifier
                    .weight(2f),
                    text = "Days", )
                Text(modifier = Modifier
                    .weight(2f),
                    text = "%", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier
                    .weight(6f),
                    text = "Present", )
                Text(modifier = Modifier
                    .weight(2f),
                    text = totalNoOfPresent.toString(), )
                Text(modifier = Modifier
                    .weight(2f),
                    text = presentPercentage, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp), thickness = 2.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier
                    .weight(6f),
                    text = "Absent", )
                Text(modifier = Modifier
                    .weight(2f),
                    text = totalDaysAbsent.toString(), )
                Text(modifier = Modifier
                    .weight(2f),
                    text = absentPercentage,fontWeight = FontWeight.Bold)

            }
//            Log.e(TAG, "presentPercentage: $presentPercentage ", )
//            Log.e(TAG, "absentPercentage: $absentPercentage ", )
//            Log.e(TAG, "presentPercentage: $totalDays ", )
        }
    }

}