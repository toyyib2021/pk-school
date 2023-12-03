package com.stevdza.san.mongodemo.screenStudent.pinLogin

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.MainActivity
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.dataStore.LicenseKey
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.LoadingDialog
import com.stevdza.san.mongodemo.screenStudent.component.LoadingVM
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldPassword
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldSigningInAndSignOutText
import com.stevdza.san.mongodemo.screenStudent.component.TitleAndSub
import com.stevdza.san.mongodemo.screenStudent.signUp.SignUpInitVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.launch

@Composable
fun Passcode(navToSelectFeatureScreen:()->Unit, navToLoginScreen:()->Unit){

    val pinVM: PinVM= viewModel()
    val signInitVM: SignUpInitVM = viewModel()
    val loadingAnimationVM: LoadingVM = viewModel()
    val context = LocalContext.current
    val data = pinVM.data.value
    val scope = rememberCoroutineScope()

    val passCodes = pinVM.data.value.firstOrNull()
    pinVM.adminDB.value = passCodes?.admin ?: ""
    pinVM.schoolOwnerDB.value = passCodes?.schoolOwner ?: ""
    pinVM.attendanceCollectorDB.value = passCodes?.attendanceCollected ?: ""

    Log.e(TAG, "Passcode: ${passCodes?.admin ?: ""}", )

//    LaunchedEffect(key1 =  true){
//        pinVM.insertAccessDB()
//    }
//
//    data.forEach {
//        Log.e(TAG, "admin: ${it.admin}", )
//        Log.e(TAG, "schoolOwner: ${it.schoolOwner}", )
//        Log.e(TAG, "attendanceCollected: ${it.attendanceCollected}", )
//        Log.e(TAG, "_id: ${it._id}", )
//    }

    val app = App.create(Constants.APP_ID)
    val user = app.currentUser
    var backPressedTime: Long = 0


    val accountTypeKey = AccountTypeKey(context)
    val licenseKey = LicenseKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")
    val license = licenseKey.getKey.collectAsState(initial = "")
    pinVM.account.value = accountType.value


    BackHandler() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            val activity: MainActivity = MainActivity()
            // on below line we are finishing activity.
            activity.finish()
            System.exit(0)
        } else {
            Log.i(TAG, "dashboardNavGraph: press back button again ")
        }
        backPressedTime = System.currentTimeMillis()

    }

    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }


        if (data.isNotEmpty()){
            loadingAnimationVM.open.value = false
        }else{
            loadingAnimationVM.open.value = true
            if (loadingAnimationVM.open.value == true){
                loadingAnimationVM.startThread(
                    backgroundTask = {
                        pinVM.getAccessDB()

                    }
                )
            }
        }

        if (loadingAnimationVM.open.value == true){
            LoadingDialog(
                viewModel = loadingAnimationVM
            )
        }
        else{
            if (license.value == ""){
                var companyNane by remember { mutableStateOf("") }
                var inputLicenseKey by remember { mutableStateOf("") }
                val companyProfile = signInitVM.companyData.value.find { it.schoolName == companyNane }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                        TextFieldSigningInAndSignOutText(
                            value = companyNane,
                            label ="School Name" ,
                            onValueChange = {companyNane = it },
                            imageVector = Icons.Default.Person
                        )
                    Spacer(modifier = Modifier.fillMaxWidth().padding(10.dp))
                    if (companyProfile != null) {

                        TextFieldSigningInAndSignOutText(
                            value = inputLicenseKey,
                            label = "License Key",
                            onValueChange = { inputLicenseKey = it },
                            imageVector = Icons.Default.Key
                        )
                        Spacer(modifier = Modifier.fillMaxWidth().padding(10.dp))
                        BtnNormal(onclick = {
                            if (companyProfile.accessPin == inputLicenseKey) {
                                scope.launch {
                                    licenseKey.saveKey(inputLicenseKey)
                                }
                                navToSelectFeatureScreen()
                            } else {
                                Toast.makeText(context, "Wrong Access Key", Toast.LENGTH_LONG)
                                    .show()
                            }

                        }, text = "Done")

                    }
                }
            }else{

                val companyProfile = signInitVM.companyData.value.find { it.accessPin == license.value }

                if (companyProfile == null){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BtnNormal(onclick = {
                                scope.launch {
                                    licenseKey.saveKey("")
                                }
                        }, text = "Re-Enter Your License Key")

                    }
                }else{
                    LazyColumn(
                        modifier = Modifier
                            .background(color = Cream)
                            .fillMaxSize()
                    ){

                        item {
                            if(snackbarVisible){
                                Snackbar(
                                    modifier=Modifier.padding(horizontal = 15.dp),
                                    backgroundColor = Color.White,
                                    contentColor = Color.Black,
                                    action = {
                                        Text(modifier = Modifier.clickable {
                                            snackbarVisible=false
                                        }, text = "Dismiss", color = Color.Black, fontSize = 10.sp)
                                    }) {
                                    Text(text = snackbarMessage, color = Color.Black, fontSize = 12.sp )
                                }
                            }

                            PasscodeScreen(
                                onLogoutClick = {

                                    scope.launch {
                                        app.currentUser?.logOut() }
                                    navToLoginScreen()
                                },
                                accountType = accountType.value,
                                passcode = pinVM.passcode.value,
                                onPasscodeChange = { pinVM.passcode.value=it },
                                confirmPasscode = pinVM.newPassword.value,
                                onConfirmPasscodeChange = { pinVM.newPassword.value=it.filter { it.isDigit() } },
                                onConfirmClick = { pinVM.confirmAdmin(
                                    passcodeNotCorrect = {
                                        snackbarMessage = "Passcode not Correct"
                                        snackbarVisible = true
                                    },
                                    passwordFieldEmpty = {
                                        snackbarMessage = "Passcode field is empty"
                                        snackbarVisible = true
                                    },
                                    navToHomeScreen = navToSelectFeatureScreen,
                                    passcodeMoreThanSix = {
                                        snackbarMessage = "Passcode can not exceed six words "
                                        snackbarVisible = true

                                    },
                                    updateAccess = {
                                        pinVM.updateAccessPass(
                                            onError = {
                                                snackbarMessage = "An error occur when changing your passcode"
                                                snackbarVisible = true
                                            },
                                            onSuccess = {
                                                pinVM.changePasswordState.value = false
                                                snackbarMessage = "Passcode change successfully"
                                                snackbarVisible = true
                                                pinVM.newPassword.value = ""
                                                Log.e(TAG, "adminDB: ${pinVM.adminDB.value}", )
                                                Log.e(TAG, "adminPass: ${pinVM.adminPass.value}", )
                                            })
                                    }
                                )
                                },
                                onChangeAccessKeyClick = {
                                    pinVM.changePasswordState.value = !pinVM.changePasswordState.value},
                                changePasswordState=pinVM.changePasswordState.value,
                                user =user
                            )
                        }
                    }
                }


            }

        }




}


@Composable
fun PasscodeScreen(
    onLogoutClick:()->Unit,
    accountType: String,
    passcode: String,
    onPasscodeChange:(String)->Unit,
    confirmPasscode: String,
    onConfirmPasscodeChange:(String)->Unit,
    onConfirmClick:()->Unit,
    onChangeAccessKeyClick:()->Unit,
    changePasswordState: Boolean,
    user: User?
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Cream)
    ) {

        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, top = 10.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(modifier = Modifier
                .clickable { onLogoutClick() }, text = stringResource(id = R.string.log_out)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        if (user != null) {

            TitleAndSub(
                title = "Sign In $accountType",
                subTitle = "Please enter $accountType access code below"
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
            )
            TextFieldSigningInAndSignOutText(
                value = accountType,
                label = stringResource(id = R.string.account_text),
                onValueChange = {},
                imageVector = Icons.Default.Person
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            TextFieldPassword(
                password = passcode,
                label = stringResource(id = R.string.passcode_text),
                onPasswordChange = { onPasscodeChange(it) },
                keyboardType = KeyboardType.Text
            )

            if (changePasswordState) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                TextFieldPassword(
                    password = confirmPasscode,
                    label = stringResource(id = R.string.confirm_passcode),
                    onPasswordChange = { onConfirmPasscodeChange(it) },
                    keyboardType = KeyboardType.Text
                )

            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            BtnNormal(
                onclick = { onConfirmClick() },
                text = stringResource(id = R.string.confirm_passcode)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.clickable { onChangeAccessKeyClick() },
                    text = stringResource(id = R.string.change_your_accesss_key),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                    color = Blue
                )
            }
        }


}


}