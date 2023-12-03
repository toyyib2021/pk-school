package com.stevdza.san.mongodemo.screenStudent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.AttendanceStudent
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.StudentIn
import com.stevdza.san.mongodemo.model.StudentOut
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.parentView.DashboardVM
import com.stevdza.san.mongodemo.screenTeacher.movement.MovementVM
import com.stevdza.san.mongodemo.util.Constants.FIRST_TERM


data class AttendanceTest(val date: String, val studentName: String, val timeIn: String, val timeOut: String)
val attendanceList = listOf<AttendanceTest>(
    AttendanceTest(date = "2023-05-02", studentName = "Faredat Badmus", timeIn = "12:00", timeOut = "4:00"),
    AttendanceTest(date = "2023-05-02", studentName = "Musa Badmus", timeIn = "12:00", timeOut = "4:00"),
    AttendanceTest(date = "2023-05-02", studentName = "Saheed Badmus", timeIn = "12:00", timeOut = "4:00"),

)

// Extract unique dates from the attendanceList
val uniqueDates = attendanceList.distinctBy { it.date }

val number = 33.0

// Format the number to a string with two decimal places
val formattedNumber = String.format("%.2f", number)

// Parse the formatted string back to a double if needed
val roundedNumber = formattedNumber.toDouble()

//val bitmap: Bitmap = yourBitmap // Replace with your Bitmap object
//val sizeInBytes = bitmap.byteCount
//
//val sizeInKB = sizeInBytes / 1024.0
//val sizeInMB = sizeInKB / 1024.0



val myList = listOf("item1", "item2", "item3")

// Convert the list to a string using joinToString
val convertedString = myList.joinToString(",") // Use a delimiter of your choice


val myString = "item1,item2,item3"


// Convert the string to a list using split
val convertedList = myString.split(",") // Use the same delimiter


private val studentOne = AttendanceStudent().apply {
    familyId = "652e34fd987940570934ab9b"
    session = "2023/2024"
    term = FIRST_TERM
    date = "Fri, Nov 3, '23"
    cless = "Nursery Two"
    studentName = "Yusuf Basit"
    studentId = "ads/2023/586"
    studentIn = StudentIn().apply {
        studentBroughtBy ="Yusuf Monsturat"
        time = "12:26:59"
    }
    studentOut = StudentOut().apply {
        studentBroughtBy ="Yusuf Monsturat"
        time = "13:26:59"
    }
}

val testAttendanceList = listOf<AttendanceStudent>(
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,

    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,

    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,
    studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne, studentOne,

    studentOne, studentOne, studentOne, studentOne,
)



val staffFirstTermAttendance = StaffAttendance().apply {
    owner_id = "pk"
    staffId = "AIA/23/2025"
    name = "Toyyib Badmus"
    position = "Admin"
    timeIn = "02:12"
    timeOut = "04:24"
    date = "Fri, Nov 3, '23"
    month = "November"
    term = "First"
    session = "2023/2024"

}
val staffSecondTermAttendance = StaffAttendance().apply {
    owner_id = "pk"
    staffId = "AIA/23/2025"
    name = "Toyyib Badmus"
    position = "Admin"
    timeIn = "02:12"
    timeOut = "04:24"
    date = "Fri, Nov 3, '23"
    month = "November"
    term = "First"
    session = "2023/2024"

}
val staffThirdTermAttendance = StaffAttendance().apply {
    owner_id = "pk"
    staffId = "AIA/23/2025"
    name = "Toyyib Badmus"
    position = "Admin"
    timeIn = "02:12"
    timeOut = "04:24"
    date = "Fri, Nov 3, '23"
    month = "November"
    term = "First"
    session = "2023/2024"

}


val staffTestList = listOf<StaffAttendance>(
    staffFirstTermAttendance, staffFirstTermAttendance, staffFirstTermAttendance, staffFirstTermAttendance, staffSecondTermAttendance,

)



@Composable
fun TestingUI(){
    val movementVM: MovementVM = viewModel()
    movementVM.staffId.value = "PK/23/2012"
    movementVM.reason.value = "Food"
    movementVM.date.value = "Tue 10 May,'23"
    movementVM.timeIn.value = "10:23"
//    movementVM.timeOut.value = ""


    Column(modifier = Modifier.verticalScroll(state = rememberScrollState()),) {
        BtnNormal(onclick = { movementVM.insert() }, text = "Done")
        Text(text = "${movementVM.staffMovementData.value.size}")


    }

}



@Composable
fun DisplayItemsInGroupsOf30(items: List<String>) {
    var startIndex by remember { mutableIntStateOf(0) }
    val endIndex = minOf(startIndex + 30, items.size)



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(items.subList(startIndex, endIndex)) { item ->
                Text(item)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (startIndex < 30) {
                        startIndex = 0
                    } else{
                        startIndex -= 30
                    }
                },
                enabled = startIndex != 0
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    if (startIndex + 30 < items.size) {
                        startIndex += 30
                    } else if (startIndex < items.size) {
                        startIndex = items.size
                    }
                },
                enabled = startIndex < items.size
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ItemListApp() {
    val items = (1..100).map { "Item $it" }

    DisplayItemsInGroupsOf30(items)
}




