package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    onFirstBtnClick:()->Unit,
    onSecondBtnClick:()->Unit,
    firstText:String,
    secondText:String,
){
    Row(modifier
        .padding(10.dp)
    ) {
        Column(modifier = Modifier
            .weight(5f)
            .clickable { onFirstBtnClick() }
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = firstText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp)
        }

        Column(modifier = Modifier
            .weight(5f)
            .background(Color.White)
            .clickable { onSecondBtnClick() }
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = secondText,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp)
        }
    }
}