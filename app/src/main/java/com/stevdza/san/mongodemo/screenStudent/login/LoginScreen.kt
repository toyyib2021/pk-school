package com.stevdza.san.mongodemo.screenStudent.login

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.MainActivity
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldPassword
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldSigningInAndSignOutText
import com.stevdza.san.mongodemo.screenStudent.component.TitleAndSub
import com.stevdza.san.mongodemo.screenStudent.signUp.SignUpInitVM
import com.stevdza.san.mongodemo.screenStudent.signUp.SignUpVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import io.realm.kotlin.mongodb.App


@Composable
fun Login(
    navToGoogleOneTap:()->Unit,
    navToSignUp:()->Unit,
    navToHome:()->Unit,
){

    val signIn: SignUpVM = viewModel()

    val contxt = LocalContext.current
    var errorMassage by remember { mutableStateOf("Login") }

    val app = App.create(Constants.APP_ID)
    val user = app.currentUser

    LoginScreen(
        onLoginClick = {
            errorMassage = "Loading..."
            signIn.emailPasswordLogin(
                onSuccess = {
                    errorMassage= "Success"
                    navToHome()
                            },
                emptyField = {
                    errorMassage = "empty Fields" },
                onError = {
                    errorMassage = "Something went wrong check your email and password" },
                context = contxt,
                onInternetConnection = {errorMassage = "Internet Connection Is Off"}
            )
        },
        onGoogleLogoClick = { navToGoogleOneTap() },
        onSignUpClick = {navToSignUp()},
        email = signIn.email.value,
        onEmailChange = {signIn.email.value = it},
        password = signIn.password.value,
        onPasswordChange = {signIn.password.value = it},
        errorMassage = errorMassage
    )

}


@Composable
fun LoginScreen(
    onLoginClick:()->Unit,
    onGoogleLogoClick:()->Unit,
    onSignUpClick:()->Unit,
    email: String,
    onEmailChange:(String)->Unit,
    password:String,
    onPasswordChange:(String)->Unit,
    errorMassage: String
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Cream)
    ) {

        Column(modifier = Modifier
                .fillMaxWidth().weight(4f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp))
                Image(painter = painterResource(id = R.drawable.pk_logo),
                    contentDescription ="pk_logo", contentScale = ContentScale.Inside )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp))
            }

            Column(modifier = Modifier
                .fillMaxWidth().weight(6f),
            ) {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
                TitleAndSub(
                    title = "Login",
                    subTitle = "Please fill the input below",
                )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
                TextFieldSigningInAndSignOutText(
                    value = email,
                    label = "Email",
                    onValueChange = { onEmailChange(it) },
                    imageVector = Icons.Default.Email
                )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
                TextFieldPassword(
                    password = password,
                    label = "Password",
                    onPasswordChange = { onPasswordChange(it) },
                    keyboardType = KeyboardType.Text
                )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
                BtnNormal(onclick = { onLoginClick() }, text =errorMassage )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
//                Column(modifier = Modifier
//                    .fillMaxWidth(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Image(modifier = Modifier
//                        .clickable { onGoogleLogoClick() },
//                        painter = painterResource(id = R.drawable.google_logo),
//                        contentDescription ="google_logo", alignment = Alignment.Center )
//                }
//                Spacer(modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp))
//                Column(modifier = Modifier
//                    .fillMaxWidth(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        modifier = Modifier.clickable { onSignUpClick() },
//                        text = "Don't have an account? Sign Up", fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
//                        color = Blue
//                    )
//                }

            }


        


    }

}