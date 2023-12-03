package com.stevdza.san.mongodemo.navigation

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stevdza.san.mongodemo.screenStudent.attendanceIn.AttendanceInScreen
import com.stevdza.san.mongodemo.screenStudent.family.Family
import com.stevdza.san.mongodemo.screenStudent.home.ClassArms
import com.stevdza.san.mongodemo.screenStudent.home.ClassNames
import com.stevdza.san.mongodemo.screenStudent.home.DashboardNew
import com.stevdza.san.mongodemo.screenStudent.home.Session
import com.stevdza.san.mongodemo.screenStudent.onboardedParent.OnboardedParent
import com.stevdza.san.mongodemo.screenStudent.onboardedStudent.OnboardedStudent
import com.stevdza.san.mongodemo.screenStudent.parentAssociateOnboarding.ParentAssociateOnboarding
import com.stevdza.san.mongodemo.screenStudent.parentOnboarding.ParentOnboarding
import com.stevdza.san.mongodemo.screenStudent.scanId.ScanId
import com.stevdza.san.mongodemo.screenStudent.studentOnboarding.StudentOnboardingScreen
import com.stevdza.san.mongodemo.screenStudent.timeInAndOut.StudentTimeInOut
import com.stevdza.san.mongodemo.screenStudent.updateStudentClass.UpdateStudentClassScreen
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_BY_ADMIN
import com.stevdza.san.mongodemo.util.Constants.ONBOARDED_STUDENT
import com.stevdza.san.mongodemo.util.Constants.DASHBOARD_GRAPH_ROUTE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeRoute(
    navController: NavHostController
) {

    navigation(
        startDestination = Screen.Home.route,
        route = DASHBOARD_GRAPH_ROUTE
    ) {

        composable(
            route = Screen.Home.route
        ) {
            DashboardNew(
                navToParentOnboarding = {navController.navigate(Screen.ParentOnboarding.route)},
                navToStudentOnboardList ={navController.navigate(Screen.StudentList.key(
                    ONBOARDED_STUDENT))},
                navToScanCardScreen = {navController.navigate(Screen.ScanId.route)},
                onManualClick = {navController.navigate(Screen.StudentList.key(
                    ATTENDANCE_BY_ADMIN))},
                navToParentOnboarded = {navController.navigate(Screen.ParentOnboarded.route)},
                navToClass ={navController.navigate(Screen.ClassNames.route)},
                navToSession = {navController.navigate(Screen.Session.route)},
                navToSwitchAccount = {navController.navigate(Screen.AccountType.route)},
                navToUpdateStudentClass = {navController.navigate(Screen.UpdateStudentClass.route)},
                navToArms = {navController.navigate(Screen.ClassArms.route)},
                onFullDetailsClick ={navController.navigate(Screen.DailyAttendanceReport.route)},
                navToAttendanceClass = {
                    val convertedList = it.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.AttendanceReportByClass.attendanceReportByClassID(convertedString))
                                       },
                navToAttendanceStudent = {
                    val convertedList = it.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.AttendanceReportForStudent.attendanceReportForStudentID(convertedString))},
                navToOldStudentAttendance = {studentId->
                    val convertedList = studentId.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.OldStudentAttendance.oldStudentAttendanceID(convertedString))},
                navToChooseAStudentForParentView = {
                    navController.navigate(Screen.ChooseAParentForParentView.route)
                },
                navToOboardAStudent = {navController.navigate(Screen.StudentOnboarding.studentFamilyID("r"))},
                navToTimeInAndOut = {navController.navigate(Screen.TimeInAndOutStudent.route)}
            )

        }



        composable(
            route = Screen.ParentOnboarding.route
        ) {
            ParentOnboarding(
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
                navToOnboardedParent = {navController.navigate(Screen.ParentOnboarded.route)}
            )

        }

        composable(
            route = Screen.ParentOnboarded.route
        ) {
            OnboardedParent(
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
                navToFamilyScreen = {navController.navigate(Screen.Family.familyID(it))}
            )

        }

        composable(
            route= Screen.StudentList.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ){ it ->
            val key = it.arguments?.getString(NAVKEY) ?: ""
            OnboardedStudent(
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
                navToFamily = {navController.navigate(Screen.Family.familyID(it))},
                takeAttendanceOrGoToFamily=key,
                navToTakeAttendance={id ->
                    navController.navigate(Screen.SignInStudent.signInStudentFamilyID(id))
//                    Log.e(TAG, "homeRoute: id->$id", )
                }
            )

        }


        composable(
            route = Screen.StudentOnboarding.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ) {
            val familyId = it.arguments?.getString(NAVKEY) ?: ""
            StudentOnboardingScreen(
                onBackCLick = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
                navToFamilyScreen = {
                    if (familyId == "r") {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(DASHBOARD_GRAPH_ROUTE){
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(Screen.Family.familyID(familyId))
                    }
                },
                familyID = familyId
            )

        }



        composable(
            route = Screen.ScanId.route
        ) {
            ScanId(
                navToAttendanceScreen = {navController.navigate(Screen.SignInStudent.signInStudentFamilyID(it))},
                onBackClick = {navController.navigate(Screen.Home.route)}
            )

        }


        composable(
            route = Screen.Family.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ) {
            val familyId = it.arguments?.getString(NAVKEY) ?: ""
            Family(
                familyID = familyId,
                navToHome = { navController.popBackStack() },
                onAddGuardianClick = { navController.navigate(Screen.ParentAssociateOnboarding.parentAssociateFamilyID(familyId))},
                onAddStudentClick = {navController.navigate(Screen.StudentOnboarding.studentFamilyID(familyId))}
            )
        }



        composable(
            route= Screen.ParentAssociateOnboarding.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ){
            val memberId = it.arguments?.getString(NAVKEY) ?: ""
            ParentAssociateOnboarding(
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
                navToFamily = { navController.navigate(Screen.Family.familyID(memberId)) },
                familyId = memberId
            )
        }


        composable(
            route = Screen.SignInStudent.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ) {
            val memberId = it.arguments?.getString(NAVKEY) ?: ""
            val scope = rememberCoroutineScope()

            AttendanceInScreen(
                familyId = memberId,
                navToHome = {
                    scope.launch {
                        delay(2000L)
                    }
                    navController.navigate(Screen.Home.route) },
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
            )
//            AttendanceTest(familyID = memberId)
        }




        // Menu Bar Screens

        composable(
            route = Screen.ClassNames.route
        ) {
            ClassNames(
                navToHome = { navController.navigate(Screen.Home.route) }
            )
        }


        composable(
            route = Screen.Session.route
        ) {
            Session(onBackClick = {navController.navigate(Screen.Home.route)})
        }

        composable(
            route = Screen.ClassArms.route
        ) {
            ClassArms(
                navToHome = {
                    navController.navigate(Screen.Home.route)
                }
            )


        }

        composable(
            route = Screen.TimeInAndOutStudent.route
        ) {
           StudentTimeInOut(onBackClick = {navController.navigate(Screen.Home.route)})
        }

        composable(
            route = Screen.UpdateStudentClass.route
        ) {

            UpdateStudentClassScreen(
                navToHome = { navController.navigate(Screen.Home.route) {
                    popUpTo(DASHBOARD_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
            )

        }



    }


}