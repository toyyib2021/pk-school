package com.stevdza.san.mongodemo.screenTeacher.dailyReport

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.TeacherAbsentAttendance
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.screenStudent.classAttendance.parseDate
import com.stevdza.san.mongodemo.screenStudent.component.BottomBar
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.InAndOutTableHeader
import com.stevdza.san.mongodemo.screenStudent.component.StaffAbsentCard
import com.stevdza.san.mongodemo.screenStudent.component.StaffAttendanceList
import com.stevdza.san.mongodemo.screenStudent.component.StaffCard
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TopBarWithDate
import com.stevdza.san.mongodemo.screenStudent.home.SessionVM
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.SearchTopBar
import com.stevdza.san.mongodemo.screenTeacher.timeInAndOut.SetTimeInAndOutVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StaffDailyAttendance(
    onBackClick: ()-> Unit
){

    val timeInAndOutVM: SetTimeInAndOutVM = viewModel()
    val dailyAttendanceVM: DailyStaffAttendanceVM = viewModel()
    val sessionVM: SessionVM = viewModel()

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )


    // Setting Time In And Out
    val data= timeInAndOutVM.data.value
    val timeInOut = data.lastOrNull()
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
    Log.e(TAG, "StaffDailyAttendance: timeInMin-> $minOut", )
    Log.e(TAG, "StaffDailyAttendance: timeInHour-> $hourOut", )



    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentDate = LocalDate.now()
    var dateValue by remember { mutableStateOf(currentDate.toString()) }
    val currentTime = LocalDateTime.now()
    val timeFormat = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))




    val i = parseDate(dateValue, "yyyy-MM-dd")
    Log.e(ContentValues.TAG, "ClassAttendance: $i", )
    val datey = i?.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))

    val students = dailyAttendanceVM.staffs.value
    val dailyAttendanceList = dailyAttendanceVM.staffAttendance.value
        .filter { it.date == datey }


    var openDialog by remember { mutableStateOf(false) }
    Log.e(ContentValues.TAG, "currentDate: $currentDate", )
    val listOfStudentId: List<String> = dailyAttendanceList.map { it.staffId }
    val listOfAbsentStudents = students.filter { student ->
        student.staffId !in listOfStudentId
    }


    var presentAndAbsentState by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf("") }
    var valueAbsent by remember { mutableStateOf("") }
    var reasonForAbsentEdit by remember { mutableStateOf(false) }


    var reasonForAbsentSheet by remember { mutableStateOf(false) }

    dailyAttendanceVM.staffAttendance.value.forEach {
        Log.e(ContentValues.TAG, "ClassAttendance: ${it.session}", )
    }

    val absent = dailyAttendanceVM.absentStaff.value.filter {
        it.date == formattedDate
    }
    val listOfAbsentWithReasonId = absent.map { it.staffId }
    val listAbsentStaff = listOfAbsentStudents.filter {
        it.staffId !in listOfAbsentWithReasonId
    }

    Log.e(TAG, "StaffDailyAttendance: absent-> ${absent.size}", )

    val formattedDateForMonth = currentDate.format(DateTimeFormatter.ofPattern("dd, MMMM, yyyy"))
    val convertedList = formattedDateForMonth.split(",")
    val monthFromDate = convertedList[1]


    val sessions = sessionVM.sessions.value
    val w= sessions.firstOrNull()
    val currentSessions = w?.year?: ""

    val accountTypeKey: AccountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent ={
            if (reasonForAbsentSheet){
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    backgroundColor =  Color.Blue
                ) {

                    Column( modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Spacer(modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth())
                        TextFieldForm(value = dailyAttendanceVM.reason.value , label = "Reason",
                            onValueChange = { dailyAttendanceVM.reason.value = it },
                            labelColor = Color.White
                        )
                        Spacer(modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth())
                        if (reasonForAbsentEdit){

                            BtnNormal(
                                onclick = { dailyAttendanceVM.updateStudentStatus(
                                    onError = {}, onSuccess = {
                                        dailyAttendanceVM.reason.value = ""
                                    })
                                    scope.launch {
                                        sheetState.collapse() }
                                },
                                text = "Done"
                            )
                        }else{
                            BtnNormal(
                                onclick = {
                                    dailyAttendanceVM.insertAbsentStaff(
                                    onError = {}, onSuccess = {
                                        dailyAttendanceVM.reason.value = ""
                                    }, emptyFeilds = {
                                        Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show() })
                                    scope.launch {
                                        sheetState.collapse() }
                                    },
                                text = "Done"
                            )
                        }

                    }
                }
            }else{
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                    backgroundColor =  Color.Blue
                ) {

                    Column( modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Spacer(modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth())
                        TextFieldForm(value = dateValue , label = "Enter a Date (YYYY-MM-DD):", onValueChange = {dateValue = it},
                            backgroundColor = if (dateValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                Color.White
                            }else{
                                Color.Red
                            },
                            cursorColor = if (dateValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                Color.Black
                            }else{
                                Color.White
                            },
                            labelColor =  Color.White,
                            cardBackgroundColor =  if (dateValue.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                Color.White
                            }else{
                                Color.Red
                            },
                        )
                        Spacer(modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth())

                    }
                }
            }

        },
        content = { contentPadding ->
            DailyAttendanceStaffScreen(
                onBackClick = { onBackClick() },
                title = "Daily Report",
                date = datey?: "",
                value = value,
                onValueChange = {value = it},
                onSearchClick = {
                   dailyAttendanceVM.attendanceSearchName.value = value
                    dailyAttendanceVM.getStaffWithName()
                },
                onFirstBtnClick = { presentAndAbsentState = true },
                onSecondBtnClick = { presentAndAbsentState = false },
                firstText = "Present",
                secondText = "Absent",
                presentAndAbsentState = presentAndAbsentState,
                label = "Enter Staff Name Here",
                ondateClick = {
                    reasonForAbsentSheet = false
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            sheetState.expand()
                        } else {
                            sheetState.collapse()
                        }
                    }
                },
                valueAbsent = valueAbsent,
                onValueAbsentChange = {valueAbsent = it },
                onSearchAbsentClick = {
                    dailyAttendanceVM.absentNameSearch.value = valueAbsent
                    dailyAttendanceVM.getStaffWithSurname()
                },
                labelAbsent = "Enter Staff Name Here",
                onStaffClick = {
                    dailyAttendanceVM.name.value = "${it.surname} ${it.otherNames}"
                    dailyAttendanceVM.position.value = it.position
                    dailyAttendanceVM.date.value = formattedDate
                    dailyAttendanceVM.month.value =monthFromDate
                    dailyAttendanceVM.session.value =currentSessions
                    dailyAttendanceVM.term.value =""
                    dailyAttendanceVM.staffId.value =it.staffId


                    reasonForAbsentSheet = true
                    reasonForAbsentEdit = false
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            sheetState.expand()
                        } else {
                            sheetState.collapse()
                        }
                    }
                },
                staffAttendance = dailyAttendanceList,
                absentStaff = listAbsentStaff,
                absentStaffAttendance = absent,
                absentStaffReason = listOfAbsentStudents,
                accountType = accountType.value,
                onDeleteClick = {
                    dailyAttendanceVM.objectID.value =it._id.toHexString()
                    openDialog = true
                },
                onEditClick = {
                    reasonForAbsentSheet = true
                    reasonForAbsentEdit = true
                    dailyAttendanceVM.objectID.value =it._id.toHexString()
                              dailyAttendanceVM.reason.value = it.reason
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            sheetState.expand()
                        } else {
                            sheetState.collapse()
                        }
                    }
                },
                openDialog =openDialog,
                onDismissRequest = {openDialog = false},
                confirmButton = {
                    dailyAttendanceVM.deleteAbsentTeacher(onError = {}, onSuccess = {}, emptyFeild = {})
                    openDialog = false
                                },
                dismissButton = {openDialog = false},
                hourIn = hourIn.toInt(),
                hourOut =  hourOut.toInt(),
                minIn = minIn.toInt(),
                minOut = minOut.toInt()
            )
        },
        sheetPeekHeight = 0.dp
    )

}




@Composable
fun DailyAttendanceStaffScreen(
    onBackClick:()->Unit,
    title: String,
    date: String,
    value: String,
    onValueChange:(String)->Unit,
    onSearchClick:()->Unit,
    staffAttendance: List<StaffAttendance>,
    absentStaff: List<TeacherProfile>,
    absentStaffAttendance: List<TeacherAbsentAttendance>,
    onFirstBtnClick:()->Unit,
    onSecondBtnClick:()->Unit,
    firstText:String,
    secondText:String,
    presentAndAbsentState: Boolean,
    label: String,
    ondateClick: ()->Unit,
    valueAbsent: String,
    onValueAbsentChange:(String)->Unit,
    onSearchAbsentClick: () -> Unit,
    labelAbsent: String,
    onStaffClick: (TeacherProfile)->Unit,
    absentStaffReason: List<TeacherProfile>,
    accountType: String,
    onDeleteClick:(TeacherAbsentAttendance)->Unit,
    onEditClick:(TeacherAbsentAttendance)->Unit,
    openDialog: Boolean,
    onDismissRequest: ()-> Unit,
    confirmButton: ()-> Unit,
    dismissButton: ()-> Unit,
    hourIn : Int,
    hourOut :Int ,
    minIn : Int,
    minOut : Int
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(10.dp)
    ) {
//        val openDialog = remember { mutableStateOf(true) }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = { onDismissRequest() },
                title = {
                    Text(text = "Delete Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Text("Are you sure you want to delete this information?", fontSize = 16.sp)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            confirmButton()
                        }) {
                        Text("Yes",style = TextStyle(color = Color.White))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            dismissButton()
                        }) {
                        Text("No",style = TextStyle(color = Color.White))
                    }
                },
                backgroundColor = Color.Blue,
                contentColor = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            TopBarWithDate(
                onBackClick = { onBackClick() },
                title = title,
                date = date, ondateClick = {ondateClick()})
        }
        Column(modifier = Modifier.weight(8f)) {

            LazyColumn{
                item{
                    if (presentAndAbsentState){
                        SearchTopBar(
                            value =value,
                            onValueChange = {onValueChange(it) },
                            onSearchClick ={ onSearchClick()},
                            label = label
                        )
                    }else{
                        SearchTopBar(
                            value =valueAbsent,
                            onValueChange = {onValueAbsentChange(it) },
                            onSearchClick ={ onSearchAbsentClick()},
                            label = labelAbsent
                        )
                    }

                }
                if (presentAndAbsentState){
                    item{
                        InAndOutTableHeader()
                    }
                    items(staffAttendance){
                        StaffAttendanceList(
                            name = it.name,
                            position = it.position,
                            date = it.date,
                            timeIn =it.timeIn,
                            timeOut = it.timeOut,
                            hourIn = hourIn,
                            hourOut =hourOut ,
                            minIn = minIn,
                            minOut = minOut
                        )
                        Log.e(TAG, "DailyAttendanceScreen: ${it._id.toHexString()}",)
                    }


                }
                else {
                    items(absentStaffAttendance) { absent ->
                        val staff = absentStaffReason.find { it.staffId == absent.staffId }
                        Log.e(TAG, "DailyAttendanceStaffScreen: staff -> ${absent.staffId}",)
                        if (staff != null) {
                            val bit = staff.pics.let { it1 -> convertBase64ToBitmap(it1) }
                            StaffAbsentCard(
                                staff = staff,
                                bitmap = bit.asImageBitmap(),
                                reasonForAbsent = absent.reason,
                                absentDate = absent.date,
                                delete = {
                                    onDeleteClick(absent) },
                                edit = {
                                       onEditClick(absent)
                                },
                                accountType = accountType
                            )
                        }
                    }


                    items(absentStaff) {
                        val bit = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
                        StaffCard(
                            staff = it,
                            bitmap = bit.asImageBitmap(),
                            onStaffClick = { onStaffClick(it) }
                        )
                    }

                }
            }

        }
        BottomBar(
            modifier = Modifier.weight(1f),
            onFirstBtnClick = { onFirstBtnClick() },
            onSecondBtnClick = { onSecondBtnClick() },
            firstText = firstText,
            secondText = secondText
        )
    }
}



//@Preview
//@Composable
//fun StaffDailyAttendancePreview(){
//    StaffDailyAttendance(
//        onBackClick = {}
//    )
//}