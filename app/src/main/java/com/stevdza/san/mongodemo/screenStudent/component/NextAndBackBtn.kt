package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stevdza.san.mongodemo.ui.theme.Cream

@Composable
fun NextAndBackBtn(
    onBackClick:()->Unit,
    onNextClick:()->Unit,
    startIndex: Int,
    listSize: Int
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
            Card(
                modifier = Modifier
                    .clickable { onBackClick() },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults
                    .cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Cream
                )
            ) {
                if (startIndex != 0){
                    Icon(modifier = Modifier.padding(vertical = 7.dp, horizontal = 20.dp),
                        imageVector = Icons.Default.NavigateBefore, contentDescription = "")
                }
            }


        Card(
            modifier = Modifier
                .clickable { onNextClick() },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults
                .cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Cream
            )
        ) {
            if (startIndex < listSize){
                Icon(modifier = Modifier.padding(vertical = 7.dp, horizontal = 20.dp),
                    imageVector = Icons.Default.NavigateNext, contentDescription = "")
            }

        }

    }
}