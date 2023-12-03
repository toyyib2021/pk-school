package com.stevdza.san.mongodemo.screenStudent.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.stevdza.san.pk.R
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.navigation.Screen
import com.stevdza.san.mongodemo.util.Constants
import io.realm.kotlin.mongodb.App


@Composable
fun Splash(navController: NavHostController,
) {

    val context = LocalContext.current
    val accountTypeKey = AccountTypeKey(context)
    val accountTypeK = accountTypeKey.getKey.collectAsState(initial = "")
    val getFinOrSecKey by remember { mutableStateOf("") }
    val app = App.create(Constants.APP_ID)
    val user = app.currentUser



    val degrees = remember { Animatable(0f) }
    var fadeInAlpha by remember { mutableFloatStateOf(0f) }

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(visible) {
        degrees.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 500)
        )
        visible = false
        navController.popBackStack()
        if (user != null) {
            navController.navigate(Screen.PinLogin.route)

        } else {
            navController.navigate(Screen.SignIn.route)

        }

    }


    SplashUI(fadeInAlpha = degrees.value)
}

@Composable
fun SplashUI(fadeInAlpha: Float) {
    val modifier = if (isSystemInDarkTheme()) {
        Modifier.background(Color.Black)
    } else {
        Modifier.background(Color.White)
    }
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .graphicsLayer(alpha = fadeInAlpha),
            painter = painterResource(id = R.drawable.pk_logo),
            contentDescription = "o"

        )
    }

}
