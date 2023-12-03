package com.stevdza.san.mongodemo.navigation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stevdza.san.mongodemo.screenStudent.classAttendance.ClassAttendance
import com.stevdza.san.mongodemo.screenStudent.dailyAttendance.DailyAttendance
import com.stevdza.san.mongodemo.screenStudent.oldStudentScreen.OldStudentAttendance
import com.stevdza.san.mongodemo.screenStudent.oldStudentScreen.OldStudentScreen
import com.stevdza.san.mongodemo.screenStudent.studentAttendance.StudentAttendance
import com.stevdza.san.mongodemo.screenStudent.studentAttendance.StudentAttendanceList
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_GRAPH_ROUTE


fun NavGraphBuilder.attendanceRoute(
    navController: NavHostController
) {

    navigation(
        startDestination = Screen.DailyAttendanceReport.route,
        route = ATTENDANCE_GRAPH_ROUTE
    ){

        composable(
            route = Screen.DailyAttendanceReport.route
        ) {
            DailyAttendance { navController.navigate(Screen.Home.route) {
                popUpTo(Constants.ATTENDANCE_GRAPH_ROUTE){
                    inclusive = true
                }
            } }
        }

        composable(
            route= Screen.AttendanceReportByClass.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ){
            val key = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = key.split(",")
            val convertedString = convertedList.joinToString("/")
            ClassAttendance(
                className = convertedString,
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(Constants.ATTENDANCE_GRAPH_ROUTE){
                        inclusive = true
                    }
                } }
            )

        }

        composable(
            route = Screen.AttendanceReportForStudent.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                }
            )
        ){
            val key = it.arguments?.getString(NAVKEY) ?: ""
            val convertedListKey = key.split(",")
            val convertedStringKey = convertedListKey.joinToString("/")
            StudentAttendanceList(
                className = convertedStringKey,
                navToStudentAttendance = {studentId->
                    val convertedList = studentId.split("/")
//                    Log.e(TAG, "convertedList: $convertedList", )
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.OneStudentAttendance.oneStudentAttendanceID(convertedString))
                },
                onBackClick = { navController.navigate(Screen.Home.route) {
                    popUpTo(Constants.ATTENDANCE_GRAPH_ROUTE){
                        inclusive = true
                    }
                } }
            )

        }

        composable(
            route = Screen.OneStudentAttendance.route,
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
                onBackClick = { navController.navigate(Screen.AttendanceReportForStudent.attendanceReportForStudentID(it))}
            )
        }


        composable(
            route = Screen.OldStudentAttendance.route,
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
            route = Screen.OldStudentMainAttendance.route,
            arguments = listOf(
                navArgument(NAVKEY){
                    type= NavType.StringType
                },
                navArgument(NAVKEY_TWO){
                    type= NavType.StringType
                }
            )
        ){
            val studentId = it.arguments?.getString(NAVKEY) ?: ""
            val cless = it.arguments?.getString(NAVKEY_TWO) ?: ""
            val convertedList = studentId.split(",")
            val convertedString = convertedList.joinToString("/")
            Log.e(TAG, "attendanceRoute: $convertedString", )
            OldStudentAttendance(studentId = convertedString, cless = cless,
                onBackClick = {
                    val convertStudentToList = it.split("/")
                    val convertStudentIDToString = convertStudentToList.joinToString(",")
                    navController.navigate(Screen.OldStudentAttendance.oldStudentAttendanceID(convertStudentIDToString))}
            )
        }



    }
}