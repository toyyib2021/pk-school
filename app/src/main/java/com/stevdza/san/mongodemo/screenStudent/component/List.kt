package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.model.AssociateParent
import com.stevdza.san.mongodemo.model.Parent
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentSelectable
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_COLLECTED
import com.stevdza.san.mongodemo.util.conversion.isPastSignInTime

@Composable
fun ParentCardEdit(
    parent: Parent,
    bitmap: ImageBitmap,
    onParentClick:()->Unit
){
    var hide by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { hide = !hide },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        if (hide){
            Row(modifier = Modifier.padding(10.dp)) {
                SelectionContainer( modifier = Modifier
                    .weight(8f)
                    .fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(text = "${parent.surname}  ${parent.otherNames}",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = parent._id.toHexString(), fontSize = 12.sp)
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp))
                        Text(text = parent.phone, fontSize = 12.sp)
                        Text(text = parent.address, fontSize = 12.sp)
                        Text(text = parent.gender, fontSize = 12.sp)


                    }
                }
                Icon(
                    modifier = Modifier
                        .weight(2f)
//                        .fillMaxSize()
                        .clickable { onParentClick() },
                    imageVector = Icons.Default.Edit, contentDescription = "")
            }
        }else{
            Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {

                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(4f)
                ){
                    Card(
                        modifier = Modifier
                            .padding(5.dp),
                        shape = CircleShape
                    ) {
                        Image( modifier = Modifier
                            .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${parent.surname}  ${parent.otherNames}",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
                    Text(text = parent.phone, fontSize = 12.sp)
                    Text(text = parent.relationship, fontSize = 12.sp)
                }
            }
        }

    }
}


@Composable
fun AssociatedParentCardEdit(
    associateParent: AssociateParent,
    bitmap: ImageBitmap,
    onParentClick:()->Unit
){
    var hide by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { hide = !hide },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        if (hide){
            Row(modifier = Modifier.padding(10.dp)) {
                Column(
                    modifier = Modifier
                        .weight(8f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${associateParent.surname}  ${associateParent.otherNames}",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
                    Text(text = associateParent.phone, fontSize = 12.sp)
                    Text(text = associateParent.address, fontSize = 12.sp)
                    Text(text = associateParent.gender, fontSize = 12.sp)
                }
                Icon(
                    modifier = Modifier
                        .weight(2f)
//                        .fillMaxSize()
                        .clickable { onParentClick() },
                    imageVector = Icons.Default.Edit, contentDescription = "")
            }
        }else{
            Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {

                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(4f)
                ){
                    Card(
                        modifier = Modifier
                            .padding(5.dp),
                        shape = CircleShape
                    ) {
                        Image( modifier = Modifier
                            .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(7f)
                        .padding(start = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${associateParent.surname}  ${associateParent.otherNames}",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
                    Text(text = associateParent.phone, fontSize = 12.sp)
                    Text(text = associateParent.relationship, fontSize = 12.sp)
                }
            }
        }

    }
}


@Composable
fun ParentCard(
    parent: Parent,
    bitmap: ImageBitmap,
    onParentClick:()->Unit
){

    var hide by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { hide = !hide },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .padding(5.dp)
                .weight(4f)) {
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
//                    Image(painter = painterResource(id = R.drawable.empty_image), contentDescription = "blank")
                    Image( modifier = Modifier
                        .size(70.dp),
                        bitmap = bitmap,
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(6f)
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${parent.surname}  ${parent.otherNames}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Text(text = parent.phone, fontSize = 12.sp)
                Text(text = parent.relationship, fontSize = 12.sp)


            }
        }
        if (hide){
            Text(text = "Click here to Continue", fontWeight = FontWeight.Bold,
                fontSize = 14.sp, modifier = Modifier
                    .clickable { onParentClick() }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AssociateParentCard(
    associateParent: AssociateParent,
    bitmap: ImageBitmap,
    onParentClick:()->Unit,
){

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable {
//                checkParent.value = associateParent
                onParentClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {

        Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(4f)
                    .size(100.dp),
                shape = CircleShape
            ) {
//                Image(painter = painterResource(id = R.drawable.empty_image), contentDescription = "blank")
                Image( bitmap = bitmap, contentDescription = "")
            }
            Column(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${associateParent.surname}  ${associateParent.otherNames}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Text(text = associateParent.phone, fontSize = 12.sp)
                Text(text = associateParent.relationship, fontSize = 12.sp)

            }
            Icon(
                modifier = Modifier
                    .weight(1f),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "CheckCircle")

        }
    }
}




@Composable
fun StudentCard(
    student: StudentData,
    bitmap: ImageBitmap,
    onStudentClick:()->Unit
){

    var hide by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { hide = !hide },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(4f)
            ){
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
                    Image( modifier = Modifier
                        .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                }
            }

            Column(
                modifier = Modifier
                    .weight(6f)
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${student.surname}  ${student.otherNames}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Text(text = student.cless, fontSize = 12.sp)
                Text(text = student.studentApplicationID, fontSize = 12.sp)
            }
        }
        if (hide){
            Text(text = "Click here to Continue", fontWeight = FontWeight.Bold,
                fontSize = 14.sp, modifier = Modifier
                    .clickable { onStudentClick() }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

    }
}



// Staff Card

@Composable
fun StaffCard(
    staff: TeacherProfile,
    bitmap: ImageBitmap,
    onStaffClick:()->Unit
){

    var hide by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { hide = !hide },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column() {
            Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(4f)
                ){
                    Card(
                        modifier = Modifier
                            .padding(5.dp),
                        shape = CircleShape
                    ) {
                        Image( modifier = Modifier
                            .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(6f)
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${staff.surname}  ${staff.otherNames}",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                    Text(text = staff.staffId, fontSize = 12.sp)
                    Text(text = staff.position, fontSize = 12.sp)
                }
            }
            if (hide){
                Text(text = "Click here to Continue", fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, modifier = Modifier
                        .clickable { onStaffClick() }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

        }

    }
}


@Composable
fun StaffAbsentCard(
    staff: TeacherProfile,
    bitmap: ImageBitmap,
    reasonForAbsent: String,
    absentDate: String,
    delete:()->Unit,
    edit:()->Unit,
    accountType: String
){

    Card(
        modifier = Modifier
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(4f)
            ){
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
                    Image( modifier = Modifier
                        .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                }
            }

            Column(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${staff.surname}  ${staff.otherNames}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Text(text = staff.staffId, fontSize = 12.sp)
                Text(text = staff.position, fontSize = 12.sp)
            }

            if (accountType != ATTENDANCE_COLLECTED){
                Column() {
                    Icon(modifier = Modifier.clickable { delete() },
                        imageVector = Icons.Default.Delete, contentDescription = "Delete")

                    Spacer(modifier = Modifier.padding(5.dp))
                    Icon(modifier = Modifier.clickable { edit() },
                        imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }


        }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row( modifier = Modifier
                    .fillMaxWidth(),
                ) {
                    Text(modifier = Modifier
                        .weight(6f),
                        text = "Reason", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(modifier = Modifier
                        .weight(4f), text = absentDate, fontSize = 14.sp, )
                }

                Text(text = reasonForAbsent, fontSize = 12.sp)
            }


    }
}


@Composable
fun StaffAbsentCardNoEdit(
    staff: TeacherProfile,
    bitmap: ImageBitmap,
    reasonForAbsent: String,
    absentDate: String,

){

    Card(
        modifier = Modifier
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(4f)
            ){
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
                    Image( modifier = Modifier
                        .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                }
            }

            Column(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${staff.surname}  ${staff.otherNames}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Text(text = staff.staffId, fontSize = 12.sp)
                Text(text = staff.position, fontSize = 12.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row( modifier = Modifier
                .fillMaxWidth(),
            ) {
                Text(modifier = Modifier
                    .weight(6f),
                    text = "Reason", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(modifier = Modifier
                    .weight(4f), text = absentDate, fontSize = 14.sp, )
            }

            Text(text = reasonForAbsent, fontSize = 12.sp)
        }


    }
}




// Attendance Card
@Composable
fun AttendanceCard(
    studentName: String,
    studentClass: String,
    broughtInBy: String,
    timeIn: String,
    broughtOutBy: String,
    timeOut: String,
    hourIn: Int,
    minIn: Int,
    hourOut: Int,
    minOut: Int
){
    
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
            Text(
                text = studentName, 
                fontSize = 14.sp, 
                 color = Color.Blue)
            Text(
                text = studentClass,
                fontSize = 12.sp, fontWeight = FontWeight.Bold,)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
            val isPastSignInTime = isPastSignInTime(timeIn, hourIn, minIn)

            Text(modifier = Modifier.weight(2f),
                text = "In", fontSize = 12.sp,
                color = if (isPastSignInTime){
                    Color.Red
                }else{
                    Color.Black
                }
            )

            Text(modifier = Modifier.weight(6f),
                text = broughtInBy, fontSize = 12.sp)

            Text(modifier = Modifier.weight(2f),
                text = timeIn, fontSize = 12.sp)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
            val isPastSignOutTime = isPastSignInTime(timeOut, hourOut, minOut)
            Text(modifier = Modifier.weight(2f),
                text = "Out", fontSize = 12.sp,
                color = if (isPastSignOutTime){
                    Color.Black
                }else{
                    Color.Red

                }
            )

            Text(modifier = Modifier.weight(6f),
                text = broughtOutBy, fontSize = 12.sp)

            Text(modifier = Modifier.weight(2f),
                text = timeOut, fontSize = 12.sp)
        }
    }
}


@Composable
fun StudentAttendanceCard(
    selectable: StudentSelectable,
    bitmap: ImageBitmap,
    onParentClick:()->Unit,
    check: MutableState<Boolean>
){

//    var hide by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { onParentClick() },
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(10.dp)
                .clickable { onParentClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(4f)
            ){
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
//                    Image(painter = painterResource(id = R.drawable.empty_image), contentDescription = "blank")
                    Image( modifier = Modifier
                        .size(70.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                }
            }
            Column(
                modifier = Modifier
                    .weight(6f)
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${selectable.student.surname}  ${selectable.student.otherNames}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Text(text = selectable.student.cless, fontSize = 12.sp)
                Text(text = selectable.student.studentApplicationID, fontSize = 12.sp)

            }
            if (selectable.selected.value){
                Icon(
                    modifier = Modifier
                        .weight(1f),
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "CheckCircle")
            }
            if (check.value){
                selectable.selected.value = false
                Icon(
                    modifier = Modifier
                        .weight(1f),
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "CheckCircle")
            }
        }

    }
}



//@Preview
@Composable
private fun LoseDialogPrev() {
    val staff = TeacherProfile().apply {
        surname = "Toyyib"
        otherNames = "Babatunde"
        position = "Head Teacher"
        staffId = "PK/2300/252"
    }
//    StaffAbsentCard(staff = staff, bitmap = null,
//        reasonForAbsent = "Sick and can not come to school",
//        absentDate = "Thu, Nov 9, '23"
//    )
}