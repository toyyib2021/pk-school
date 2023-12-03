package com.stevdza.san.mongodemo.screenTeacher.absentStaffRecord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StaffStatusType
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenTeacher.dashboard.StaffDashboardVM
import com.stevdza.san.mongodemo.ui.theme.Cream

@Composable
fun AbsentStaffRecordList(
    onBackClick:()->Unit,
    onStaffClick:(String, String)->Unit
){
    val staffDashboardVM: StaffDashboardVM = viewModel()
    val staff = staffDashboardVM.teacherData.value.filter { it.teacherStatus?.status == StaffStatusType.ACTIVE.status }



    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        TopBar(onBackClick = { onBackClick() }, title = "Staff List")
        LazyColumn(){
            items(staff){teacher ->

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)) {
                    val attendanceData = staffDashboardVM.absentStaff.value.filter { it.staffId == teacher.staffId }
                    val staffSession = attendanceData.distinctBy { it.session }
                    var hide by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.clickable {hide = !hide }) {
                        Text(
                            text = "${teacher.surname} ${teacher.otherNames}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = teacher.position,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)

                        if (hide){
                            staffSession.forEach {
                                Spacer(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp))
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onStaffClick(teacher.staffId, it.session) },
                                    text = it.session,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp))
                                Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                            }
                        }
                    }


                }

            }
        }
    }
}