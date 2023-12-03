package com.stevdza.san.mongodemo.screenStudent.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_COLLECTED
import kotlinx.coroutines.delay

@Composable
fun ClassNames(
    navToHome:()->Unit,
){
    val classNamesVM: ClassNamesVM= viewModel()
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var editState by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val accountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")


    val classNames =  classNamesVM.classNames.value.sortedByDescending { it._id }
    classNames.forEach {
        Log.e(TAG, "ClassNames: ${it.className}", )
    }

    LaunchedEffect(key1 = snackbarVisible ){
        if (snackbarVisible){
            delay(5000)
            snackbarVisible = false
        }
    }

    if(snackbarVisible){
        Snackbar(
            modifier=Modifier.padding(horizontal = 15.dp),
            backgroundColor = Color.White,
            contentColor = Color.Black,
            action = {
                Text(modifier = Modifier.clickable {
                    snackbarVisible=false
                }, text = "Dismiss", color = Color.Black, fontSize = 10.sp)
            }) {
            Text(text = snackbarMessage, color = Color.Black, fontSize = 12.sp )
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {

        if (showDialog) {
            AlertDialog(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.Blue,
                contentColor = Color.White,
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = "Delete ${classNamesVM.names.value}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Text(text = "Are you sure you want to delete ${classNamesVM.names.value}")
                },
                buttons = {
                    Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.Start) {
                        Button(
                            onClick = {
                                classNamesVM.names.value = ""
                                classNamesVM.objectId.value = ""
                                showDialog = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Black)
                        ) {
                            Text(text = "No", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                classNamesVM.deleteClassName(
                                    onSuccess = {
                                        classNamesVM.names.value = ""
                                        classNamesVM.objectId.value = ""
                                        showDialog = false
                                    },
                                    onError = {
                                        snackbarMessage = ""
                                        snackbarVisible = true
                                        showDialog = false
                                    },
                                    emptyFeild = {
                                        snackbarMessage = "Try Again"
                                        snackbarVisible = true
                                        showDialog = false
                                    }
                                )
                                 },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Black)
                        ) {
                            Text(text = "Yes")
                        }
                    }
                }
            )
        }



        ClassNamesScreen(
            navToHome = { navToHome() },
            value = classNamesVM.names.value,
            label = "Enter CLass Name",
            onValueChange = { classNamesVM.names.value = it },
            onAddClick = {
                classNamesVM.insertClassName(
                    onError = {
                        snackbarMessage = "Something went wrong"
                        snackbarVisible = true },
                    onSuccess = {
                        snackbarMessage = "Class Add Successfully"
                        snackbarVisible = true
                        classNamesVM.names.value = ""
                    },
                    emptyFeild = {
                        snackbarMessage = "Some fields are empty"
                        snackbarVisible = true
                    }
                )
            },
            classNames = classNames,
            onEditClick = {
                if (accountType.value != ATTENDANCE_COLLECTED){
                    classNamesVM.names.value = it.className
                    classNamesVM.objectId.value = it._id.toHexString()
                    editState = true
                }else{
                    snackbarMessage = "Contact Admin To effect the change"
                    snackbarVisible = true
                }
                },
            onDeleteClick = {
                showDialog=true
                classNamesVM.names.value = it.className
                classNamesVM.objectId.value = it._id.toHexString()
            },
            editState=editState,
            onUpdateClick={
                classNamesVM.updateClassNames(
                    onSuccess = {
                        classNamesVM.names.value = ""
                        classNamesVM.objectId.value = ""
                        editState = false
                    },
                    onError = {
                        snackbarMessage="Try Again"
                        snackbarVisible=true
                    },
                    emptyFeild = {
                        snackbarMessage="No data to update"
                        snackbarVisible=true
                    }
                )
            },
            onCancelClick={
                classNamesVM.names.value = ""
                classNamesVM.objectId.value = ""
                editState = false
            },
            accountType = accountType.value
        )
    }
}



@Composable
fun ClassNamesScreen(
    navToHome:()->Unit,
    value: String,
    label:String,
    onValueChange:(String)->Unit,
    onAddClick:()->Unit,
    classNames: List<ClassName>,
    onEditClick:(ClassName)->Unit,
    onDeleteClick:(ClassName)->Unit,
    editState:Boolean,
    onUpdateClick:()->Unit,
    onCancelClick:()->Unit,
    accountType: String
){
    Column(modifier = Modifier
//        .fillMaxSize()
        .background(Cream)) {
        TopBar(onBackClick = { navToHome() }, title = "Class")
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
//        Text(text = "Add", fontSize = 14.sp, fontWeight = FontWeight.Bold)

        if (accountType != ATTENDANCE_COLLECTED){
            if (editState){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(8f)) {
                        TextFieldForm(value = value, label = label,
                            onValueChange = {onValueChange(it)})
                    }
                    Column(modifier = Modifier.weight(2f)) {

                        Text(modifier = Modifier
                            .clickable { onUpdateClick() },
                            text = "Update", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(modifier = Modifier
                            .clickable { onCancelClick() }
                            .padding(top = 10.dp),
                            text = "Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            else{
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(8f)) {
                        TextFieldForm(value = value, label = label,
                            onValueChange = {onValueChange(it)})
                    }
                    Text(modifier = Modifier
                        .weight(2f)
                        .clickable { onAddClick() },
                        text = "Add", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }



        LazyColumn{
            items(classNames){
                OneClass(
                    classNames = it,
                    onEditClick = {onEditClick(it)},
                    onDeleteClick = {onDeleteClick(it)},
                    accountType = accountType
                )
            }
        }
    }
}

@Composable
fun OneClass(
    classNames: ClassName,
    onEditClick:()->Unit,
    onDeleteClick:()->Unit,
    accountType: String
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(modifier = Modifier.weight(6f),
                        text = classNames.className, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (accountType != ATTENDANCE_COLLECTED){
                    Icon(modifier = Modifier
                        .clickable { onEditClick() }
                        .weight(2f),
                        imageVector = Icons.Default.Edit, contentDescription = "Edit")
//                    Icon(modifier = Modifier
//                        .clickable { onDeleteClick() }
//                        .weight(2f),
//                        imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }

            }

        Divider(thickness = 2.dp, color = Color.Black)
    }

}