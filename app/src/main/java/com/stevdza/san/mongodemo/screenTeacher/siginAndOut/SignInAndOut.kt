package com.stevdza.san.mongodemo.screenTeacher.siginAndOut

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.CurrentTermKey
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.home.SessionVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import com.stevdza.san.mongodemo.util.isInternetAvailable
import com.stevdza.san.pk.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StaffSignInAndOut(
    id: String,
    navToFeature:()->Unit,
    onBackClick:()->Unit
){

    val sessionVM: SessionVM = viewModel()
    val staffAttendanceVM: SignInAndOutVM = viewModel()

    val context = LocalContext.current
    val currentDate = LocalDate.now()
    val currentTime = LocalDateTime.now()
    val timeFormat = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    val formattedDateForMonth = currentDate.format(DateTimeFormatter.ofPattern("dd, MMMM, yyyy"))


    val currentTermKey = CurrentTermKey(context)
    val currentTerm = currentTermKey.getKey.collectAsState(initial = "")


    val sessions = sessionVM.sessions.value
    val w= sessions.firstOrNull()
    val currentSessions = w?.year?: ""

    var signinBtn by remember { mutableStateOf("") }
    signinBtn = if (isInternetAvailable(context)){
        "Sign In"
    }else{
        "No Internet Connection"
    }

    var signoutBtn by remember { mutableStateOf("") }
    signoutBtn = if (isInternetAvailable(context)){
        "Sign Out"
    }else{
        "No Internet Connection"
    }

    val teacherProfileData = staffAttendanceVM.teacherData.value
    val teacher = teacherProfileData.find { it._id.toHexString() == id }

    val attendance = staffAttendanceVM.teacherAttendanceData.value

    val todayAttendance = attendance.find {
        it.date == formattedDate && it.staffId == teacher?.staffId }


   
    if (todayAttendance == null){
        // sign in staff
        staffAttendanceVM.term.value = currentTerm.value
        staffAttendanceVM.session.value = currentSessions
        staffAttendanceVM.timeIn.value = timeFormat
        staffAttendanceVM.date.value = formattedDate
        Log.e(TAG, "SignInAndOut: date -> $formattedDate", )
        Log.e(TAG, "SignInAndOut: timeIn -> $timeFormat", )
        Log.e(TAG, "SignInAndOut: currentTerm -> ${currentTerm.value}", )
        Log.e(TAG, "SignInAndOut: currentSessions -> $currentSessions", )

        val convertedList = formattedDateForMonth.split(",")
        val monthFromDate = convertedList[1]
        staffAttendanceVM.month.value = monthFromDate
        Log.e(TAG, "SignInAndOut: month-> ${staffAttendanceVM.month.value}", )

        val name = "${teacher?.surname} ${teacher?.otherNames}"
        staffAttendanceVM.name.value = name

        val staffId = "${teacher?.staffId}"
        staffAttendanceVM.staffId.value = staffId

        val position = "${teacher?.position}"
        staffAttendanceVM.position.value = position

        BackHandler() { onBackClick() }

        StaffSigninAndOUtUI(
            teacher = teacher,
            name = name,
            staffId = staffId,
            btnText = signinBtn,

            onBtnClick = {
                if (isInternetAvailable(context)){
                    staffAttendanceVM.signInStaff(
                        onSuccess = {
                             Toast.makeText(context, "Signing In Successful",
                                Toast.LENGTH_SHORT).show()
                            navToFeature()
                        },
                        onError = { Toast.makeText(context,
                            "Something went wrong try again",
                            Toast.LENGTH_SHORT).show()})
                }
            },
            welcomeText = "Welcome Back",
            icon = painterResource(id = R.drawable.welcome)
        )

    }else{
        val name = "${teacher?.surname} ${teacher?.otherNames}"
        staffAttendanceVM.name.value = name

        val staffId = "${teacher?.staffId}"
        staffAttendanceVM.staffId.value = staffId

        staffAttendanceVM.timeOut.value = timeFormat
        staffAttendanceVM.objectIdEdit.value =  todayAttendance._id.toHexString()
        Log.e(TAG, "StaffSignInAndOut: ${staffAttendanceVM.objectIdEdit.value}", )

        // sign out staff
        if (todayAttendance.timeOut.isNotEmpty()){
            StaffSignOutUI(name = name, btnText =" Back To Home",
                onBtnClick = { navToFeature() },
                icon =  painterResource(id = R.drawable.log_out))
        }else{
            StaffSigninAndOUtUI(
                teacher = teacher,
                name = name,
                staffId = staffId,
                btnText = signoutBtn,
                onBtnClick = {
                    if (isInternetAvailable(context)){
                        staffAttendanceVM.signOutStaff(
                            onSuccess = {
                                Toast.makeText(context, "Signing Out Successful",
                                    Toast.LENGTH_SHORT).show()
                                navToFeature()
                            },
                            onError = { Toast.makeText(context, "Something went wrong try again",
                                Toast.LENGTH_SHORT).show()})
                    }
                },
                welcomeText = "Good Bye ",
                icon = painterResource(id = R.drawable.good_bye)
            )

        }

    }


}

@Composable
private fun StaffSigninAndOUtUI(
    teacher: TeacherProfile?,
    name: String,
    staffId: String,
    btnText: String,
    onBtnClick:()->Unit,
    welcomeText:String,
    icon: Painter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageBitmap = teacher?.pics?.let { convertBase64ToBitmap(it) }
        Card(
            modifier = Modifier.padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Cream,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )

        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = welcomeText, fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = Color.Black
                    )
                    Image(painter = icon, contentDescription ="" , modifier = Modifier.size(50.dp))
                }

                Text(text = name, fontSize = 14.sp)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
                    if (imageBitmap != null) {
                        Image(
                            modifier = Modifier
                                .size(70.dp),
                            bitmap = imageBitmap.asImageBitmap(),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )
                    }

                }
                Text(text = staffId, fontSize = 14.sp)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                BtnNormal(onclick = { onBtnClick()}, text = btnText)
            }

        }

    }
}



@Composable
private fun StaffSignOutUI(
    name: String,
    btnText: String,
    onBtnClick:()->Unit,
    icon: Painter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Cream,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )

        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$name have signed out for the day", fontSize = 14.sp)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                Image(painter = icon, contentDescription ="" )


                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                BtnNormal(onclick = { onBtnClick()}, text = btnText)
            }

        }

    }
}