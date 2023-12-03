package com.stevdza.san.mongodemo.screenStudent.attendanceIn

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.CurrentTermKey
import com.stevdza.san.mongodemo.model.AssociateParent
import com.stevdza.san.mongodemo.model.StudentSelectable
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.accountType.AccountTypeScreen
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.LoadingDialog
import com.stevdza.san.mongodemo.screenStudent.component.LoadingVM
import com.stevdza.san.mongodemo.screenStudent.component.StudentAttendanceCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.family.FamilyVM
import com.stevdza.san.mongodemo.screenStudent.home.SessionVM
import com.stevdza.san.mongodemo.ui.theme.Black
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.ui.theme.White
import com.stevdza.san.mongodemo.util.Constants.FIRST_TERM
import com.stevdza.san.mongodemo.util.Constants.IN
import com.stevdza.san.mongodemo.util.Constants.OUT
import com.stevdza.san.mongodemo.util.Constants.SECOND_TERM
import com.stevdza.san.mongodemo.util.Constants.THIRD_TERM
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import com.stevdza.san.mongodemo.util.isInternetAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun AttendanceInScreen(
    familyId: String,
    navToHome:()->Unit,
    onBackClick:()->Unit,
){

    val sessionVM: SessionVM= viewModel()
    val familyVM: FamilyVM = viewModel()
    val loadingAnimationVM: LoadingVM = viewModel()
    val attendanceInVM: AttendanceInVM = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    familyVM.objectID.value = familyId

    val currentDate = LocalDate.now()
    val currentTime = LocalDateTime.now()
    val timeFormat = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    val currentTermKey = CurrentTermKey(context)
    val currentTerm = currentTermKey.getKey.collectAsState(initial = "")
    sessionVM.termName.value = currentTerm.value

    val sessions = sessionVM.sessions.value
    val w= sessions.firstOrNull()
    val currentSessions = w?.year?: ""





    var signInAndOutState by remember { mutableStateOf(IN) }

    if(familyVM.parentData.value.isNotEmpty()){
        familyVM.getParentWithID()
    }

    if(familyVM.childrenData.value.isNotEmpty()){
        familyVM.getChildrenWIthFamilyID()
    }

    if(familyVM.associateParentData.value.isNotEmpty()){
        familyVM.getAssociateParentWithFamilyID()
    }

    val currentTermDate = remember { mutableStateOf("") }



    if (sessionVM.termName.value.isNotEmpty()){
        LaunchedEffect(key1 = true){

            sessionVM.getTermWithTermName()
            sessionVM.oneTerm.value.let {
                if (it != null) {
                    currentTermDate.value = it.termEnd
                }
            }
        }
    }


//    Log.e(TAG, "currentTermDate: ${currentTermDate.value}", )
//    Log.e(TAG, "currentDate: $currentDate", )
    Log.e(TAG, "currentTerm: ${currentTerm.value}", )

    BackHandler() {
        onBackClick()
    }

    if (sessionVM.termName.value.isNotEmpty()){
        Column {

            if (termChecker(
                    inputDateText = currentTermDate,
                    currentDate =currentDate,
//                    currentTermExperation = currentTermExperation
                )){
                // Take Attendance
//                Text(text = "Term is ongoing", fontSize = 20.sp)
                attendanceInVM.familyId.value = familyId
                attendanceInVM.date.value = formattedDate
                attendanceInVM.timeIn.value = timeFormat
                attendanceInVM.timeOut.value = timeFormat
                attendanceInVM.term.value = currentTerm.value
                attendanceInVM.session.value = currentSessions
//                Log.e(TAG, "date: ${attendanceInVM.date.value}", )
//                Log.e(TAG, "timeIn: ${attendanceInVM.timeIn.value}", )
//                Log.e(TAG, "timeOut: ${attendanceInVM.timeOut.value}", )
//                Log.e(TAG, "term: ${attendanceInVM.term.value}", )
                Log.e(TAG, "session: ${attendanceInVM.session.value}", )
//                Log.e(TAG, "familyId: ${attendanceInVM.familyId.value}", )


                LaunchedEffect(key1 = Unit){
                    loadingAnimationVM.open.value = true
                    if (loadingAnimationVM.open.value == true){
                        delay(2000)
                        loadingAnimationVM.startThread(
                            backgroundTask ={}
                        )
                        loadingAnimationVM.open.value = false
                    }
                }

                if (loadingAnimationVM.open.value == true){
                    LoadingDialog(
                        viewModel = loadingAnimationVM
                    )
                }else{
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(Cream)
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            TopBar(onBackClick = { onBackClick()}, title = "Attendance")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 50.dp, vertical = 10.dp)
                            ){
                                Card(
                                    modifier = Modifier
                                        .clickable { signInAndOutState = IN }
                                        .weight(5f),
                                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp),
                                    contentColor = Black,
                                    backgroundColor =
                                    when(signInAndOutState){
                                        IN->{
                                            White
                                        }
                                        OUT->{
                                            Cream
                                        }

                                        else -> {
                                            Cream}
                                    },
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "In", fontSize = 14.sp)
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .clickable { signInAndOutState = OUT }
                                        .weight(5f),
                                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                                    contentColor = Black,
                                    backgroundColor =
                                    when(signInAndOutState){
                                        IN->{
                                            Cream
                                        }
                                        OUT->{
                                            White
                                        }

                                        else -> {
                                            Cream}
                                    },
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "Out", fontSize = 14.sp)
                                    }
                                }
                            }


                        }

                        Column(modifier = Modifier.weight(8f)) {

                            // One Parent Plus Associate Parent
                            val oneParent = familyVM.oneParentData.value
                            var asty by remember { mutableStateOf<AssociateParent?>(null)}

                            var selectedItemId by remember { mutableIntStateOf(-1) } // Initialize with an invalid value
                            val check = remember {mutableStateOf(false) }
                            oneParent?.let {
                                val ab = AssociateParent().apply {
                                    surname = it.surname
                                    otherNames = it.otherNames
                                    pics = it.pics
                                    phone = it.phone
                                    relationship = it.relationship

                                }
//                            Log.e(TAG, "ab: ${ab.surname}", )

                                asty = ab
//                            Log.e(TAG, "asty: ${asty?.surname}", )


                            }
                            val associateParents = familyVM.associateParentData.value + asty

                            var parentNames by remember {
                                mutableStateOf("")
                            }
                            attendanceInVM.studentBroughtInBy.value = parentNames
                            attendanceInVM.studentBroughtOutBy.value = parentNames


                            val data = familyVM.childrenData.value.filter { it.studentStatus?.status == StudentStatusType.ACTIVE.status }
                            val stu = attendanceInVM.stu
                            val stuOut = attendanceInVM.stuOut
                            val studentsFromAFamily = data.filter { it.familyId == familyId }
                            val currentAttendanceList = attendanceInVM.attendanceList.value.filter { it.date == formattedDate && it.familyId == familyId}
                            val currentAttendanceListForOut = attendanceInVM.attendanceList.value.filter {
                                it.date == formattedDate && it.familyId == familyId && it.studentOut == null
                            }
                            val studentApplicationIDInAttendance = currentAttendanceList.map { it.studentId }
                            val studentApplicationIDOutAttendance = currentAttendanceListForOut.map { it.studentId }
                            val studentThatCanSignIn = studentsFromAFamily.filter {
                                it.studentApplicationID !in studentApplicationIDInAttendance
                            }

                            val u = studentsFromAFamily.filter {
                                it !in studentThatCanSignIn
                            }


                            val studentThatCanSignOut = u.filter { it.studentApplicationID in studentApplicationIDOutAttendance  }

                            attendanceInVM.studentData.value = studentThatCanSignIn
                            var studentListHide by remember { mutableStateOf(false) }

                            LaunchedEffect(key1 = studentListHide){
                                if (studentListHide){
                                    attendanceInVM.studentData.value.forEach {
                                        val selectable = StudentSelectable(it, mutableStateOf(false))
                                        stu.add(selectable)
                                    }
                                }
                            }

                            LaunchedEffect(key1 = studentListHide){
                                if (studentListHide){
                                    studentThatCanSignOut.forEach {
                                        val selectable = StudentSelectable(it, mutableStateOf(false))
                                        stuOut.add(selectable)
                                    }
                                }
                            }

                            val students = stu.filter { it.student.familyId == familyId }
                            val studentsOut = stuOut.filter { it.student.familyId == familyId }
                            val signOutStudentId = studentsOut.map { it.student.studentApplicationID }
                            val studentAttendanceForOut = currentAttendanceList.filter {
                                it.studentId in signOutStudentId
                            }
                            attendanceInVM.getAttendanceIDForSigningOut.value = studentAttendanceForOut



                            LaunchedEffect(key1 = signInAndOutState ){
                                stu.forEach {
                                    it.selectedFalse()
                                }
                                stuOut.forEach{
                                    it.selectedFalse()
                                }
                            }

                            LazyColumn(state = rememberLazyListState()){

                                items(associateParents.size) { index ->
                                    androidx.compose.material3.Card(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .clickable {
                                                parentNames =
                                                    "${associateParents[index]?.surname ?: ""} ${associateParents[index]?.otherNames ?: ""}"
                                                selectedItemId = index
                                                Log.e(TAG, "parentNames: $parentNames",)
                                                studentListHide = true
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Cream
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                    ) {

                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .weight(4f)
                                            ){
                                                androidx.compose.material3.Card(
                                                    modifier = Modifier
                                                        .padding(5.dp),
                                                    shape = CircleShape
                                                ) {
                                                    val bsonBinary = associateParents[index]?.pics
                                                val bitmap = bsonBinary?.let { it -> convertBase64ToBitmap(it) }

                                                if (bitmap != null) {
                                                    Image( modifier = Modifier.size(70.dp),
                                                        bitmap = bitmap.asImageBitmap(),
                                                        contentDescription = "",
                                                        contentScale = ContentScale.FillWidth)
                                                }

                                                }
                                            }

                                            Column(
                                                modifier = Modifier
                                                    .weight(6f)
                                                    .fillMaxWidth()
                                                    .padding(start = 20.dp),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                androidx.compose.material3.Text(
                                                    text = "${associateParents[index]?.surname}  ${associateParents[index]?.otherNames}",
                                                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                                                )
                                                Spacer(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(5.dp)
                                                )
                                                androidx.compose.material3.Text(
                                                    text = "${associateParents[index]?.phone}",
                                                    fontSize = 12.sp
                                                )
                                                androidx.compose.material3.Text(
                                                    text = "${associateParents[index]?.relationship}",
                                                    fontSize = 12.sp
                                                )

                                            }
                                            if (selectedItemId == index) {
                                                Icon(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "CheckCircle"
                                                )
                                            }


                                        }
                                    }

                                }

                                item{
                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp)){

                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { check.value = !check.value }) {
                                            Column(modifier = Modifier.weight(9f)) {
                                                Text(text = "Select All", fontSize = 14.sp)
                                            }
                                            if (check.value){
                                                Icon(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    imageVector = Icons.Default.AddBox,
                                                    contentDescription = "CheckCircle")
                                            }else{
                                                Icon(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    imageVector = Icons.Default.CheckBox,
                                                    contentDescription = "CheckCircle")
                                            }
                                        }
                                    }
                                }

                                when(signInAndOutState){
                                    IN->{
                                        items(students){
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Column(modifier = Modifier.weight(9f)) {

                                                    val bitmapImage = it.student.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                                                if (bitmapImage != null) {
//                                            val b = familyVM.resizePhoto(bitmapImage)
                                                    StudentAttendanceCard(
                                                        selectable = it,
                                                        bitmap = bitmapImage.asImageBitmap(),
                                                        onParentClick = {
                                                            it.toggle(
                                                                addSelectedStudent = {},
                                                                removeSelectedStudent = {})
                                                        },
                                                        check = check
                                                    )
//                                                }
                                                }
                                            }
                                        }

                                    }
                                    OUT->{
                                        items(studentsOut){

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Column(modifier = Modifier.weight(9f)) {

                                                    val bitmapImage = it.student.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                                                if (bitmapImage != null) {
                                                    StudentAttendanceCard(
                                                        selectable = it,
                                                        bitmap = bitmapImage.asImageBitmap(),
                                                        onParentClick = {
                                                            it.toggle(
                                                                addSelectedStudent = { },
                                                                removeSelectedStudent = { }
                                                            )
                                                        },
                                                        check = check
                                                    )
//                                                }
                                                }
                                            }
                                        }

                                    }
                                }

                                item{
                                    BtnNormal(onclick = {
                                        when(signInAndOutState){
                                            IN->{
                                                if (isInternetAvailable(context)){
                                                    attendanceInVM.insertAttendance(
                                                        onSuccess = {
                                                            Toast.makeText(
                                                                context,
                                                                "Success",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navToHome()
                                                        },
                                                        onError = {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    )
                                                }else{
                                                    Toast.makeText(
                                                        context,
                                                        "On Internet Connection",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                  }


                                            }
                                            OUT->{
                                                if (isInternetAvailable(context)){
                                                    attendanceInVM.updateAttendance(
                                                        onSuccess = {
                                                            Toast.makeText(
                                                                context,
                                                                "Success",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navToHome()
                                                        },
                                                        onError = {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    )
                                                }else{
                                                    Toast.makeText(
                                                        context,
                                                        "On Internet Connection",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }

                                            }
                                        }

                                    },
                                        text =
                                        when(signInAndOutState){
                                            IN->{
                                                "Sign in Student"
                                            }
                                            OUT->{
                                                "Sign out Student"
                                            }

                                            else -> {""}
                                        }
                                    )
                                }
                            }
                        }


                    }

                }

            }
            else{
//                Text(text = "Term is ended", fontSize = 20.sp)
                // Select Account Type
                AccountTypeScreen(
                    name = "Admin",
                    onFirstClick = {
                        scope.launch {
                            currentTermKey.saveKey(FIRST_TERM)

                        }
                        navToHome()
                    },
                    onSecondClick = { scope.launch {
                        currentTermKey.saveKey( SECOND_TERM )
                        }
                        navToHome()
                    },
                    onThirdClick = {
                        scope.launch {
                            currentTermKey.saveKey(THIRD_TERM)
                        }
                        navToHome()
                    },
                    firstText = "First Term",
                    secondText = "Second Term",
                    thirdText ="Third Term"
                )
            }
        }
    }else{
        AccountTypeScreen(
            name = "Admin",
            onFirstClick = {
                scope.launch {
                    currentTermKey.saveKey(FIRST_TERM)
                }
                sessionVM.termName.value = currentTerm.value
            },
            onSecondClick = { scope.launch {
                currentTermKey.saveKey(SECOND_TERM)}
            },
            onThirdClick = {
                scope.launch {
                    currentTermKey.saveKey(THIRD_TERM )
                }
                sessionVM.termName.value = currentTerm.value },
            firstText = "First Term",
            secondText = "Second Term",
            thirdText ="Third Term"
        )
    }
}



private fun termChecker(
    inputDateText: MutableState<String>,
    currentDate: LocalDate?,
//    currentTermExperation: MutableState<Boolean>,
): Boolean {
    val inputDate = try {
        LocalDate.parse(inputDateText.value, DateTimeFormatter.ISO_DATE)
    } catch (e: Exception) {
        null
    }

    var currentTermExperation = true

    if (inputDate != null) {
        val comparisonResult = inputDate.compareTo(currentDate)

        when {
            comparisonResult < 0 -> {
                // term ended
                currentTermExperation = false
            }

            comparisonResult == 0 -> {
//                currentTerm.value = "last day of the term"

            }

            else -> {
                // term is ongoing
                currentTermExperation = true
            }
        }
    }
    return currentTermExperation
}