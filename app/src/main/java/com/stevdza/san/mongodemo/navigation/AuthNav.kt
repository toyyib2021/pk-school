package com.stevdza.san.mongodemo.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.stevdza.san.mongodemo.screenStudent.SelectFeature
import com.stevdza.san.mongodemo.screenStudent.TestingUI
import com.stevdza.san.mongodemo.screenStudent.accountType.AccountType
import com.stevdza.san.mongodemo.screenStudent.auth.AuthenticationScreen
import com.stevdza.san.mongodemo.screenStudent.auth.AuthenticationViewModel
import com.stevdza.san.mongodemo.screenStudent.login.Login
import com.stevdza.san.mongodemo.screenStudent.pinLogin.Passcode
import com.stevdza.san.mongodemo.screenStudent.signUp.SignUp
import com.stevdza.san.mongodemo.util.Constants.AUTH_GRAPH_ROUTE
import com.stevdza.san.mongodemo.screenStudent.splash.Splash
import com.stevdza.san.mongodemo.screenTeacher.movement.MovementHistoryMonth
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState


@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authRoute(
    navController: NavHostController
) {

    navigation(
        startDestination = Screen.Splash.route,
        route = AUTH_GRAPH_ROUTE
    ){


        composable(route = Screen.Splash.route) {
            Splash(navController = navController)
//            TestingUI()
        }


        composable(route = Screen.SignUp.route) {
            SignUp(
                navToSignInScreen = {navController.navigate(Screen.SignIn.route)},
                navToHomeScreen = {navController.navigate(Screen.AccountType.route)}
            )
        }

        composable(route = Screen.SignIn.route) {
            Login(
                navToGoogleOneTap = { navController.navigate(Screen.Authentication.route) },
                navToSignUp = {navController.navigate(Screen.SignUp.route)},
                navToHome = {navController.navigate(Screen.AccountType.route)}
            )

        }

        composable(route = Screen.AccountType.route) {
            AccountType(
                navToPinScreen = { navController.navigate(Screen.PinLogin.route) }
            )

        }


        composable(route = Screen.Authentication.route){
            val viewModel: AuthenticationViewModel = viewModel()
            val authenticated by viewModel.authenticated
            val loadingState by viewModel.loadingState
            val oneTapState = rememberOneTapSignInState()
            val messageBarState = rememberMessageBarState()

            AuthenticationScreen(
                authenticated = authenticated,
                loadingState = loadingState,
                oneTapState = oneTapState,
                messageBarState = messageBarState,
                onButtonClicked = {
                    oneTapState.open()
                    viewModel.setLoading(true)
                },
                onSuccessfulSignIn = { tokenId ->
                    viewModel.signInWithMongoAtlas(
                        tokenId = tokenId,
                        onSuccess = {
                            messageBarState.addSuccess("Successfully Authenticated!")
                            viewModel.setLoading(false)
                        },
                        onError = {
                            messageBarState.addError(it)
                            viewModel.setLoading(false)
                        }
                    )
                },
                onDialogDismissed = { message ->
                    messageBarState.addError(Exception(message))
                    viewModel.setLoading(false)
                },
                navigateToHome = { navController.navigate(Screen.AccountType.route) }
            )
        }


        composable(route = Screen.PinLogin.route) {
            Passcode (
                navToSelectFeatureScreen = {navController.navigate(Screen.SelectFeature.route)},
                navToLoginScreen = {navController.navigate(Screen.SignIn.route)}
            )

        }

        composable(route = Screen.SelectFeature.route) {
            SelectFeature(
                navToLoginPin = {navController.navigate(Screen.AccountType.route)},
                navToStaffAttendance = {navController.navigate(Screen.StaffDashboard.route)},
                navToStudentAttendance ={navController.navigate(Screen.Home.route)}
            )
        }
    }
}