package com.stevdza.san.mongodemo.screenStudent.parentView

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun ParentDashboard(
    familyID: String,
    onCurrentClassClick:(String)->Unit,
    onPreviousClassClick:(String)->Unit,
    onParentAndWardCInforClick:(String)->Unit,
){

    val parentDashboardVM: DashboardVM = viewModel()
    val currentDate = LocalDate.now()
    val context = LocalContext.current
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    parentDashboardVM.familyID.value = familyID
    parentDashboardVM.date.value = formattedDate


    Log.e(TAG, "ParentDashboard: family ID -> $familyID", )



//    if (parentDashboardVM.studentList.value.isNotEmpty()){
//        parentDashboardVM.getWIthFamilyID()
//    }

    val studentList = parentDashboardVM.studentList.value.filter { it.familyId == familyID }
    var studentID by remember { mutableStateOf("") }
    var timeIn by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    val firstStudent = studentList.firstOrNull()

    var studentImage by remember { mutableStateOf<Bitmap?>(null) }



    var studentDataChangeState by remember { mutableStateOf(false) }
    var studentDataChangeStateTwo by remember { mutableStateOf(true) }
    val student = remember { mutableStateOf<StudentData?>(null) }

    LaunchedEffect(key1 = studentDataChangeState ){

        student.value?.let {
            parentDashboardVM.studentID.value = it.studentApplicationID
            parentDashboardVM.studentName.value =  "${it.surname} ${it.otherNames}"
            studentImage = convertBase64ToBitmap(it.pics)

        }
        val attendanceForTheDay =  parentDashboardVM.attendanceList.value.filter { it.date == formattedDate }
        val studentInAndOutData = attendanceForTheDay.find {stud ->
            parentDashboardVM.studentID.value == stud.studentId }

        parentDashboardVM.timeIn.value = studentInAndOutData?.studentIn?.time
        parentDashboardVM.timeOut.value = studentInAndOutData?.studentOut?.time



        Log.e(TAG, "ParentDashboard: time In ->  ${parentDashboardVM.timeIn.value}", )
//                parentDashboardVM.byte.value =  it.pics

    }


    if (studentDataChangeStateTwo){
        firstStudent?.let {
            parentDashboardVM.studentID.value = it.studentApplicationID
            parentDashboardVM.studentName.value =  "${it.surname} ${it.otherNames}"
            studentImage = convertBase64ToBitmap(it.pics)

        }
        val attendanceForTheDay =  parentDashboardVM.attendanceList.value.filter { it.date == formattedDate }
        val studentInAndOutData = attendanceForTheDay.find {stud ->
            parentDashboardVM.studentID.value == stud.studentId }

        parentDashboardVM.timeIn.value = studentInAndOutData?.studentIn?.time
        parentDashboardVM.timeOut.value = studentInAndOutData?.studentOut?.time
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)
    ) {

        DashboardSectionOne(
            studentName = parentDashboardVM.studentName.value,
            presentAndAbsent =
            if (parentDashboardVM.timeIn.value != null){
                "Present"
            }else{
                "Absent"
            },
            date = parentDashboardVM.date.value,
            timeIn =parentDashboardVM.timeIn.value ?: "--:--",
            timeOut = parentDashboardVM.timeOut.value ?: "--:--",
            bitmap = studentImage

        )
        DashboardSectionTwo(
            modifier = Modifier.weight(6.5f),
            currentClassTitle = "Current Class Attendance",
            currentClassDes = "view all attendance of the present session to get a better understanding of your wards attendance pattern",
            onCurrentClassClick = {
                if (studentList.isNotEmpty()){
                    onCurrentClassClick(parentDashboardVM.studentID.value)
                }else{
                    Toast.makeText(context, "On Student Associated to This Family", Toast.LENGTH_SHORT).show()
                }
               },
            onPreviousClassClick = {
                if (studentList.isNotEmpty()){
                onPreviousClassClick(parentDashboardVM.studentID.value)
            }else{
            Toast.makeText(context, "On Student Associated to This Family", Toast.LENGTH_SHORT).show()
        }
                                   },
            previousClassTitle = "Previous Classes Attendance",
            previousClassDes = "view all attendance of the previous sessions to get a better understanding of your wards attendance pattern",
            onParentAndWardCInforClick = {onParentAndWardCInforClick(familyID)},
            parentAndWardTitle = "Parent/Guardian And Student",
            parentAndWardDes = "view all information about student, parent's and guardian",
        )
        DashboardSectionThree(
            modifier = Modifier.weight(1.5f),
            studentNames = studentList,
            onStudentClick = {
                studentDataChangeState = !studentDataChangeState
                studentDataChangeStateTwo = false
                student.value = it

                Log.e(TAG, "ParentDashboard: studentID-> ${parentDashboardVM.studentID.value}," )
                Log.e(TAG, "ParentDashboard:  studentName-> ${parentDashboardVM.studentName.value}," )
                Log.e(TAG, "ParentDashboard:  timeIn-> ${timeIn}," )
                Log.e(TAG, "ParentDashboard:  timeOut-> ${parentDashboardVM.timeOut.value}," )
                Log.e(TAG, "ParentDashboard:  timeOut-> ${student.value}," )

            },
            selectAStudent = "Select A Student")
    }


}

@Composable
fun DashboardSectionOne(
    modifier: Modifier=Modifier,
    studentName:String,
    presentAndAbsent:String,
    date:String,
    timeIn:String,
    timeOut:String,
    bitmap: Bitmap? = null
){

    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Blue)
    ) {
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp, color = Color.White)
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Blue)
                ) {
                    bitmap?.asImageBitmap()?.let {
                        Image(modifier = Modifier
                            .size(100.dp),
                            bitmap = it,
                            contentDescription = "student pics")
                    }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .weight(8f),
                ) {
                    Text(modifier = Modifier
                        .fillMaxWidth(), text = studentName,
                        fontSize = 20.sp, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier
                        .padding(7.dp)
                        .fillMaxWidth())
                    Text(modifier = Modifier
                        .fillMaxWidth() ,
                        text = presentAndAbsent, fontSize = 18.sp,
                        textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier
                        .padding(7.dp)
                        .fillMaxWidth())
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                            text = date, fontSize = 14.sp,
                            textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {

                        Row(
                            modifier = Modifier
                                .weight(5f)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "login",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Column(modifier = Modifier.weight(6f)) {
                                Text(text = "Time In:", fontSize = 14.sp, color = Color.White)
                                Text(text = timeIn, fontSize = 12.sp, color = Color.White)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .weight(5f)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "logOut",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Column(modifier = Modifier.weight(6f)) {
                                Text(text = "Time Out:", fontSize = 14.sp, color = Color.White)
                                Text(text = timeOut, fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }

                    }



                }
            }

        }

}



@Composable
fun DashboardSectionTwo(
    modifier: Modifier=Modifier,
    onCurrentClassClick:()->Unit,
    currentClassTitle: String,
    currentClassDes: String,
    onPreviousClassClick:()->Unit,
    previousClassTitle: String,
    previousClassDes: String,
    onParentAndWardCInforClick:()->Unit,
    parentAndWardTitle:String,
    parentAndWardDes:String,

    ){
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState()),
//        verticalArrangement = Arrangement.Center
    ) {
        // Current Class Attendance
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCurrentClassClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(5.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Blue)
            ) {
                Image(modifier = Modifier
                    .size(100.dp),
                    painter = painterResource(id = R.drawable.two),
                    contentDescription = "student pics")
            }
            Spacer(modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp))
            Column(modifier = Modifier.weight(8f)) {
                Text(text = currentClassTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = currentClassDes, fontSize = 10.sp, lineHeight = 12.sp)
            }
            androidx.compose.material.Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "logOut",)

        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            thickness = 3.dp, color = Color.Black)

        // Previous Class Attendance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPreviousClassClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(5.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Blue)
            ) {
                Image(modifier = Modifier
                    .size(100.dp),
                    painter = painterResource(id = R.drawable.three),
                    contentDescription = "student pics")
            }
            Spacer(modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp))
            Column(modifier = Modifier.weight(8f)) {
                Text(text = previousClassTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = previousClassDes, fontSize = 10.sp, lineHeight = 12.sp)
            }
            androidx.compose.material.Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "logOut",)

        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            thickness = 3.dp, color = Color.Black)

        // Parent/Guardian/Ward Information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onParentAndWardCInforClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(5.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Blue)
            ) {
                Image(modifier = Modifier
                    .size(100.dp),
                    painter = painterResource(id = R.drawable.family_icon),
                    contentDescription = "student pics")
            }
            Spacer(modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp))
            Column(modifier = Modifier.weight(8f)) {
                Text(text = parentAndWardTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = parentAndWardDes, fontSize = 10.sp, lineHeight = 12.sp)
            }
            androidx.compose.material.Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "logOut",)

        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            thickness = 3.dp, color = Color.Black)
    }
}


@Composable
fun DashboardSectionThree(
    modifier: Modifier = Modifier,
    studentNames: List<StudentData>,
    onStudentClick:(StudentData)->Unit,
    selectAStudent: String
){

    Column(
        modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(modifier = Modifier.padding(10.dp), text = selectAStudent,
            fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier  = Modifier
                .fillMaxWidth()
                .horizontalScroll(state = rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {

            studentNames.forEach {
                Card(modifier = Modifier.clickable { onStudentClick(it) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = Cream),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp), text = it.otherNames, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(10.dp))

            }
        }
    }

}

