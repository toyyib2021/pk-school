package com.stevdza.san.mongodemo.screenStudent.accountType

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.dataStore.DefaultSettingsKey
import com.stevdza.san.mongodemo.dataStore.FullNameKey
import com.stevdza.san.mongodemo.screenStudent.component.TitleAndSub
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants.ADMIN
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_COLLECTED
import com.stevdza.san.mongodemo.util.Constants.DIRECTOR
import kotlinx.coroutines.launch

@Composable
fun AccountType(
    navToPinScreen:()-> Unit,
){

    val accountTypeVM: AccountTypeVM = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accountTypeKey = AccountTypeKey(context)
    val fullNameKey = FullNameKey(context)
    val defaultSettingsKey = DefaultSettingsKey(context)
    val fullName = fullNameKey.getKey.collectAsState(initial = "")
    val getAccountTypeKey = accountTypeKey.getKey.collectAsState(initial = "")
    val getDefaultSettingsKey = defaultSettingsKey.getKey.collectAsState(initial = "")





    LazyColumn(
        modifier = Modifier
            .background(color = Cream)
            .fillMaxSize()){
        item {
            AccountTypeScreen(
                name = "",
                onFirstClick = {
                        scope.launch { accountTypeKey.saveKey(ADMIN) }
                        navToPinScreen()
                },
                onSecondClick = {
                    scope.launch {
                        accountTypeKey.saveKey(DIRECTOR)
                    }
                    navToPinScreen()
                },
                onThirdClick = {
                    scope.launch {
                        accountTypeKey.saveKey(ATTENDANCE_COLLECTED)
                    }
                    navToPinScreen()
                },
                firstText = stringResource(id = R.string.admin_tex),
                secondText = stringResource(id = R.string.director_text),
                thirdText = stringResource(id = R.string.attendance_collector)
            )
        }
    }
}

@Composable
fun AccountTypeScreen(
    name:String,
    onFirstClick:()->Unit,
    onThirdClick:()->Unit,
    onSecondClick:()->Unit,
    firstText: String,
    secondText: String,
    thirdText: String,
){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Cream)) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
        TitleAndSub(
            title = "Welcome $name",
            subTitle = stringResource(id = R.string.please_choose))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp))
        Text(
            modifier = Modifier
                .padding(start = 20.dp, bottom = 5.dp)
                .fillMaxWidth()
                .clickable { onFirstClick() },
            text = firstText, color = Color.Black,
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), thickness = 3.dp, color = Color.Black)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))

        Text(
            modifier = Modifier
                .padding(start = 20.dp, bottom = 5.dp)
                .fillMaxWidth()
                .clickable { onSecondClick() },
            text = secondText, color = Color.Black,
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), thickness = 3.dp, color = Color.Black)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        Text(
            modifier = Modifier
                .padding(start = 20.dp, bottom = 5.dp)
                .fillMaxWidth()
                .clickable { onThirdClick() },
            text = thirdText, color = Color.Black,
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), thickness = 3.dp, color = Color.Black)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))


    }
}

