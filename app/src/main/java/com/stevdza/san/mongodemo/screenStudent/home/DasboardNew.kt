package com.stevdza.san.mongodemo.screenStudent.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.MainActivity
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.LoadingDialog
import com.stevdza.san.mongodemo.screenStudent.component.LoadingVM
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TitleAndSub
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants.ADMIN
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_COLLECTED
import com.stevdza.san.mongodemo.util.Constants.CLASS
import com.stevdza.san.mongodemo.util.Constants.DAILY_REPORT
import com.stevdza.san.mongodemo.util.Constants.DIRECTOR
import com.stevdza.san.mongodemo.util.Constants.SESSION
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.log

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardNew(
    navToParentOnboarding:()->Unit,
    navToParentOnboarded:()->Unit,
    navToStudentOnboardList:()->Unit,
    onManualClick:()->Unit,
    navToScanCardScreen:()->Unit,
    navToClass:()->Unit,
    navToSwitchAccount:()->Unit,
    navToUpdateStudentClass:()->Unit,
    navToSession:()->Unit,
    navToArms:()->Unit,
    onFullDetailsClick:()->Unit,
    navToAttendanceClass:(String)->Unit,
    navToAttendanceStudent:(String)->Unit,
    navToOldStudentAttendance:(String)->Unit,
    navToChooseAStudentForParentView:()->Unit,
    navToOboardAStudent:()->Unit,
    navToTimeInAndOut:()->Unit,
){

    val dasboardVM: DashboardVM= viewModel()
    val loadingAnimationVM: LoadingVM = viewModel()

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))

    Log.e(TAG, "Dashboard: formattedDate -> $formattedDate", )




    val attendanceList = dasboardVM.attendanceList.value.filter { it.date == formattedDate }
    val attendanceListForSignOut = dasboardVM.attendanceList.value.filter {
        it.date == formattedDate && it.studentOut != null }
    val totalNoOfStudentInSchool = attendanceList.size
    val totalNoOfStudentOutOfSchool = attendanceListForSignOut.size
    val students = dasboardVM.students.value.filter { it.studentStatus?.status == StudentStatusType.ACTIVE.status }
    val totalNoOfStudent = students.size
    var attendanceReportState by remember { mutableStateOf(DAILY_REPORT) }
    var parentCardBack by remember { mutableStateOf(false) }
    var studentCardBack by remember { mutableStateOf(false) }

    var totalNoStudentClass by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var parentAndSchool by remember { mutableStateOf(true) }

//    val classNames = dasboardVM.classNames.value.map { it.className }
    val classNamesTwo = dasboardVM.classNames.value.sortedByDescending { it._id }
    classNamesTwo.forEach {
        Log.e(TAG, "Dashboard: ${it.className}", )
    }


// Absent Student
    val listOfStudentId: List<String> = attendanceList.map { it.studentId }
    val listOfAbsentStudents = students.filter { student ->
            student.studentApplicationID !in listOfStudentId
        }
    val totalNoOfAbsentStudent = listOfAbsentStudents.size

    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val accountTypeKey: AccountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")
    var backPressedTime: Long = 0

    BackHandler() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            val activity: MainActivity = MainActivity()
            // on below line we are finishing activity.
            activity.finish()
            System.exit(0)
        } else {
            Log.i(TAG, "dashboardNavGraph: press back button again ")
        }
        backPressedTime = System.currentTimeMillis()

    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier
                .width(200.dp)
                .height(400.dp)
            ) {
                DrawerContent(
                    navToClass = { navToClass() },
                    navToSession = {navToSession()},
                    navToUpdateStudentClass={navToUpdateStudentClass()},
                    navToSwitchAccount = {navToSwitchAccount()},
                    navToArms = {navToArms()},
                    navToParentAccount = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                        parentAndSchool = !parentAndSchool},
                    parentAndSchool = parentAndSchool,
                    accountType = accountType.value,
                    navToTimeInAndOut = {navToTimeInAndOut()}
                )
            }
        },
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent ={

                Card(modifier = Modifier.padding(12.dp), shape = RoundedCornerShape(15.dp)) {
                    Column(modifier= Modifier
                        .background(Blue),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Attendance", fontSize = 16.sp, color = Color.White)
                        Row(
                            modifier=Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .weight(5f)
                                    .clickable {
                                        navToScanCardScreen()
                                    },
                                elevation = 10.dp,
                                shape = RoundedCornerShape(10.dp),
                                backgroundColor = Color.White,
                                contentColor = Color.Black
                            ) {
                                Column(
                                    modifier=Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Text(modifier = Modifier
                                        .padding(10.dp),
                                        text = "By Card", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .weight(5f)
                                    .clickable { onManualClick() },
                                elevation = 10.dp,
                                shape = RoundedCornerShape(10.dp),
                                backgroundColor = Color.White,
                                contentColor = Color.Black
                            ) {
                                Column(
                                    modifier=Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Text(modifier = Modifier
                                        .padding(10.dp),
                                        text = "Manually", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

            },
            content = { contentPadding ->
                    // Screen content
                    val it = contentPadding

                if (students.isNotEmpty()){
                    loadingAnimationVM.open.value = false
                }else{
                    loadingAnimationVM.open.value = true
                    if (loadingAnimationVM.open.value == true){
                        loadingAnimationVM.startThread(
                            backgroundTask = {
                                dasboardVM.getAttendance()
                                dasboardVM.getAllStudent()
                                dasboardVM.getAllClasses()
                            }
                        )
                    }

                }



                if (loadingAnimationVM.open.value == true){
                    LoadingDialog(
                        viewModel = loadingAnimationVM
                    )
                }else{
                    if (parentAndSchool){
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .background(Cream)
                        ) {
                            SectionOne(
                                modifier = Modifier.weight(1f),
                                onMenuClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                },
                                title = "Welcome ${accountType.value}"
                            )
                            LazyColumn(
                                modifier = Modifier.weight(8f)
                            ) {
                                item {
                                    DashboardScreen(
                                        onParentOnboardClick = navToParentOnboarding,
                                        onStudentOnboardingCardClick = {
                                            when(accountType.value){
                                                ADMIN -> {studentCardBack = !studentCardBack }
                                                DIRECTOR -> {studentCardBack = !studentCardBack }
                                                ATTENDANCE_COLLECTED -> {navToStudentOnboardList()}
                                            }
                                        },
                                        onDailyReportClick = { attendanceReportState = DAILY_REPORT },
                                        onClassCLick = { attendanceReportState = CLASS },
                                        onSessionClick = { attendanceReportState = SESSION },
                                        attendanceReportState = attendanceReportState,
                                        totalNoSignIn = totalNoOfStudentInSchool.toString(),
                                        totalNoSignOut =totalNoOfStudentOutOfSchool.toString(),
                                        totalNoAbsent = totalNoOfAbsentStudent.toString(),
                                        totalNoStudent = totalNoOfStudent.toString(),
                                        onFullDetailsClick = {onFullDetailsClick()},

                                        parentCardBack = parentCardBack,
                                        onParentOnbardClick = { navToParentOnboarding() },
                                        onViewParentClick = { navToParentOnboarded() },
                                        onParentCardClick = {
                                            when(accountType.value){
                                                ADMIN -> {parentCardBack = !parentCardBack }
                                                DIRECTOR -> {parentCardBack = !parentCardBack }
                                                ATTENDANCE_COLLECTED -> {navToParentOnboarded()}
                                            }
                                        },
                                        classNames =classNamesTwo,
                                        navToAttendanceClass = {navToAttendanceClass(it)},
                                        navToAttendanceStudent = {navToAttendanceStudent(it)},
                                        dasboardVM = dasboardVM,
                                        onValueChange = {studentId = it},
                                        value =studentId,
                                        label = "Student Application No.",
                                        navToOldStudentAttendance = {navToOldStudentAttendance(it)},
                                        studentCardBack = studentCardBack,
                                        onViewStudentClick = navToStudentOnboardList,
                                        onStudentOnbardClick ={navToOboardAStudent()},
                                        onstudentCardBackClick = {
                                            when(accountType.value){
                                                ADMIN -> {studentCardBack = !studentCardBack }
                                                DIRECTOR -> {studentCardBack = !studentCardBack }
                                                ATTENDANCE_COLLECTED -> {navToParentOnboarded()}
                                            }
                                        },
                                        studentData = dasboardVM.students.value

                                    )
                                }
                            }
                            ForthSection(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                onTakeAttendanceCLick = {
                                    when(accountType.value){
                                        ATTENDANCE_COLLECTED -> {
                                            navToScanCardScreen()
                                        }
                                        else -> {
                                            scope.launch {
                                                if (sheetState.isCollapsed) {
                                                    sheetState.expand()
                                                } else {
                                                    sheetState.collapse()
                                                }
                                            }
                                        }
                                    }

                                },)

                        }
                    }
                    else{
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Cream),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Continue As a Parent", modifier = Modifier.clickable { navToChooseAStudentForParentView() })
                        }
                    }
                }

                },
            sheetPeekHeight = 0.dp
        )


    }


}


@Composable
fun DrawerContent(
    navToClass:()->Unit,
    navToArms:()->Unit,
    navToSession:()->Unit,
    navToUpdateStudentClass:()->Unit,
    navToSwitchAccount:()->Unit,
    parentAndSchool: Boolean,
    navToParentAccount:()->Unit,
    accountType: String,
    navToTimeInAndOut:()->Unit,

) {
    Column(
        modifier = Modifier
            .background(Cream)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(modifier = Modifier.weight(4f),
                painter = painterResource(id = R.drawable.pk_logo),
                contentDescription = "pk_logo")
            Column(modifier = Modifier.weight(6f)) {
                Text(
                    text = "PURE",
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Knowledge Technology ",
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 12.sp,
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Class", fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navToClass() }
                .padding(8.dp))
        Text(
            text = "Arms", fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navToArms() }
                .padding(8.dp))


        if (accountType != ATTENDANCE_COLLECTED){

            Text(
                text = "Session",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navToSession()
                    }
                    .padding(8.dp)
            )

            Text(
                text = "Time In & Out",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navToTimeInAndOut()
                    }
                    .padding(8.dp)
            )

            Text(
                text = "Update Student Class",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navToUpdateStudentClass()
                    }
                    .padding(8.dp)
            )
        }

        Text(
            text = "Switch Account",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navToSwitchAccount()
                }
                .padding(8.dp)
        )

//        Text(
//            text = if (parentAndSchool){
//                "Parent View"
//            }else{
//                "School View"
//                 },
//            fontSize = 14.sp,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable {
//                    navToParentAccount()
//                }
//                .padding(8.dp)
//        )

        Spacer(modifier = Modifier.weight(1f))

        Divider()

        // Add other items here as needed
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    onParentOnboardClick:()->Unit,
    onStudentOnboardingCardClick:()->Unit,
    onDailyReportClick:()->Unit,
    onClassCLick:()->Unit,
    onSessionClick:()->Unit,
    attendanceReportState: String,
    totalNoSignIn:String,
    totalNoSignOut:String,
    totalNoAbsent:String,
    totalNoStudent:String,
    onFullDetailsClick:()->Unit,
    parentCardBack: Boolean,
    onParentOnbardClick:()->Unit,
    onViewParentClick:()->Unit,
    onParentCardClick:()->Unit,
    classNames: List<ClassName>,
    navToAttendanceClass:(String)->Unit,
    navToAttendanceStudent:(String)->Unit,
    dasboardVM: DashboardVM,
    onValueChange:(String)->Unit,
    value: String,
    label: String,
    navToOldStudentAttendance:(String)->Unit,
    studentCardBack: Boolean,
    onViewStudentClick: ()->Unit,
    onStudentOnbardClick:()->Unit,
    onstudentCardBackClick:()->Unit,
    studentData: List<StudentData>

){

    var height by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    )
    {


        // Second Session
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "onboarding", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth())
            Card(modifier=Modifier.clickable { onParentOnboardClick() },
                shape= RoundedCornerShape(10.dp),
                backgroundColor = Color.White,
                elevation = 5.dp
            ) {
                if (parentCardBack){
                    Row(modifier = Modifier
                        .padding(10.dp)
                        .clickable { onParentCardClick() },
                        verticalAlignment = Alignment.CenterVertically) {

                        Card(backgroundColor = Cream, shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .weight(5f)
                                .padding(20.dp)
                                .clickable { onViewParentClick() }
                        ) {
                            Text(
                                modifier = Modifier.padding(7.dp),
                                text = "View Onboard Parent's",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                        Card(backgroundColor = Cream, shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .weight(5f)
                                .padding(20.dp)
                                .clickable { onParentOnbardClick() }
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(7.dp)
                                    .fillMaxSize(),
                                text = "Onboard A Parent",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                }else{
                    Row(modifier = Modifier
                        .padding(10.dp)
                        .clickable { onParentCardClick() },
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier
                            .weight(4f)
                            .padding(horizontal = 10.dp),
                            text = "click here to onboard parent / guardian / privilege person", fontSize = 12.sp)
                        Column(modifier = Modifier.weight(6f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(text = "Onboard",
                                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                color = Blue
                            )
                            Text(text = "Parent / Guardian",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = Blue
                            )
                        }
                    }
                }

            }
            Spacer(modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth())
            Card(modifier=Modifier.clickable { onStudentOnboardingCardClick() },
                shape= RoundedCornerShape(10.dp),
                backgroundColor = Color.White,
                elevation = 5.dp
            ) {

                if (studentCardBack){
                    Row(modifier = Modifier
                        .padding(10.dp)
                        .clickable { onstudentCardBackClick() },
                        verticalAlignment = Alignment.CenterVertically) {

                        Card(backgroundColor = Cream, shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .weight(5f)
                                .padding(20.dp)
                                .clickable { onViewStudentClick() }
                        ) {
                            Text(
                                modifier = Modifier.padding(7.dp),
                                text = "View Onboard Student's",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                        Card(backgroundColor = Cream, shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .weight(5f)
                                .padding(20.dp)
                                .clickable { onStudentOnbardClick() }
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(7.dp)
                                    .fillMaxSize(),
                                text = "Onboard A Student",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                }else{
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier
                            .weight(4f)
                            .padding(horizontal = 10.dp),
                            text = "click here to view student onboard by their classes", fontSize = 12.sp)
                        Column(modifier = Modifier.weight(6f), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            Text(text = "Student",
                                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                color = Blue
                            )
                            Text(text = "onboard",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = Blue
                            )
                        }
                    }
                }


            }
        }

        // Third Session
        val heightInDp = with(LocalDensity.current) { height.toDp() }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp))
        Column(modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Attendance Report", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(3f)
                        .onSizeChanged { newSize ->
                            // Update the height when the size changes
                            height = newSize.height
                        }, backgroundColor = Color.White,
                    elevation = 5.dp
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(
                            modifier = Modifier.clickable { onDailyReportClick() },
                            backgroundColor = if (attendanceReportState == DAILY_REPORT){
                                Cream
                            }else{
                                Color.White },
                            shape = CircleShape
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.Assessment,
                                    contentDescription = "report")
                                Text(text = """
                                    Daily 
                                    Report
                                """.trimIndent(), fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }


                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(
                            modifier = Modifier.clickable { onClassCLick() },
                            backgroundColor = if (attendanceReportState == CLASS){
                                Cream
                            }else{
                                Color.White },
                            shape = CircleShape
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.Class,
                                    contentDescription = "Classes")
                                Text(text = "Class", fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(
                            modifier = Modifier.clickable { onSessionClick() },
                            backgroundColor = if (attendanceReportState == SESSION){
                                Cream
                            }else{
                                Color.White },
                            shape = CircleShape
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.School,
                                    contentDescription = "session")
                                Text(text = "Student", fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Column(modifier = Modifier
                    .weight(7f)
                    .height(heightInDp)) {
                    Text(text = when(attendanceReportState){
                        DAILY_REPORT->{
                            "Today's Attendance Report"
                        }
                        CLASS->{
                            "Attendance Report By Class"
                        }
                        SESSION->{
                            "Attendance Report By Student"
                        }
                        else -> {""}
                    }, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp))
                    Card(modifier = Modifier.fillMaxSize(),
                         elevation = 10.dp) {
                        when(attendanceReportState) {
                            DAILY_REPORT -> {
                                Column(modifier = Modifier
                                    .padding(start = 20.dp)
                                    .verticalScroll(state = rememberScrollState())
                                ) {
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total Student Signed In",
                                        fontSize = 14.sp)
                                    Text(text = totalNoSignIn,
                                        fontSize = 16.sp, color = Blue, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total Student Signed Out",
                                        fontSize = 14.sp)
                                    Text(text = totalNoSignOut,
                                        fontSize = 16.sp, color = Blue,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total Student Absent",
                                        fontSize = 14.sp)
                                    Text(text = totalNoAbsent,
                                        fontSize = 16.sp, color = Blue,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total No. of Student ",
                                        fontSize = 14.sp)
                                    Text(text = totalNoStudent,
                                        fontSize = 16.sp, color = Blue,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.padding(10.dp))
                                    Text(modifier = Modifier.clickable { onFullDetailsClick() },
                                            text = "Click here to get full report ",
                                        fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))

                                }

                            }

                            CLASS -> {

                                Column(modifier = Modifier
                                    .verticalScroll(state = rememberScrollState())
                                    .padding(10.dp)
                                ) {
                                    classNames.forEach {
                                            var hide by remember { mutableStateOf(false) }
                                        Row(modifier = Modifier
                                            .clickable { hide = !hide }
                                            .fillMaxWidth(),) {

                                            val studentClass =
                                                studentData.filter { cless ->
                                                    cless.cless == it.className &&
                                                            cless.studentStatus?.status == StudentStatusType.ACTIVE.status
                                                }
                                            val totalStudentsForAClass = studentClass.size
                                            totalStudentsForAClass.toString()

                                            val currentDate = LocalDate.now()
                                            val formattedDate =
                                                currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))

                                            val presentStudentForClass =
                                                dasboardVM.attendanceList.value.filter {att->
                                                    att.date == formattedDate && att.cless == it.className
                                                }

                                            val totalPresentForClass = presentStudentForClass.size

                                            Text(
                                                modifier = Modifier.weight(7f),
                                                text =it.className,
                                                fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                modifier = Modifier.weight(3f),
                                                text =" $totalPresentForClass/$totalStudentsForAClass",
                                                fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Divider(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp), thickness = 2.dp)
                                        if (hide){
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Card(modifier = Modifier.weight(4f),
                                                    onClick = { navToAttendanceClass(it.className) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    backgroundColor = Blue,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(modifier = Modifier
                                                        .padding(5.dp)
                                                        .fillMaxWidth(),
                                                        text = "Daily Report", fontSize = 11.sp, textAlign = TextAlign.Center)
                                                }
                                                Spacer(modifier = Modifier.weight(1f))
                                                Card(modifier = Modifier.weight(5f),
                                                    onClick = { navToAttendanceStudent(it.className) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    backgroundColor = Blue,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(modifier = Modifier
                                                        .padding(5.dp)
                                                        .fillMaxWidth(),
                                                        text = "Student Report", fontSize = 12.sp, textAlign = TextAlign.Center)
                                                }

                                            }
                                        }

                                    }
                                    }
                            }

                            SESSION -> {
                                Column(modifier = Modifier
                                    .verticalScroll(state = rememberScrollState())
                                    .padding(10.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    TextFieldForm(
                                        value = value,
                                        label = label,
                                        onValueChange = {onValueChange(it)})
                                    Card(modifier = Modifier.fillMaxWidth(),
                                        onClick = { navToOldStudentAttendance(value) },
                                        shape = RoundedCornerShape(10.dp),
                                        backgroundColor = Blue,
                                        contentColor = Color.White
                                    ) {
                                        Text(modifier = Modifier
                                            .padding(5.dp)
                                            .fillMaxWidth(),
                                            text = "Done", fontSize = 11.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }

        // Forth Session


    }
}

@Composable
private fun SectionOne(
    modifier: Modifier= Modifier,
    onMenuClick: () -> Unit,
    title: String
) {
    Row(
        modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.clickable { onMenuClick() },
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu"
            )
        }
        TitleAndSub(
            title = title,
            subTitle = "Let's get start with today's attendance"
        )
    }
}

@Composable
private fun ForthSection(modifier : Modifier= Modifier, onTakeAttendanceCLick: () -> Unit) {
    Card(
        modifier
            .padding(10.dp)
            .clickable { onTakeAttendanceCLick() },
        shape = RoundedCornerShape(20.dp),
        contentColor = Color.White,
        backgroundColor = Blue
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(5f)
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ">>>>>>>>>", fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(5f)
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Student In & Out", fontSize = 12.sp)
            }
        }
    }
}