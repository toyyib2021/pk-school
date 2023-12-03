package com.stevdza.san.mongodemo.screenStudent.component

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.isPastSignInTime


@Composable
fun StaffAttendanceList(
    name:String,
    position: String,
    date:String,
    timeIn: String,
    timeOut: String,
    hourIn: Int,
    minIn: Int,
    hourOut: Int,
    minOut: Int
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream),

    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier
                .weight(6f)
                .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = position, fontSize = 12.sp)
            }
            Column(modifier = Modifier.weight(4f)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    val isPastSignInTime = isPastSignInTime(timeIn, hourIn, minIn)

                    Text(text = timeIn, fontSize = 10.sp, modifier = Modifier.weight(5f),
                    color = if (isPastSignInTime){
                        Color.Red
                    }else{
                        Color.Black
                    }
                    )
                    val isPastSignOutTime = isPastSignInTime(timeOut, hourOut, minOut)
                    Text(text = timeOut, fontSize = 10.sp, modifier = Modifier.weight(5f),
                        color = if (isPastSignOutTime){
                            Color.Black
                        }else{
                            Color.Red

                        }
                    )
                }
                Text(text = date, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp), thickness = 3.dp)

    }
}

@Composable
fun InAndOutTableHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(6f))
        Text(
            text = "In", fontSize = 10.sp, modifier = Modifier.weight(2f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Out", fontSize = 10.sp, modifier = Modifier.weight(2f),
            fontWeight = FontWeight.Bold
        )
    }
}


////@Preview
//@Composable
//fun StaffAttendanceListPrev() {
//    StaffAttendanceList(
//        name = "Toyyib Babatunde",
//        position = "Head Teacher",
//        date = "20/10/2023",
//        timeIn = "20:10pm",
//        timeOut ="14:21pm"
//    )
//}
