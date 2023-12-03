package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.ui.theme.Blue

@Composable
fun TitleAndSub(
    title:String,
    subTitle:String,
    modifier: Modifier = Modifier
){
    Column(modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(text = title, fontSize = 24.sp, color = Color.Black,
            fontWeight = FontWeight.Bold)
        Text(text = subTitle, fontSize = 12.sp, color = Blue)
    }
}
