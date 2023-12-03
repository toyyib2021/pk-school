package com.stevdza.san.mongodemo.navigation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stevdza.san.mongodemo.screenStudent.oldStudentScreen.OldStudentScreen
import com.stevdza.san.mongodemo.screenStudent.parentView.FamilyParentView
import com.stevdza.san.mongodemo.screenStudent.parentView.ParentDashboard
import com.stevdza.san.mongodemo.screenStudent.parentView.ParentListInParentView
import com.stevdza.san.mongodemo.screenStudent.studentAttendance.StudentAttendance
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.Constants.PARENT_VIEW_DASHBOARD_GRAPH_ROUTE


fun NavGraphBuilder.parentViewDashboard(
    navController: NavHostController
) {

    navigation(
        startDestination = Screen.ChooseAParentForParentView.route,
        route = PARENT_VIEW_DASHBOARD_GRAPH_ROUTE
    ){

        composable(
            route = Screen.ChooseAParentForParentView.route
        ) {
            ParentListInParentView(onBackClick = { navController.navigate(Screen.Home.route) },
                navToDashboardScreen = {
                    navController.navigate(Screen.DashboardForParentView.dashboardForParentViewID(it))
                })
        }

        composable(
            route= Screen.DashboardForParentView.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ){
            val key = it.arguments?.getString(NAVKEY) ?: ""
            ParentDashboard(
                familyID = key,
                onCurrentClassClick = {
                    val convertedList = it.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.CurrentClassAttendanceForParentView.currentClassAttendanceForParentViewID(convertedString))},
                onPreviousClassClick = {
                    val convertedList = it.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.PreviousClassAttendanceForParentView.PreviousClassAttendanceForParentViewID(convertedString))
                },
                onParentAndWardCInforClick={navController.navigate(Screen.FamilyParentView.FamilyParentViewID(it))}
            )

        }

        composable(
            route = Screen.CurrentClassAttendanceForParentView.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ){
            val studentId = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = studentId.split(",")
            val convertedString = convertedList.joinToString("/")
            Log.e(TAG, "attendanceRoute: $convertedString", )
            StudentAttendance(
                studentId = convertedString,
                onBackClick = {
//                    navController.navigate(Screen.DashboardForParentView.dashboardForParentViewID(it))
                }
            )

        }


        composable(
            route = Screen.PreviousClassAttendanceForParentView.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ){
            val studentId = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = studentId.split(",")
            val convertedString = convertedList.joinToString("/")
            Log.e(TAG, "attendanceRoute: $convertedString", )
            OldStudentScreen(
                studentId = convertedString,
                onBackClick ={ navController.navigate(Screen.Home.route) {
                    popUpTo(Constants.ATTENDANCE_GRAPH_ROUTE){
                        inclusive = true
                    }
                } },
                navToOldStudentAttendanceMain = { studentID, cless ->
                    val convertStudentToList = studentID.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertStudentIDToString = convertStudentToList.joinToString(",")
                    navController.navigate(Screen.OldStudentMainAttendance.oldStudentMainAttendanceID(convertStudentIDToString, cless))
                }
            )
        }



        composable(
            route = Screen.FamilyParentView.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ) {
            val familyID = it.arguments?.getString(NAVKEY) ?: ""
            FamilyParentView(
                familyID = familyID,
                navToHome = {navController.navigate(Screen.Home.route)}
            )

        }










    }
}