package com.stevdza.san.mongodemo.screenStudent.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.Term
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import kotlinx.coroutines.delay

@Composable
fun Session(
    onBackClick:()->Unit
){
    val sessionVM: SessionVM = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    var termEndDate by remember { mutableStateOf("") }
    var termStartDate by remember { mutableStateOf("") }
    val sessionData = sessionVM.terms.value
    val sessions = sessionVM.sessions.value
    val w= sessions.firstOrNull()
    val currentYear = w?.year ?: ""
    val objectIdYear = w?._id?.toHexString() ?: ""


    Log.e(TAG, "Session: ${sessions.size}", )
    sessionData.forEach {
        Log.e(TAG, "Session: ${it.termName}", )
        Log.e(TAG, "Session: ${it._id}", )
    }

    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = snackbarVisible ){
        if (snackbarVisible){
            delay(3000)
            snackbarVisible = false
        }
    }




    SessionScreen(
        navToHome = { onBackClick() },
        year = currentYear,
        terms = sessionData,
        updateTermEndTextFeild = { sessionVM.termEnd.value = it },
        updateTermStratTextFeild = { sessionVM.termStart.value = it},
        onTermEndEditChange = {
            sessionVM.termEnd.value = it
            termEndDate=it},
        onTermStartEditChange = {
            sessionVM.termStart.value = it
            termStartDate=it
                                },
        termId = { sessionVM.objectId.value = it},
        updateTermDuration = {
             sessionVM.updateTermDate(
                 onSuccess = {
                     snackbarMessage="Update Successful"
                     snackbarVisible=true },
                 onError = {
                     snackbarMessage="Try again Something went wrong"
                     snackbarVisible=true },
                 emptyFeild ={
                     snackbarMessage="empty field"
                     snackbarVisible=true
                 }
             )
        },
        termEndEdit = sessionVM.termEnd.value,
        termStartEdit = sessionVM.termStart.value,
        snackbarMessage = snackbarMessage,
        snackbarVisible = snackbarVisible,
        onDismiss = {snackbarVisible=false},
        yearTextFeildToYear = { sessionVM.sessionYear.value = currentYear },
        yearTextFeild = sessionVM.sessionYear.value,
        onYearTextFeildChange = {sessionVM.sessionYear.value=it},
        saveSessionYear = {
            sessionVM.objectIdSessionYear.value = objectIdYear
            sessionVM.updateSessionYear(
                onSuccess = {
                    snackbarMessage="Update Successful"
                    snackbarVisible=true
                },
                onError = {
                    snackbarMessage="Try again Something went wrong"
                    snackbarVisible=true
                },
                emptyFeild ={
                    snackbarMessage="empty field"
                    snackbarVisible=true
                })
        },
    )
}


@Composable
fun SessionScreen(
    navToHome:()->Unit,
    year: String,
    terms: List<Term>,
    updateTermEndTextFeild:(String)->Unit,
    updateTermStratTextFeild:(String)->Unit,
    onTermEndEditChange:(String)->Unit,
    onTermStartEditChange:(String)->Unit,
    termId:(String)->Unit,
    updateTermDuration:()->Unit,
    termEndEdit: String,
    termStartEdit: String,
    snackbarVisible: Boolean,
    snackbarMessage: String,
    onDismiss:()->Unit,
    yearTextFeildToYear:()->Unit,
    yearTextFeild: String,
    onYearTextFeildChange:(String)->Unit,
    saveSessionYear:()->Unit,

){

    if(snackbarVisible){
        Snackbar(
            modifier=Modifier.padding(horizontal = 15.dp),
            backgroundColor = Color.White,
            contentColor = Color.Black,
            action = {
                Text(modifier = Modifier.clickable {
                    onDismiss()
                }, text = "Dismiss", color = Color.Black, fontSize = 10.sp)
            }) {
            Text(text = snackbarMessage, color = Color.Black, fontSize = 12.sp )
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Cream)
        ) {

        LazyColumn(modifier = Modifier.padding(10.dp)){
            item{
                TopBar(onBackClick = { navToHome() }, title = "Session")
            }
            item{
               SessionYear(
                   yearTextFeildToYear = { yearTextFeildToYear() },
                   yearTextFeild = yearTextFeild,
                   onYearTextFeildChange = {onYearTextFeildChange(it)},
                   saveSessionYear = { saveSessionYear() },
                   year = year
               )
            }
            items(terms){

                OneTermUi(
                    term = it,
                    termStartEdit = termStartEdit,
                    termEndEdit = termEndEdit,
                    updateTermDuration = { updateTermDuration() },
                    onTermStartEditChange = { onTermStartEditChange(it) },
                    onTermEndEditChange = {onTermEndEditChange(it)},
                    updateTermSTartAndEndTextFeild = {
                        updateTermEndTextFeild(it.termEnd)
                        termId(it._id.toHexString())
                        updateTermStratTextFeild(it.termStart)}
                )
            }

        }
    }
}



@Composable
private fun OneTermUi(
    term: Term,
    termStartEdit: String,
    termEndEdit: String,
    updateTermDuration:()->Unit,
    onTermStartEditChange:(String)->Unit,
    onTermEndEditChange:(String)->Unit,
    updateTermSTartAndEndTextFeild:()->Unit
) {
    var hide by remember { mutableStateOf(false) }

    Card(modifier= Modifier
        .padding(10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 3.dp,
        backgroundColor = Cream
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 10.dp)) {
            Row() {
                Text(modifier = Modifier.weight(7f),
                    text = term.termName, fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
                Icon(modifier = Modifier
                    .weight(3f)
                    .clickable {
                        updateTermSTartAndEndTextFeild()
                        hide = !hide
                    },
                    imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp))
            if (hide){
                Column {
                    TextFieldForm(
                        value = termStartEdit,
                        label = "Start Date (YYYY-MM-DD)",
                        onValueChange = {onTermStartEditChange(it)}
                    )

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))

                    TextFieldForm(
                        value = termEndEdit,
                        label = "End Date (YYYY-MM-DD)",
                        onValueChange ={onTermEndEditChange(it)}
                    )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    if (termStartEdit.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                        termEndEdit.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))){
                        BtnNormal(onclick = {
                            hide = false
                            updateTermDuration() },
                            text = "Save"
                        )
                    }

                }
            }else{
                Column() {
                    Row(modifier = Modifier
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(3f),
                            text = "Start Date")
                        Card(
                            modifier = Modifier.weight(7f),
                            elevation = 10.dp,
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(modifier = Modifier.padding(10.dp), text = term.termStart, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(3f),
                            text = "End Date")
                        Card(
                            modifier = Modifier.weight(7f),
                            elevation = 10.dp,
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(modifier = Modifier.padding(10.dp),
                                text = term.termEnd, fontWeight = FontWeight.Bold)
                        }

                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
                }
            }

        }
    }
}


@Composable
fun SessionYear(
    yearTextFeildToYear:()->Unit,
    yearTextFeild: String,
    onYearTextFeildChange:(String)->Unit,
    saveSessionYear:()->Unit,
    year: String
){
    var hide by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            yearTextFeildToYear()
            hide = true
        }
        .padding(vertical = 10.dp, horizontal = 5.dp)) {
        if (hide){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(7f)) {
                    TextFieldForm(
                        value = yearTextFeild,
                        label = "Session",
                        onValueChange = {onYearTextFeildChange(it)}
                    )
                }
                Icon(
                    modifier = Modifier
                        .weight(3f)
                        .clickable {
                            hide = false
                            saveSessionYear()
                        },
                    imageVector = Icons.Default.Done,
                    contentDescription ="Done" )

            }

        }else{
            Text(text = "Current Session", fontSize = 16.sp,
                fontWeight = FontWeight.Bold)
            Text(text = year, fontSize = 12.sp)
        }

    }
}





