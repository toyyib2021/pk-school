package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BtnNormal(
    onclick:()->Unit,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black

){
    Column(modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .padding()
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = { onclick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp)
        ) {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
                text = text, textAlign = TextAlign.Center)
        }
    }

}
