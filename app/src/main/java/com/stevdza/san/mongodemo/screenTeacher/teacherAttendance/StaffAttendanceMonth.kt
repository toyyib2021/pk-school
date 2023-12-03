package com.stevdza.san.mongodemo.screenTeacher.teacherAttendance

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
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
import com.stevdza.san.mongodemo.dataStore.CurrentTermKey
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
import com.stevdza.san.mongodemo.util.StaffMonthlyAttendanceReportDoc
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StaffAttendanceHistoryMonth(
    staffId: String,
    onBackClick:(String)->Unit
){

    val staffDashboardVM: StaffDashboardVM = viewModel()
    val timeInAndOutVM: SetTimeInAndOutVM = viewModel()
    val sessionVM: SessionVM = viewModel()
    val context = LocalContext.current

    val currentTermKey = CurrentTermKey(context)
    val currentTerm = currentTermKey.getKey.collectAsState(initial = "")
    var month by remember { mutableStateOf("") }

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


    staffDashboardVM.staffId.value = staffId
    val months = listOf<String>(
        " January", " February", " March", " April", " May", " June",
        " July", " August", " September", " October", " November", " December"
    )
    val staff = staffDashboardVM.teacherData.value.find { it.staffId == staffId }


    var post by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf("") }

    staff?.let {
        post = it.position
        staffName = "${it.surname} ${it.otherNames}"
    }

    val sessions = sessionVM.sessions.value
    val w= sessions.firstOrNull()
    val currentSessions = w?.year?: ""

    val attendanceListForAStaff =  staffDashboardVM.teacherAttendanceData.value.filter {
        it.month == month && it.session == currentSessions
                && it.staffId == staffId}
    val totalNoOfPresent = attendanceListForAStaff.size


    // Total Days that the school opened
    val listOfEachDaySchoolOpened = staffDashboardVM.teacherAttendanceData.value.filter {
        it.month == month && it.session == currentSessions}
    val listOfEachDaySchoolOpenedForATerm = listOfEachDaySchoolOpened.distinctBy { it.date }
    val totalNoOfDaysSchoolOpened = listOfEachDaySchoolOpenedForATerm.size

    // Absent Days
    val totalDaysAbsent = totalNoOfDaysSchoolOpened - totalNoOfPresent
    Log.e(TAG, "StaffAttendanceHistoryMonth: totalDaysAbsent -> $totalDaysAbsent", )
    Log.e(TAG, "StaffAttendanceHistoryMonth: totalNoOfDaysSchoolOpened -> $totalNoOfDaysSchoolOpened", )
    Log.e(TAG, "StaffAttendanceHistoryMonth: totalNoOfPresent -> $totalNoOfPresent", )

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
                                schoolNme = staffName,
                                title = "Attendance Report For the Month of $month",
                                date = formattedDate,
                                attendance = attendanceListForAStaff,
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

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        var startIndex by remember { mutableIntStateOf(0) }
        val endIndex = minOf(startIndex + 30, attendanceListForAStaff.size)
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(3f)
        ){
            TopBarWithDownload(onBackClick = { onBackClick(post) }, title = currentSessions,
                onDownloadClick = {
                    if (attendanceListForAStaff.isNotEmpty()){
                        Intent(Intent.ACTION_CREATE_DOCUMENT)
                            .apply {
                                type = "application/pdf"
                                putExtra(
                                    Intent.EXTRA_TITLE,
                                    "$staffName Attendance for $month.pdf"
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
                    .fillMaxWidth(), text = staffName, fontSize = 15.sp, textAlign = TextAlign.Center)
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

        LazyColumn(modifier = Modifier
            .padding(10.dp)
            .weight(6f)
        ){
            item{
                InAndOutTableHeader()
            }

            items(attendanceListForAStaff.subList(startIndex, endIndex).reversed()){
                StaffAttendanceList(name = it.name, position = it.position,
                    date = it.date, timeIn = it.timeIn, timeOut = it.timeOut,
                    hourIn = hourIn.toInt(),
                    hourOut =  hourOut.toInt(),
                    minIn = minIn.toInt(),
                    minOut = minOut.toInt()
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
                        if (startIndex + 30 < attendanceListForAStaff.size) {
                            startIndex += 30
                        } else if (startIndex < attendanceListForAStaff.size) {
                            startIndex = attendanceListForAStaff.size
                        }
                    },
                    startIndex = startIndex,
                    listSize = attendanceListForAStaff.size
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