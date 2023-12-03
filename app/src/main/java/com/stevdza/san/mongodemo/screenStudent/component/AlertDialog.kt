package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AlertDialogDemo(
    openDialog: Boolean,
    onDismissRequest:()->Unit,
    confirmButton:()->Unit,
    dismissButton:()->Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest() },
            title = {
                Text(text = "Alert Dialog", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Are you sure you want to close this dialog?", fontSize = 16.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmButton()
                    }) {
                    Text("Yes",style = TextStyle(color = Color.White))
                } },
            dismissButton = {
                TextButton(
                    onClick = {
                        dismissButton()
                    }) {
                    Text("No",style = TextStyle(color = Color.White))
                } },
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )
    }
}
