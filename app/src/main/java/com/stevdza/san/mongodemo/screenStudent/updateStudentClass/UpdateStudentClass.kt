package com.stevdza.san.mongodemo.screenStudent.updateStudentClass

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StudentSelectable
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.StudentAttendanceCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.onboardedStudent.OnBoardedStudentVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun UpdateStudentClassScreen(navToHome:()->Unit){
    val onBoardedStudentVM: OnBoardedStudentVM = viewModel()
    val context = LocalContext.current


    val data = onBoardedStudentVM.studentData.value
    val check = remember { mutableStateOf(false) }
    val stu = onBoardedStudentVM.stu

    val clesses = onBoardedStudentVM.studentClass.value.map { it.className }
    val armses = onBoardedStudentVM.studentArm.value.map { it.armsName }
    var selectedClass by remember { mutableStateOf("All") }
    var selectePromotedClass by remember { mutableStateOf("All") }

    val studentByClassAndArms= data.filter {
        it.cless == selectedClass && it.arms == selectePromotedClass}

    LaunchedEffect(key1 = data ){
        data.forEach {
            val selectable = StudentSelectable(it, mutableStateOf(false))
            stu.add(selectable)
        }
    }



    data.forEach {
        Log.e(TAG, "data: ${it.otherNames}", )
    }
    val studentByClass = stu.filter {
        it.student.cless == selectedClass && it.student.studentStatus?.status == StudentStatusType.ACTIVE.status}
    val onlyTrue = stu.filter { it.selected.value }
    onlyTrue.forEach {
        Log.e(TAG, "studentByClass: ${it.student.otherNames}", )
        Log.e(TAG, "studentByClass: ${it.selected}", )
    }

    LaunchedEffect(key1 = selectedClass ){
        stu.forEach {
            it.selectedFalse()
        }
    }



    OnboardedStudentScreen(
        cless = selectedClass,
        arms = selectePromotedClass,
        student = studentByClass,
        onBackClick = { navToHome() },
        clesses = clesses,
        armses = clesses,
        getClass = {
            selectedClass = it
                   },
        getArms = {
            selectePromotedClass = it
            onBoardedStudentVM.cless.value = it
                  },
        onStudentClick = {
            it.toggle(
                addSelectedStudent = { student ->
//                    selectedStudent.add(student)
//                    Log.e(ContentValues.TAG, "getSelectedStudent: ${selectedStudent.size}", )
                },
                removeSelectedStudent = {student2 ->

//                    selectedStudent.remove(student2)
//                    Log.e(ContentValues.TAG, "removeSelectedStudent: ${selectedStudent}", )
                }
            )
        },
        check = check,
        updateClass = {
            onBoardedStudentVM.upDateClass(
                onError = {Toast.makeText(context, "Fail", Toast.LENGTH_LONG).show()},
                onSuccess = {
                    navToHome()
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()}
            )
        }
    )


}

@Composable
fun OnboardedStudentScreen(
    cless:String,
    arms:String,
    student: List<StudentSelectable>,
    onBackClick:()->Unit,
    clesses: List<String>,
    armses: List<String>,
    getClass:(String)->Unit,
    getArms:(String)->Unit,
    onStudentClick:(StudentSelectable)->Unit,
    check:MutableState<Boolean>,
    updateClass:()->Unit

){

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        TopBar(onBackClick = { onBackClick() }, title = "Students")
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
        ) {
            var hideClass by remember { mutableStateOf(false) }
            var hideArms by remember { mutableStateOf(false) }
            Column(modifier = Modifier
                .weight(5f)
                .padding(vertical = 5.dp)
                .clickable { hideClass = !hideClass }
                .background(Color.White)
            ) {

                if (hideClass){
                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Class", fontSize = 12.sp)
                    Spacer(modifier = Modifier.padding(5.dp))
                    clesses.forEach {
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                getClass(it)
                                hideClass = false
                            }
                            .padding(horizontal = 10.dp),
                            text = it, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.padding(5.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                    }
                }else{

                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Class", fontSize = 12.sp)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable { hideClass = true }) {
                        Text(text = cless, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "ArrowDropDown", tint = Color.Black)
                    }
                }

            }
            Column(modifier = Modifier
                .weight(5f)
                .padding(vertical = 5.dp)
                .clickable { hideArms = !hideArms }
                .background(Color.Black)) {
                if (hideArms){
                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Sent To", fontSize = 12.sp)
                    Spacer(modifier = Modifier.padding(5.dp))
                    armses.forEach {
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                getArms(it)
                                hideArms = false
                            }
                            .padding(horizontal = 10.dp),
                            text = it, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.padding(5.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                    }
                }else{

                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Promoted Class", fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable { hideArms = true }) {
                        Text(text = arms, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "ArrowDropDown", tint = Color.White)
                    }
                }

            }
        }
        LazyColumn{
            items(student){
                val imageBitmap = it.student.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                if (imageBitmap != null) {
                        StudentAttendanceCard(
                            selectable = it,
                            bitmap =imageBitmap.asImageBitmap() ,
                            onParentClick = {onStudentClick(it)},
                            check = check
                        )


//                }
            }
        }
        BtnNormal(onclick = { updateClass() }, text = "Update Class")
    }
}