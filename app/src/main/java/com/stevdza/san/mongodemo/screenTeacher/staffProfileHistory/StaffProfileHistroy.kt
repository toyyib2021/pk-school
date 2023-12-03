package com.stevdza.san.mongodemo.screenTeacher.staffProfileHistory

import android.content.ContentValues
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.screenStudent.component.TopBar

import com.stevdza.san.mongodemo.screenTeacher.dashboard.StaffDashboardVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import com.stevdza.san.pk.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StaffProfileHistory(
    staffId: String,
    onBackClick: ()-> Unit,
    navToPastStaffAttendance:(String, String)-> Unit,
    navToPastStaffAttendanceMonth:(String, String)-> Unit,
){

    val staffDashboardVM: StaffDashboardVM = viewModel()
    val staffList = staffDashboardVM.teacherData.value
    val staff = staffList.find { it.staffId == staffId }

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val attendanceData = staffDashboardVM.teacherAttendanceData.value.filter { it.staffId == staffId }

    val staffSession = attendanceData.distinctBy { it.session }

    var session by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    BackHandler() {
        onBackClick()
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent ={
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                navToPastStaffAttendance(staffId, session)
                            },
                        text = "Term",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 3.dp
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                navToPastStaffAttendanceMonth(staffId, session)
                            },
                        text = "Months",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 3.dp
                    )

                }


        },
        content = {
            if (staff != null){
                Column( modifier = Modifier
                    .fillMaxSize()
                    .background(Cream)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TopBar(onBackClick = { onBackClick() }, title = "Staff Data")
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp))
                        val bitmap = staff.pics.let { convertBase64ToBitmap(it) }
                        Card(
                            modifier = Modifier
                                .padding(5.dp),
                            shape = CircleShape
                        ) {
//                    if (bitmap != null) {
                            Image( modifier = Modifier
                                .size(100.dp), bitmap = bitmap.asImageBitmap(), contentDescription = "", contentScale = ContentScale.FillWidth)
//                    }
                        }

                        Text(text = "${staff.surname} ${staff.otherNames}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = staff.staffId, fontSize = 12.sp,)


                        Divider(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp), thickness = 3.dp)
                        Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                            staffSession.forEach {
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        session = it.session
                                        scope.launch {
                                            if (sheetState.isCollapsed) {
                                                sheetState.expand()
                                            } else {
                                                sheetState.collapse()

                                            }
                                        }
                                    },
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 3.dp
                                    ),
                                    shape = RoundedCornerShape(5.dp),
                                    colors = CardDefaults.cardColors(containerColor = Cream),
                                ) {
                                    Text(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                        text = it.session, fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )

                                }

                                Log.e(ContentValues.TAG, "studentClass: ${it.session}", )
                            }


                        }


                    }

                }
            }else{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.can_find_any_data),
                        contentDescription = "can_find_any_data")
                    Text(text = "Could not find any student ", fontSize = 16.sp)
                    Text(text = "with ID $staffId", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        sheetPeekHeight = 0.dp
    )


}