package com.stevdza.san.mongodemo.screenStudent.oldStudentScreen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun OldStudentScreen(
    studentId: String,
    onBackClick: ()-> Unit,
    navToOldStudentAttendanceMain:(String, String)-> Unit,
){

    val oldStudentVM: OldStudentVM = viewModel()
    val studentList = oldStudentVM.studentData.value
    val student = studentList.find { it.studentApplicationID == studentId }

    val attendanceData = oldStudentVM.attendanceData.value.filter { it.studentId == studentId }

    val studentClass = attendanceData.distinctBy { it.cless }


    BackHandler() {
        onBackClick()
    }
    if (student != null){
        Column( modifier = Modifier
            .fillMaxSize()
            .background(Cream)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(onBackClick = { onBackClick() }, title = "Student Data")
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp))
                val bitmap = student.pics.let { convertBase64ToBitmap(it) }
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

                Text(text = "${student.surname} ${student.otherNames}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = student.studentApplicationID, fontSize = 12.sp,)


                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp), thickness = 3.dp)
                Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                    studentClass.forEach {
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable { navToOldStudentAttendanceMain(it.studentId, it.cless) },
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            ),
                            shape = RoundedCornerShape(5.dp),
                            colors = CardDefaults.cardColors(containerColor = Cream),
                        ) {
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                                text = it.cless, fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Log.e(TAG, "studentClass: ${it.cless}", )
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
            Text(text = "with ID $studentId", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

}