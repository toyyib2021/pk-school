package com.stevdza.san.mongodemo.screenTeacher.dashboard

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.DrawerValue
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
import com.stevdza.san.mongodemo.MainActivity
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.StaffAttendancePdf
import com.stevdza.san.mongodemo.model.StaffStatusType
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.screenStudent.component.LoadingDialog
import com.stevdza.san.mongodemo.screenStudent.component.LoadingVM
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TitleAndSub
import com.stevdza.san.mongodemo.screenTeacher.timeInAndOut.SetTimeInAndOutVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.Constants.CLASS
import com.stevdza.san.mongodemo.util.Constants.SESSION
import com.stevdza.san.mongodemo.util.MonthlyAttendanceReportDoc
import com.stevdza.san.pk.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardStaff(
    onOnboardStaffClick:()->Unit,
    onStaffOnboardClick:()->Unit,
    onManualClick:()->Unit,
    navToScanCardScreen:()->Unit,
    navToSwitchAccount:()->Unit,
    onFullDetailsClick:()->Unit,
    navToOldStaffAttendance:(String)->Unit,
    navToStaffAttendance:(String)->Unit,
    navToStaffAttendanceMonth:(String)->Unit,
    navToAbsentRecord:()->Unit,
    navToActivateStaff:()->Unit,
    navToTimeInOut:()->Unit,
){

    val staffDasboardVM: StaffDashboardVM = viewModel()
    val loadingAnimationVM: LoadingVM = viewModel()
    val timeInAndOutVM: SetTimeInAndOutVM = viewModel()

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    val currentTime = LocalTime.now()
    val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))

//    Log.e(TAG, "Dashboard: -> $roundedNumber", )

    val context = LocalContext.current

    val pdfDocumentsGenerator = MonthlyAttendanceReportDoc(context)

    // Setting Time In And Out
    val data= timeInAndOutVM.data.value
    val timeInOut = data.find { it._id.toHexString() == timeInAndOutVM.objectId.value }
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

    var month by remember { mutableStateOf("") }
    val attendanceListMonth = staffDasboardVM.teacherAttendanceData.value.filter { it.month == month }

    val scope = rememberCoroutineScope()
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.data?.let {
                context.contentResolver.openOutputStream(it)?.let {
                    scope.launch {
                        pdfDocumentsGenerator.generateInvoicePdf(
                            outputStream = it,
                            staffAttendancePdf = StaffAttendancePdf(
                                schoolNme = "Pure Knowledge Academy",
                                title = "Attendance Report For the Month of $month, 2023",
                                date = formattedDate,
                                attendance = attendanceListMonth,
                                absentDays = "12",
                                presentDays = "50",
                                signatureUrl = "",
                                hourIn = hourIn.toInt(),
                                hourOut = hourOut.toInt(),
                                minIn = minIn.toInt(),
                                minOut = minOut.toInt(),
                                presentDaysPercent = "",
                                absentDaysPercent = "",
                            )
                        )
                    }
                }
            }
        }
    )



    var attendanceReportState by remember { mutableStateOf(Constants.DAILY_REPORT) }

    val attendanceList = staffDasboardVM.teacherAttendanceData.value.filter { it.date == formattedDate }
    val attendanceListForSignOut = staffDasboardVM.teacherAttendanceData.value.filter {
        it.date == formattedDate && it.timeOut != "" }
    val totalNoOfStudentInSchool = attendanceList.size
    val totalNoOfStudentOutOfSchool = attendanceListForSignOut.size

    // Staffs
    val staffs = staffDasboardVM.teacherData.value.filter { it.teacherStatus?.status == StaffStatusType.ACTIVE.status }
    val totalNoOfStaff = staffs.size


    var totalNoStudentClass by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    val classNames = staffs.map { it.surname }

// Absent Student
    val listOfStudentId: List<String> = attendanceList.map { it.staffId }
    val listOfAbsentStudents = staffs.filter { student ->
        student.staffId !in listOfStudentId
    }
    val totalNoOfAbsentStudent = listOfAbsentStudents.size

    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val accountTypeKey: AccountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")
    var backPressedTime: Long = 0

    var staffIdState by remember {
        mutableStateOf("")
    }
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



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier
                .width(200.dp)
                .height(400.dp)
            ) {
                DrawerContent(
                    navToSwitchAccount = {navToSwitchAccount()},
                    accountType = accountType.value,
                    navToTimeInOut = {navToTimeInOut()},
                    navToAbsentRecord = {navToAbsentRecord()},
                    navToActivateStaff = {navToActivateStaff()}
                )
            }
        },
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent ={
                when(staffIdState){
                    ""-> {
                        Card(modifier = Modifier.padding(12.dp), shape = RoundedCornerShape(15.dp)) {
                            Column(modifier= Modifier
                                .background(Blue),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Attendance", fontSize = 16.sp, color = Color.White)
                                Row(
                                    modifier= Modifier.fillMaxWidth(),
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
                                            modifier= Modifier.fillMaxWidth(),
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
                                            modifier= Modifier.fillMaxWidth(),
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

                    }
                    "Download"-> {
                        val months = listOf<String>(
                            " January", " February", " March", " April", " May", " June",
                            " July", " August", " September", " October", " November", " December"
                        )
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Blue)
                        ) {
                        months.forEach {
                            androidx.compose.material3.Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        month = it
                                        Intent(Intent.ACTION_CREATE_DOCUMENT)
                                            .apply {
                                                type = "application/pdf"
                                                putExtra(
                                                    Intent.EXTRA_TITLE,
                                                    "Staff Attendance for the month of $it.pdf"
                                                )
                                            }
                                            .also {
                                                createDocumentLauncher.launch(it)
                                            }
                                        scope.launch { sheetState.collapse() }
                                    },
                                text = it,
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            androidx.compose.material3.Divider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 3.dp
                            )
                        }
                    }


                    }else ->{
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Blue)
                    ) {
                        androidx.compose.material3.Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    navToStaffAttendance(staffIdState)
                                },
                            text = "Term",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        androidx.compose.material3.Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 3.dp
                        )
                        androidx.compose.material3.Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    navToStaffAttendanceMonth(staffIdState)
                                },
                            text = "Months",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        androidx.compose.material3.Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 3.dp
                        )

                    }

                    }
                }


            },
            content = { contentPadding ->
                // Screen content
                val it = contentPadding

                if (staffs.isNotEmpty()){
                    loadingAnimationVM.open.value = false
                }else{
                    loadingAnimationVM.open.value = true
                    if (loadingAnimationVM.open.value == true){
                        loadingAnimationVM.startThread(
                            backgroundTask = {
                                staffDasboardVM.getAllTeacher()
                                staffDasboardVM.getAllTeacherAttendance()
                            }
                        )
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
                                        onOnboardStaffClick = onOnboardStaffClick,
                                        onStaffOnboardClick = onStaffOnboardClick,
                                        onDailyReportClick = { attendanceReportState = Constants.DAILY_REPORT },
                                        onStaffCLick = { attendanceReportState = CLASS },
                                        onPastStaffClick = { attendanceReportState = SESSION },
                                        attendanceReportState = attendanceReportState,
                                        totalNoSignIn = totalNoOfStudentInSchool.toString(),
                                        totalNoSignOut =totalNoOfStudentOutOfSchool.toString(),
                                        totalNoAbsent = totalNoOfAbsentStudent.toString(),
                                        totalNoStudent = totalNoOfStaff.toString(),
                                        onFullDetailsClick = {onFullDetailsClick()},
                                        staffProfileList = staffs,
                                        onValueChange = {studentId = it},
                                        value =studentId,
                                        label = "Staff ID.",
                                        navToOldStaffAttendance = {navToOldStaffAttendance(it)},
                                        navToStaffAttendance = {
                                            staffIdState = it.staffId
                                            scope.launch {
                                                if (sheetState.isCollapsed) {
                                                    sheetState.expand()
                                                } else {
                                                    sheetState.collapse()
                                                }
                                            }
                                        },
                                        staffAttendanceList = attendanceList,
                                        onDownloadAttendanceClick = {
                                            staffIdState = "Download"
                                            scope.launch {
                                                if (sheetState.isCollapsed) {
                                                    sheetState.expand()
                                                } else {
                                                    sheetState.collapse()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            ForthSection(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                onTakeAttendanceCLick = {
                                    when(accountType.value){
                                        Constants.ATTENDANCE_COLLECTED -> {
                                            navToScanCardScreen()
                                        }
                                        else -> {
                                            staffIdState = ""
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



            },
            sheetPeekHeight = 0.dp
        )


    }


}


@Composable
fun DrawerContent(
    navToTimeInOut:()->Unit,
    navToSwitchAccount:()->Unit,
    accountType: String,
    navToAbsentRecord:()->Unit,
    navToActivateStaff:()->Unit
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


        if (accountType != Constants.ATTENDANCE_COLLECTED){

            Text(
                text = "Time In & Out",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navToTimeInOut()
                    }
                    .padding(8.dp)
            )


        }

        Text(
            text = "Absent Record",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navToAbsentRecord()
                }
                .padding(8.dp)
        )

        Text(
            text = "Activate Staff",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navToActivateStaff()
                }
                .padding(8.dp)
        )

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




        Spacer(modifier = Modifier.weight(1f))

        Divider()

        // Add other items here as needed
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    onOnboardStaffClick:()->Unit,
    onStaffOnboardClick:()->Unit,
    onDailyReportClick:()->Unit,
    onStaffCLick:()->Unit,
    onPastStaffClick:()->Unit,
    attendanceReportState: String,
    totalNoSignIn:String,
    totalNoSignOut:String,
    totalNoAbsent:String,
    totalNoStudent:String,
    onFullDetailsClick:()->Unit,
    staffProfileList: List<TeacherProfile>,
    staffAttendanceList: List<StaffAttendance>,
    navToStaffAttendance:(TeacherProfile)->Unit,
    onValueChange:(String)->Unit,
    value: String,
    label: String,
    navToOldStaffAttendance:(String)->Unit,
    onDownloadAttendanceClick:()->Unit

){

    var height by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
        // Second Session
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "onboarding", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth())
            Card(modifier= Modifier.clickable { onOnboardStaffClick() },
                shape= RoundedCornerShape(10.dp),
                backgroundColor = Color.White
            ) {

                Row(modifier = Modifier
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 10.dp),
                        text = "click here to onboard teaching and non-teaching staff's ", fontSize = 12.sp)
                    Column(modifier = Modifier.weight(6f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Onboard",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold,
                            color = Blue
                        )
                        Text(text = "Staff",
                            fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            color = Blue
                        )
                    }
                }
            }
            Spacer(modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth())
            Card(modifier= Modifier.clickable { onStaffOnboardClick() },
                shape= RoundedCornerShape(10.dp),
                backgroundColor = Color.White
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 10.dp),
                        text = "click here to view teaching and non-teaching staff onboarded", fontSize = 12.sp)
                    Column(modifier = Modifier.weight(6f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Staff",
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
                        }, backgroundColor = Color.White
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(
                            modifier = Modifier.clickable { onDailyReportClick() },
                            backgroundColor = if (attendanceReportState == Constants.DAILY_REPORT){
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
                            modifier = Modifier.clickable { onStaffCLick() },
                            backgroundColor = if (attendanceReportState == Constants.CLASS){
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
                                Text(text = "Staff's", fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(
                            modifier = Modifier.clickable { onPastStaffClick() },
                            backgroundColor = if (attendanceReportState == Constants.SESSION){
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
                                Text(text = """
                                    Past 
                                    Staff
                                """.trimIndent(), fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Column(modifier = Modifier
                    .weight(7f)
                    .height(heightInDp)
                ) {
                    Text(text = when(attendanceReportState){
                        Constants.DAILY_REPORT ->{
                            "Today's Attendance Report"
                        }
                        Constants.CLASS ->{
                            "Attendance Report For Staff"
                        }
                        Constants.SESSION ->{
                            "Attendance Report For Past Staff"
                        }
                        else -> {""}
                    }, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp))
                    Card(modifier = Modifier.fillMaxSize(),
                        backgroundColor = Color.White) {
                        when(attendanceReportState) {
                            Constants.DAILY_REPORT -> {
                                Column(modifier = Modifier
                                    .padding(start = 20.dp)
                                    .verticalScroll(state = rememberScrollState())
                                ) {
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total Staff Signed In",
                                        fontSize = 14.sp)
                                    Text(text = totalNoSignIn,
                                        fontSize = 16.sp, color = Blue, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total Staff Signed Out",
                                        fontSize = 14.sp)
                                    Text(text = totalNoSignOut,
                                        fontSize = 16.sp, color = Blue,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total Staff Absent",
                                        fontSize = 14.sp)
                                    Text(text = totalNoAbsent,
                                        fontSize = 16.sp, color = Blue,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp))
                                    Text(text = "Total No. of Staff ",
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

                            Constants.CLASS -> {

                                Column(modifier = Modifier
                                    .verticalScroll(state = rememberScrollState())
                                    .padding(10.dp)
                                ) {
                                    staffProfileList.forEach {
                                        val presentOrAbsent = staffAttendanceList.find { staffAttendance ->
                                            staffAttendance.staffId == it.staffId }
                                        Row(modifier = Modifier
                                            .clickable { navToStaffAttendance(it) }
                                            .fillMaxWidth(),) {

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 10.dp)
                                                    .weight(7f)
                                            ) {
                                                Text(
                                                    text ="${it.surname} ${it.otherNames}",
                                                    fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text =it.position,
                                                    fontSize = 10.sp)
                                            }

                                            if (presentOrAbsent != null){
                                                Icon(imageVector = Icons.Default.CheckBox, contentDescription = "")
                                            }

                                        }

                                        Divider(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp), thickness = 2.dp)


                                    }
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp))
                                    Text(
                                        modifier = Modifier.clickable { onDownloadAttendanceClick() },
                                        text = "Click here to download Staff Attendance",
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                }
                            }

                            Constants.SESSION -> {
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
                                        onClick = { navToOldStaffAttendance(value) },
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
    modifier: Modifier = Modifier,
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
private fun ForthSection(
    modifier : Modifier = Modifier,
    onTakeAttendanceCLick: () -> Unit) {
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