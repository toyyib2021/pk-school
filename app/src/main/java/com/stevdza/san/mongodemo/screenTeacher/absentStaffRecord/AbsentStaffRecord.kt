package com.stevdza.san.mongodemo.screenTeacher.absentStaffRecord

import android.content.ContentValues
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.screenStudent.component.NextAndBackBtn
import com.stevdza.san.mongodemo.screenStudent.component.StaffAbsentCard
import com.stevdza.san.mongodemo.screenStudent.component.StaffAbsentCardNoEdit
import com.stevdza.san.mongodemo.screenStudent.component.StaffAttendanceList
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.home.SessionVM
import com.stevdza.san.mongodemo.screenTeacher.dashboard.StaffDashboardVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun AbsentStaffRecord(
    onBackClick:()->Unit,
    session:String,
    staffId:String
){


    val staffDashboardVm: StaffDashboardVM = viewModel()
    val sessionVM: SessionVM = viewModel()
    val context = LocalContext.current
    var month by remember { mutableStateOf("") }
    var post by remember { mutableStateOf("") }

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

    val absent = staffDashboardVm.absentStaff.value.filter {
        it.session == session && it.month == month }

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

    val accountTypeKey: AccountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")


    BackHandler() {
        onBackClick()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        var startIndex by remember { mutableIntStateOf(0) }
        val endIndex = minOf(startIndex + 30, absent.size)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
        ) {
            TopBar(onBackClick = { onBackClick() }, title = session)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .weight(5f)
                        .fillMaxWidth(),
                    text = studentNames,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .weight(5f)
                        .fillMaxWidth(), text = post, fontSize = 15.sp, textAlign = TextAlign.Center
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 3.dp
            )
            Row(
                modifier = Modifier
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
                        backgroundColor = if (month == it) {
                            Blue
                        } else {
                            Color.White
                        },
                        contentColor = if (month == it) {
                            Color.White
                        } else {
                            Color.Black
                        }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(),
                            text = it, fontSize = 14.sp, textAlign = TextAlign.Center
                        )
                    }
                }


            }
        }


        Log.e(ContentValues.TAG, "attendanceListForAStudent: $attendanceListForAStudent",)
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .weight(6f)
        ) {
            items(absent.subList(startIndex, endIndex).reversed()){ absent ->
//                val staff = absentStaff.find { it.staffId == absent.staffId }
//                Log.e(ContentValues.TAG, "DailyAttendanceStaffScreen: staff -> ${absent.staffId}",)
                if (staff != null) {
                    val bit = staff.pics.let { it1 -> convertBase64ToBitmap(it1) }
                    StaffAbsentCardNoEdit(
                        staff = staff,
                        bitmap = bit.asImageBitmap(),
                        reasonForAbsent = absent.reason,
                        absentDate = absent.date,
                    )
                }



            }
            item {
                NextAndBackBtn(
                    onBackClick = {
                        if (startIndex < 30) {
                            startIndex = 0
                        } else {
                            startIndex -= 30
                        }
                    },
                    onNextClick = {
                        if (startIndex + 30 < absent.size) {
                            startIndex += 30
                        } else if (startIndex < absent.size) {
                            startIndex = absent.size
                        }
                    },
                    startIndex = startIndex,
                    listSize = absent.size
                )
            }

        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
                .padding(10.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = "",
                )
                Text(
                    modifier = Modifier
                        .weight(2f),
                    text = "Days",
                )
                Text(
                    modifier = Modifier
                        .weight(2f),
                    text = "%", fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = "Present",
                )
                Text(
                    modifier = Modifier
                        .weight(2f),
                    text = totalNoOfPresent.toString(),
                )
                Text(
                    modifier = Modifier
                        .weight(2f),
                    text = presentPercentage, fontWeight = FontWeight.Bold
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp), thickness = 2.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = "Absent",
                )
                Text(
                    modifier = Modifier
                        .weight(2f),
                    text = totalDaysAbsent.toString(),
                )
                Text(
                    modifier = Modifier
                        .weight(2f),
                    text = absentPercentage, fontWeight = FontWeight.Bold
                )

            }
        }
    }

}