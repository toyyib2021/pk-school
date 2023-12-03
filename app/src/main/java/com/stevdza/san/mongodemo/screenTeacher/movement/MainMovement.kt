package com.stevdza.san.mongodemo.screenTeacher.movement

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream

@Composable
fun MainMovement(
    navToStaffMovement:()->Unit,
    navToMovementHistory:()->Unit,
    navToLogMovement:()->Unit,
    onBackClick:()->Unit,
){

    BackHandler() {onBackClick()}

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {

        TopBar(onBackClick = { onBackClick() }, title = "Movement")
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp))

        Text(
            text = "Log Staff Movement",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navToLogMovement() }
                .padding(start = 10.dp)
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp))

        Text(
            text = "Staff Movement",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navToStaffMovement() }
                .padding(start = 10.dp)
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp))

        Text(
            text = "Movement History",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navToMovementHistory() }
                .padding(start = 10.dp)
        )
    }
}


//@Preview
//@Composable
//private fun MainMovementPreview() {
//    MainMovement(
//        navToStaffMovement = { /*TODO*/ },
//        onBackClick = {},
//        navToMovementHistory ={}
//    )
//}