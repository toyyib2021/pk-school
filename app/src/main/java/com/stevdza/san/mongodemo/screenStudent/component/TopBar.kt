package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    onBackClick:()->Unit,
    title:String,
    color: Color = Color.Transparent,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Black,
){
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .background(color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(modifier = Modifier
            .weight(1f).clickable { onBackClick() },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "ArrowBack", tint = iconColor)
        Text(modifier = Modifier
            .weight(9f).fillMaxWidth(),
                text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Composable
fun TopBarWithDownload(
    onBackClick:()->Unit,
    title:String,
    color: Color = Color.Transparent,
    onDownloadClick:()->Unit
){
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .background(color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(modifier = Modifier
            .weight(1f).clickable { onBackClick() },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "ArrowBack")
        Text(modifier = Modifier
            .weight(7f).fillMaxWidth(),
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Icon(modifier = Modifier
            .weight(2f).clickable { onDownloadClick() },
            imageVector = Icons.Default.Download,
            contentDescription = "ArrowBack")
    }
}


@Composable
fun TopBarWithDate(
    onBackClick:()->Unit,
    title:String,
    date:String,
    ondateClick:()->Unit
){
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(modifier = Modifier
            .weight(1f).clickable { onBackClick() },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "ArrowBack")
        Text(modifier = Modifier
            .weight(6f).fillMaxWidth(),
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(modifier = Modifier
            .weight(3f).fillMaxWidth().clickable { ondateClick() },
            text = date,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}