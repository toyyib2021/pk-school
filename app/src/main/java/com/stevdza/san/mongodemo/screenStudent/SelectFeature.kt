package com.stevdza.san.mongodemo.screenStudent

import android.content.ContentValues
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.MainActivity
import com.stevdza.san.mongodemo.navigation.Screen
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.pk.R

@Composable
fun SelectFeature(
    navToLoginPin: ()->Unit,
    navToStaffAttendance:()->Unit,
    navToStudentAttendance: () -> Unit
){
    var backPressedTime: Long = 0

    BackHandler() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            val activity: MainActivity = MainActivity()
            // on below line we are finishing activity.
            activity.finish()
            System.exit(0)
        } else {
            Log.i(ContentValues.TAG, "dashboardNavGraph: press back button again ")
        }
        backPressedTime = System.currentTimeMillis()

    }

    SelectFeatureScreen(
        onBackClick = {navToLoginPin()},
        onStaffCardClick = {navToStaffAttendance()},
        onStudentCardClick = {navToStudentAttendance()}
    )
}




@Composable
fun SelectFeatureScreen(
    onBackClick: ()->Unit,
    onStaffCardClick:()->Unit,
    onStudentCardClick:()->Unit

){

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)
        .verticalScroll(state = rememberScrollState())
    ) {
        TopBar(
            onBackClick = { onBackClick() },
            title = "Features"
        )
        FeaturesCard(
            onStaffCardClick = onStaffCardClick,
            onStudentCardClick = onStudentCardClick
        )
    }

}




@Composable
fun FeaturesCard(
    onStaffCardClick:()->Unit,
    onStudentCardClick:()->Unit
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .weight(5f)
                .height(200.dp)
                .padding(20.dp)
                .clickable { onStaffCardClick() }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp)
            ) {
                Image(modifier = Modifier.weight(7f),
                    painter = painterResource(id = R.drawable.staff_attendance),
                    contentDescription = "staff_attendance")
                Text(text = "Staff Attendance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

        }

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .weight(5f)
                .height(200.dp)
                .padding(20.dp)
                .clickable { onStudentCardClick() }
        ) {

            Column(
                modifier =Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(modifier = Modifier.weight(7f), painter = painterResource(id = R.drawable.student_attendence),
                    contentDescription = "staff_attendance")
                Text(text = "Student Attendance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

}
