package com.stevdza.san.mongodemo.navigation

import android.content.ContentValues
import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stevdza.san.mongodemo.screenTeacher.absentStaffRecord.AbsentStaffRecord
import com.stevdza.san.mongodemo.screenTeacher.absentStaffRecord.AbsentStaffRecordList
import com.stevdza.san.mongodemo.screenTeacher.dailyReport.StaffDailyAttendance
import com.stevdza.san.mongodemo.screenTeacher.dashboard.ActivateStaff
import com.stevdza.san.mongodemo.screenTeacher.dashboard.DashboardStaff
import com.stevdza.san.mongodemo.screenTeacher.movement.MainMovement
import com.stevdza.san.mongodemo.screenTeacher.movement.MovementHistoryMonth
import com.stevdza.san.mongodemo.screenTeacher.movement.MovementLog
import com.stevdza.san.mongodemo.screenTeacher.movement.MovementScanId
import com.stevdza.san.mongodemo.screenTeacher.onboardStaff.OnboardStaff
import com.stevdza.san.mongodemo.screenTeacher.scanCard.StaffScanId
import com.stevdza.san.mongodemo.screenTeacher.siginAndOut.StaffSignInAndOut
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.StaffOnboard
import com.stevdza.san.mongodemo.screenTeacher.staffProfile.StaffProfile
import com.stevdza.san.mongodemo.screenTeacher.staffProfileHistory.PastStaffAttendanceReport
import com.stevdza.san.mongodemo.screenTeacher.staffProfileHistory.PastStaffAttendanceReportMonth
import com.stevdza.san.mongodemo.screenTeacher.staffProfileHistory.StaffProfileHistory
import com.stevdza.san.mongodemo.screenTeacher.teacherAttendance.StaffAttendanceHistory
import com.stevdza.san.mongodemo.screenTeacher.teacherAttendance.StaffAttendanceHistoryMonth
import com.stevdza.san.mongodemo.screenTeacher.timeInAndOut.TimeInOut
import com.stevdza.san.mongodemo.util.Constants.STAFF_ATTENDANCE_GRAPH_ROUTE


fun NavGraphBuilder.staffAttendanceRoute(
    navController: NavHostController
) {

    navigation(
        startDestination = Screen.StaffDashboard.route,
        route = STAFF_ATTENDANCE_GRAPH_ROUTE
    ) {


        composable(
            route = Screen.StaffDashboard.route
        ) {
            DashboardStaff(
                onOnboardStaffClick = { navController.navigate(Screen.OnboardStaff.route) },
                onStaffOnboardClick = { navController.navigate(Screen.StaffOnboard.route) },
                onManualClick = { navController.navigate(Screen.StaffSignInManual.route) },
                navToScanCardScreen = { navController.navigate(Screen.StaffSignInCard.route) },
                navToSwitchAccount = { navController.navigate(Screen.AccountType.route) },
                onFullDetailsClick = { navController.navigate(Screen.StaffDailyReport.route) },
                navToOldStaffAttendance = {
                    val convertedList = it.split("/")
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(
                        Screen.PastStaffReportProfile.pastStaffReportProfileId(
                            convertedString
                        )
                    )
                },
                navToStaffAttendance = {
                    val convertedList = it.split("/")
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(Screen.StaffReport.staffReportId(convertedString))
                },
                navToStaffAttendanceMonth = {
                    val convertedList = it.split("/")
                    val convertedString = convertedList.joinToString(",")
                    navController.navigate(
                        Screen.StaffReportMonths.staffReportMonthsId(
                            convertedString
                        )
                    )
                },
                navToAbsentRecord = { navController.navigate(Screen.AbsentStaffRecordList.route) },
//                navToMovement = { navController.navigate(Screen.MovementMainScreen.route) },
                navToActivateStaff = { navController.navigate(Screen.ActivateStaffScreen.route) },
                navToTimeInOut = { navController.navigate(Screen.TimeInAndOutStaff.route) }
            )
        }



        composable(
            route = Screen.OnboardStaff.route
        ) {
            OnboardStaff(onBackCLick = { navController.navigate(Screen.StaffDashboard.route) },
                navToStaffOnboardScreen = { navController.navigate(Screen.StaffDashboard.route) })
        }

        composable(
            route = Screen.ActivateStaffScreen.route
        ) {
            ActivateStaff(onBackClick = { navController.navigate(Screen.StaffDashboard.route) },
                navToStaffProfile = { navController.navigate(Screen.StaffProfile.staffProfileID(it)) })
        }


        composable(
            route = Screen.StaffOnboard.route
        ) {
            StaffOnboard(onBackClick = { navController.navigate(Screen.StaffDashboard.route) },
                navToStaffProfile = { navController.navigate(Screen.StaffProfile.staffProfileID(it)) })
        }


        composable(
            route = Screen.StaffProfile.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                }
            )
        ) {
            val key = it.arguments?.getString(NAVKEY) ?: ""
            StaffProfile(
                id = key,
                onBackClick = { navController.navigate(Screen.StaffOnboard.route) })
        }



        composable(
            route = Screen.StaffSignInManual.route
        ) {
            StaffOnboard(onBackClick = { navController.navigate(Screen.StaffDashboard.route) },
                navToStaffProfile = {
                    navController.navigate(
                        Screen.StaffSignInAndOut.staffSignInAndOutId(
                            it
                        )
                    )
                })
        }

        composable(
            route = Screen.StaffSignInCard.route
        ) {
            StaffScanId(
                navToAttendanceScreen = {
                    navController.navigate(
                        Screen.StaffSignInAndOut.staffSignInAndOutId(
                            it
                        )
                    )
                },
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) }
            )
        }


        composable(
            route = Screen.StaffSignInAndOut.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                }
            )
        ) {
            val key = it.arguments?.getString(NAVKEY) ?: ""
            StaffSignInAndOut(id = key,
                navToFeature = { navController.navigate(Screen.SelectFeature.route) },
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) }
            )

        }





        composable(
            route = Screen.StaffDailyReport.route
        ) {
            StaffDailyAttendance(
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) }
            )
        }

        composable(
            route = Screen.StaffReport.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ) {
            val key = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = key.split(",")
            val convertedString = convertedList.joinToString("/")
            StaffAttendanceHistory(staffId = convertedString,
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) })

        }

        composable(
            route = Screen.StaffReportMonths.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ) {
            val key = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = key.split(",")
            val convertedString = convertedList.joinToString("/")
            StaffAttendanceHistoryMonth(staffId = convertedString,
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) })

        }


        composable(
            route = Screen.PastStaffReportProfile.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                })
        ) {
            val key = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = key.split(",")
            val convertedString = convertedList.joinToString("/")
            StaffProfileHistory(
                staffId = convertedString,
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) },
                navToPastStaffAttendance = { staffId: String, session: String ->
                    val convertedListOne = staffId.split("/")
                    val convertedStringOne = convertedListOne.joinToString(",")
                    val convertedListTwo = session.split("/")
                    val convertedStringTwo = convertedListTwo.joinToString(",")
                    navController.navigate(
                        Screen.PastStaffAttendanceReport.pastStaffAttendanceReport(
                            convertedStringOne,
                            convertedStringTwo
                        )
                    )
                },
                navToPastStaffAttendanceMonth = { staffId: String, session: String ->
                    val convertedListOne = staffId.split("/")
                    val convertedStringOne = convertedListOne.joinToString(",")
                    val convertedListTwo = session.split("/")
                    val convertedStringTwo = convertedListTwo.joinToString(",")
                    navController.navigate(
                        Screen.PastStaffAttendanceReportMonth.pastStaffAttendanceReportMonthId(
                            convertedStringOne,
                            convertedStringTwo
                        )
                    )
                }
            )
        }


        composable(
            route = Screen.PastStaffAttendanceReport.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                },
                navArgument(NAVKEY_TWO) {
                    type = NavType.StringType
                }
            )
        ) {
            val staffId = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = staffId.split(",")
            val convertedString = convertedList.joinToString("/")
            Log.e(ContentValues.TAG, "attendanceRoute: $convertedString",)

            val session = it.arguments?.getString(NAVKEY_TWO) ?: ""
            val convertedListTwo = session.split(",")
            val convertedStringTwo = convertedListTwo.joinToString("/")

            PastStaffAttendanceReport(staffId = convertedString, session = convertedStringTwo,
                onBackClick = {
                    val convertStudentToList = it.split("/")
                    val convertStudentIDToString = convertStudentToList.joinToString(",")
                    navController.navigate(
                        Screen.PastStaffReportProfile.pastStaffReportProfileId(
                            convertStudentIDToString
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.PastStaffAttendanceReportMonth.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                },
                navArgument(NAVKEY_TWO) {
                    type = NavType.StringType
                }
            )
        ) {
            val staffId = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = staffId.split(",")
            val convertedString = convertedList.joinToString("/")
            Log.e(ContentValues.TAG, "attendanceRoute: $convertedString",)

            val session = it.arguments?.getString(NAVKEY_TWO) ?: ""
            val convertedListTwo = session.split(",")
            val convertedStringTwo = convertedListTwo.joinToString("/")

            PastStaffAttendanceReportMonth(staffId = convertedString, session = convertedStringTwo,
                onBackClick = {
                    val convertStudentToList = it.split("/")
                    val convertStudentIDToString = convertStudentToList.joinToString(",")
                    navController.navigate(
                        Screen.PastStaffReportProfile.pastStaffReportProfileId(
                            convertStudentIDToString
                        )
                    )
                }
            )
        }




        composable(
            route = Screen.AbsentStaffRecordList.route
        ) {
            AbsentStaffRecordList(
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) },
                onStaffClick = { staffId, session ->
                    val convertedListOne = staffId.split("/")
                    val convertedStringOne = convertedListOne.joinToString(",")
                    val convertedListTwo = session.split("/")
                    val convertedStringTwo = convertedListTwo.joinToString(",")
                    navController.navigate(
                        Screen.AbsentStaffRecord.absentStaffRecordId(
                            convertedStringOne,
                            convertedStringTwo
                        )
                    )
                }
            )
        }



        composable(
            route = Screen.AbsentStaffRecord.route,
            arguments = listOf(
                navArgument(NAVKEY) {
                    type = NavType.StringType
                },
                navArgument(NAVKEY_TWO) {
                    type = NavType.StringType
                }
            )
        ) {
            val staffId = it.arguments?.getString(NAVKEY) ?: ""
            val convertedList = staffId.split(",")
            val convertedString = convertedList.joinToString("/")
            Log.e(ContentValues.TAG, "attendanceRoute: $convertedString",)

            val session = it.arguments?.getString(NAVKEY_TWO) ?: ""
            val convertedListTwo = session.split(",")
            val convertedStringTwo = convertedListTwo.joinToString("/")

            AbsentStaffRecord(
                onBackClick = { navController.navigate(Screen.AbsentStaffRecordList.route) },
                session = convertedStringTwo, staffId = convertedString
            )

        }

        composable(
            route = Screen.MovementMainScreen.route
        ) {
            MainMovement(
                navToStaffMovement = { navController.navigate(Screen.MovementLogScreen.route) },
                navToMovementHistory = { navController.navigate(Screen.StaffListMovementScreen.route) },
                onBackClick = { navController.navigate(Screen.StaffDashboard.route) },
                navToLogMovement = { navController.navigate(Screen.StaffMovementScanIdScreen.route) }
            )

        }

        composable(
            route = Screen.MovementLogScreen.route
        ) {
            MovementLog(onBackClick = { navController.navigate(Screen.MovementMainScreen.route) })
        }


        composable(
            route = Screen.StaffMovementScanIdScreen.route
        ) {
            MovementScanId(
                navToAttendanceScreen = {},
                onBackClick = { navController.navigate(Screen.MovementMainScreen.route) },
                navToStaffDasboard = { navController.navigate(Screen.StaffDashboard.route) }
            )
        }

        composable(
            route = Screen.TimeInAndOutStaff.route
        ) {
            TimeInOut(
                onBackClick = {navController.navigate(Screen.StaffDashboard.route)}
            )
        }


        composable(
            route = Screen.StaffListMovementScreen.route
        ) {
            MovementHistoryMonth()
        }
    }

}