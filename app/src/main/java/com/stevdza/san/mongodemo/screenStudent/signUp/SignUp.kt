package com.stevdza.san.mongodemo.screenStudent.signUp

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.dataStore.FullNameKey
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldPassword
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldSigningInAndSignOutText
import com.stevdza.san.mongodemo.screenStudent.component.TitleAndSub
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.launch

@Composable
fun SignUp(navToSignInScreen:()->Unit, navToHomeScreen:()->Unit){

    val signUpVM: SignUpVM= viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fullNameSave = FullNameKey(context)
    val app = App.create(Constants.APP_ID)
    val user = app.currentUser

    signUpVM.accessPin.value = signUpVM.generateAlphallumeric(17)


    var errorMassage by remember { mutableStateOf("Sign Up") }
    var companySetup by remember { mutableStateOf("0") }
    var studentSetup by remember { mutableStateOf("0") }
    var staffSetup by remember { mutableStateOf("0") }
    var parentSetup by remember { mutableStateOf("0") }
    var staffTimeSetup by remember { mutableStateOf("0") }
    var studentTimeSetup by remember { mutableStateOf("0") }
    var firstTermSetup by remember { mutableStateOf("0") }
    var secondTermSetup by remember { mutableStateOf("0") }
    var thirdSetup by remember { mutableStateOf("0") }
    val defultPinSetup by remember { mutableStateOf("0") }
    var sessionYearSetup by remember { mutableStateOf("0") }

    val deleteAllCompanyProfile = signUpVM.deleteAllCompanyProfile.value
    val deleteAllStudentData = signUpVM.deleteAllStudentData.value
    val deleteAllTeacherProfile = signUpVM.deleteAllTeacherProfile.value
    val deleteAllParent = signUpVM.deleteAllParent.value
    val deleteAllPin = signUpVM.deleteAllPin.value
    val deleteAllTimeInOut = signUpVM.deleteAllTimeInOut.value
    val deleteAllSession = signUpVM.deleteAllSession.value
    val deleteAllTerm = signUpVM.deleteAllTerm.value

    Log.e(TAG, "SignUp: deleteAllCompanyProfile -> ${deleteAllCompanyProfile.size}", )
    Log.e(TAG, "SignUp: deleteAllStudentData -> ${deleteAllStudentData.size}", )
    Log.e(TAG, "SignUp: deleteAllTeacherProfile -> ${deleteAllTeacherProfile.size}", )
    Log.e(TAG, "SignUp: deleteAllParent -> ${deleteAllParent.size}", )
    Log.e(TAG, "SignUp: deleteAllPin -> ${deleteAllPin.size}", )
    Log.e(TAG, "SignUp: deleteAllTimeInOut -> ${deleteAllTimeInOut.size}", )
    Log.e(TAG, "SignUp: deleteAllSession -> ${deleteAllSession.size}", )
    Log.e(TAG, "SignUp: deleteAllTerm -> ${deleteAllTerm.size}", )




//    if ( user != null) {
    if ( errorMassage == "Success") {
        LazyColumn(
            modifier = Modifier
                .background(color = Cream)
                .fillMaxSize()
        ){
            item {
                SignUpScreen(
                    onSignUpClick = {

                        signUpVM.insertCompany(onSuccess = {companySetup = "Complete"}, onError = {companySetup = "Error"},)
                        signUpVM.insertStaff(onSuccess = {staffSetup = "Complete"}, onError = {staffSetup = "Error"},)
                        signUpVM.insertStudentData(onSuccess = {studentSetup = "Complete"}, onError = {studentSetup = "Error"},)
                        signUpVM.insertParentPass(onSuccess = {parentSetup = "Complete"}, onError = {parentSetup = "Error"},)
                        signUpVM.insertStaffTime(onSuccess = {staffTimeSetup = "Complete"}, onError = {staffTimeSetup = "Error"}, emptyFeild = {})
                        signUpVM.insertStudentTime(onSuccess = {studentTimeSetup = "Complete"}, onError = {studentTimeSetup = "Error"}, emptyFeild = {})
                        signUpVM.insertSessionYear(onSuccess = {sessionYearSetup = "Complete"}, onError = {sessionYearSetup = "Error"}, emptyFeild = {})
                        signUpVM.insertFirstTerm(
                            onSuccess = {
                                firstTermSetup = "Complete"
                                signUpVM.insertSecondTerm(
                                    onSuccess = {
                                        secondTermSetup = "Complete"
                                        signUpVM.insertThirdTerm(onSuccess = {thirdSetup = "Complete"}, onError = {thirdSetup = "Error"}, emptyFeild = {})
                                                },
                                    onError = {secondTermSetup = "Error"},
                                    emptyFeild = {})
                            },
                            onError = { firstTermSetup = "Error" },
                            emptyFeild = {})
                        signUpVM.insertAccessDB()
                    },
                    onSignInClick ={navToSignInScreen()},
                    fullName = signUpVM.schoolName.value,
                    onFullNameClick= {signUpVM.schoolName.value=it},
                    phone =signUpVM.repName.value ,
                    onPhoneClick ={signUpVM.repName.value = it} ,
                    email =signUpVM.email.value,
                    onEmailClick ={signUpVM.email.value =it },
                    password =signUpVM.password.value,
                    onPasswordClick ={signUpVM.password.value=it},
                    confirmPassword =signUpVM.accessPin.value,
                    onConfirmPasswordClick = {signUpVM.accessPin.value=it},
                    signUp = errorMassage,
                    fullNameLabel = "School Name",
                    phoneLabel = "Full Name",
                    comfirmPassLabel ="Access Pin"

                )
            }

            item{
                Column() {
                    Row(modifier = Modifier.horizontalScroll(state = rememberScrollState())) {
                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllCompany() }) {
                                Text(text = "Company Setup")
                                Text(text = companySetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllStudent() }) {
                                Text(text = "Student Setup")
                                Text(text = studentSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllTeacher() }) {
                                Text(text = "Staff Setup")
                                Text(text = staffSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllParent() }) {
                                Text(text = "Parent Setup")
                                Text(text = parentSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllTimeInOut() }) {
                                Text(text = "Staff Time Setup")
                                Text(text = staffTimeSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllTimeInOut() }) {
                                Text(text = "Student Time Setup")
                                Text(text = studentTimeSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllTerm() }) {
                                Text(text = "First Setup")
                                Text(text = firstTermSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable {  }) {
                                Text(text = "Second Setup")
                                Text(text = secondTermSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable {  }) {
                                Text(text = "Third Setup")
                                Text(text = thirdSetup,)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))


                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllPin() }) {
                                Text(text = "Pin Setup")
                                Text(text = defultPinSetup,)
                            }
                        }


                        Card(elevation = 5.dp) {
                            Column(modifier = Modifier
                                .padding(10.dp)
                                .clickable { signUpVM.deleteAllSession() }) {
                                Text(text = "Session Year")
                                Text(text = sessionYearSetup,)
                            }
                        }
                    }
//                    SelectionContainer {
//                        Card(elevation = 5.dp) {
//                            Column(modifier = Modifier
//                                .padding(10.dp)
//                                .clickable { signUpVM.deleteAllTerm() }) {
//                                Text(text = "Access Pin")
//                                Text(text = signUpVM.accessPin.value,)
//                            }
//                        }
//                    }
                    BtnNormal(onclick = {
                        navToHomeScreen()
                    }, text ="Done" )
                }

            }
        }
    }else{
        LazyColumn(
            modifier = Modifier
                .background(color = Cream)
                .fillMaxSize()
        ){
            item {
                SignUpScreen(
                    onSignUpClick = {
                        errorMassage = "Loading..."
                        signUpVM.emailPasswordAuth(

                            onSuccess = {

                                errorMassage = "Success"},
                            unMarchedPassword = {errorMassage = "password did not march"},
                            emptyField = {errorMassage = "some field are empty"},
                            onError = {errorMassage = "something went wrong try another email"},
                            context = context,
                            onInternetConnection = { errorMassage = "Internet Connection Is Off"}
                        )
                        scope.launch {
                            fullNameSave.saveKey(signUpVM.fullName.value)
                        }
                    },
                    onSignInClick ={navToSignInScreen()},
                    fullName = signUpVM.fullName.value,
                    onFullNameClick= {signUpVM.fullName.value=it},
                    phone =signUpVM.phone.value ,
                    onPhoneClick ={signUpVM.phone.value = it} ,
                    email =signUpVM.email.value,
                    onEmailClick ={signUpVM.email.value =it },
                    password =signUpVM.password.value,
                    onPasswordClick ={signUpVM.password.value=it},
                    confirmPassword =signUpVM.confirmPassword.value,
                    onConfirmPasswordClick = {signUpVM.confirmPassword.value=it},
                    signUp = errorMassage
                )
            }
        }
    }


}

@Composable
fun SignUpScreen(
    onSignUpClick:()->Unit,
    onSignInClick:()->Unit,
    fullName: String,
    onFullNameClick:(String)->Unit,
    phone: String,
    onPhoneClick:(String)->Unit,
    email: String,
    onEmailClick:(String)->Unit,
    password: String,
    onPasswordClick:(String)->Unit,
    confirmPassword: String,
    onConfirmPasswordClick:(String)->Unit,
    signUp:String,
    fullNameLabel: String = stringResource(id = R.string.full_name),
    phoneLabel: String = stringResource(id = R.string.phone_text),
    comfirmPassLabel :String  = stringResource(id = R.string.confirm_password)
){


    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Cream)) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
        TitleAndSub(
            title = stringResource(id = R.string.create_account),
            subTitle = stringResource(id = R.string.create_account))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        TextFieldSigningInAndSignOutText(
            value = fullName,
            label =fullNameLabel ,
            onValueChange = {onFullNameClick(it)},
            imageVector = Icons.Default.Person)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        TextFieldSigningInAndSignOutText(
            value = phone,
            label = phoneLabel,
            onValueChange = {onPhoneClick(it)},
            imageVector = Icons.Default.Phone)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        TextFieldSigningInAndSignOutText(
            value = email,
            label = stringResource(id = R.string.email_text),
            onValueChange = {onEmailClick(it)},
            imageVector = Icons.Default.Email)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        TextFieldPassword(
            password = password,
            label = stringResource(id = R.string.password_text),
            onPasswordChange = {onPasswordClick(it)},
            keyboardType = KeyboardType.Text)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        TextFieldPassword(
            password = confirmPassword,
            label = comfirmPassLabel,
            onPasswordChange = {onConfirmPasswordClick(it)},
            keyboardType = KeyboardType.Text)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
        BtnNormal(
            onclick = { onSignUpClick() },
            text = signUp)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.clickable { onSignInClick() },
                text = stringResource(id = R.string.already_sign_in),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                color = Blue
            )
        }

    }
}


