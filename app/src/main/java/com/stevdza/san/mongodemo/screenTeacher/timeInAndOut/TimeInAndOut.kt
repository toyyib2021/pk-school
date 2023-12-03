package com.stevdza.san.mongodemo.screenTeacher.timeInAndOut

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.isPastSignInTime
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun TimeInOut(
    onBackClick: ()->Unit
){

    val context = LocalContext.current
    val timeInAndOutVM: SetTimeInAndOutVM = viewModel()
    val data= timeInAndOutVM.data.value

    Log.e(TAG, "TimeInOut: ${data.size}", )

    val timeInOut = data.lastOrNull()

    var pickedTime by remember { mutableStateOf(LocalTime.NOON) }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("HH:mm")
                .format(pickedTime)
        }
    }

    BackHandler() { onBackClick() }

    val timeInDialogState = rememberMaterialDialogState()
    val timeOutDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(onBackClick = { onBackClick() }, title = "Time")
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        Text(text = "Set time In", fontSize = 16.sp)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier
                .weight(7f)
                .padding(10.dp), elevation = 5.dp) {
                Text(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                    text = timeInOut?.timeIn ?: "00:00", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {  timeInDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue, contentColor = Color.White)
            ) {
                Text(text = "Set time")
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp))
        Text(text = "Set time Out", fontSize = 16.sp)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier
                .weight(7f)
                .padding(10.dp), elevation = 5.dp) {
                Text(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                    text = timeInOut?.timeOut ?: "00:00", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {  timeOutDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue, contentColor = Color.White)
            ) {
                Text(text = "Set time")
            }
        }

    }

    MaterialDialog(
        dialogState = timeInDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                val inputTime = "15:30"
                val hour = 15
                val min = 30
                val isPast4PM = isPastSignInTime(inputTime, hour, min)

                if (isPast4PM) {
                    Log.e(TAG, "TimeInOut: $inputTime is past 4:00 PM.", )
//                    println("$inputTime is past 4:00 PM.")
                } else {
//                    println("$inputTime is not past 4:00 PM.")
                    Log.e(TAG, "TimeInOut: $inputTime is not past 4:00 PM.", )
                }

                timeInAndOutVM.timeIn.value = formattedTime
                timeInAndOutVM.objectId.value = timeInOut?._id?.toHexString() ?: ""
                timeInAndOutVM.updateTimeIn(
                    onError = {},
                    onSuccess = {
                        Toast.makeText(context, "Clicked ok", Toast.LENGTH_LONG).show()
                    },
                    emptyFeild = {Toast.makeText(context, "empty", Toast.LENGTH_LONG).show()}
                )

            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.NOON,
            is24HourClock = true,
            title = "Set Time In",
//            timeRange = LocalTime.MIDNIGHT..LocalTime.NOON
        ) {
            pickedTime = it
        }
    }

    MaterialDialog(
        dialogState = timeOutDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                timeInAndOutVM.objectId.value = timeInOut?._id?.toHexString() ?: ""
                timeInAndOutVM.timeOut.value = formattedTime
                timeInAndOutVM.updateTimeOut(
                    onError = {},
                    onSuccess = {
                        Toast.makeText(context, "Clicked ok", Toast.LENGTH_LONG).show()
                    },
                    emptyFeild = {Toast.makeText(context, "empty", Toast.LENGTH_LONG).show()}
                )

            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.NOON,
            is24HourClock = true,
            title = "Set Time Out",
//            timeRange = LocalTime.MIDNIGHT..LocalTime.NOON
        ) {
            pickedTime = it
        }
    }
}






