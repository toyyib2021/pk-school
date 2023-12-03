package com.stevdza.san.mongodemo.navigation


const val NAVKEY = "order_iD"
const val NAVKEY_TWO = "order_iD_two"
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object SignUp : Screen("sign_up_screen")
    object SignIn : Screen("sign_in_screen")
    object Authentication : Screen(route = "authentication_screen")
    object AccountType : Screen("account_type_screen")
    object PinLogin : Screen("pin_login_screen")
    object SelectFeature : Screen("select_feature_screen")

    object Home : Screen(route = "home_screen")
    object ScanId : Screen(route = "scan_Id_screen")


    object ParentOnboarding : Screen(route = "parent_onboarding_screen")
    object ParentOnboarded : Screen(route = "parent_onboarded_screen")

    object StudentList : Screen("student_list_screen/{$NAVKEY}"){
        fun key(key: String): String{
            return "student_list_screen/$key"
        }
    }

    object Family : Screen("family_screen/{$NAVKEY}"){
        fun familyID(key: String): String{
            return "family_screen/$key"
        }
    }

    object StudentOnboarding : Screen("student_onboarding_screen/{$NAVKEY}"){
        fun studentFamilyID(key: String): String{
            return "student_onboarding_screen/$key"
        }
    }

    object ParentAssociateOnboarding : Screen("parent_associate_onboarding_screen/{$NAVKEY}"){
        fun parentAssociateFamilyID(key: String): String{
            return "parent_associate_onboarding_screen/$key"
        }
    }

    object SignInStudent : Screen("sign_in_student_screen/{$NAVKEY}"){
        fun signInStudentFamilyID(key: String): String{
            return "sign_in_student_screen/$key"
        }
    }

    object SignOutStudent : Screen("sign_out_student_screen/{$NAVKEY}"){
        fun signInStudentFamilyID(key: String): String{
            return "sign_out_student_screen/$key"
        }
    }

    // STUDENT ATTENDANCE ROUTE'S
    object DailyAttendanceReport : Screen(route = "daily_attendance_report_screen")

    object AttendanceReportByClass : Screen("attendance_report_by_class_screen/{$NAVKEY}"){
        fun attendanceReportByClassID(key: String): String{
            return "attendance_report_by_class_screen/$key"
        }
    }

    object AttendanceReportForStudent : Screen(route = "attendance_report_for_student_screen/{$NAVKEY}"){
        fun attendanceReportForStudentID(key: String): String{
            return "attendance_report_for_student_screen/$key"
        }
    }

    object OneStudentAttendance : Screen("one_student_attendance_screen/{$NAVKEY}"){
        fun oneStudentAttendanceID(key: String): String{
            return "one_student_attendance_screen/$key"
        }
    }

    object OldStudentAttendance : Screen("old_student_attendance_screen/{$NAVKEY}"){
        fun oldStudentAttendanceID(key: String): String{
            return "old_student_attendance_screen/$key"
        }
    }


    object OldStudentMainAttendance : Screen("old_student_main_attendance_screen/{$NAVKEY}/{$NAVKEY_TWO}"){
        fun oldStudentMainAttendanceID(key: String, key2: String): String{
            return "old_student_main_attendance_screen/$key/$key2"
        }
    }


    object Session : Screen(route = "session_screen")
    object ClassNames : Screen(route = "class_names_screen")
    object ClassArms : Screen(route = "class_arms_screen")
    object UpdateStudentClass : Screen(route = "update_class_screen")
    object TimeInAndOutStudent : Screen(route = "time_in_and_out_screen")
    object TimeInAndOutStaff : Screen(route = "staff_time_in_and_out_screen")



    // Parent Screen
    object ChooseAParentForParentView : Screen(route = "choose_a_student_parent_view_screen")

    object DashboardForParentView : Screen(route = "dashboard_parent_view_screen/{$NAVKEY}"){
        fun dashboardForParentViewID(key: String): String{
            return "dashboard_parent_view_screen/$key"
        }
    }
    object CurrentClassAttendanceForParentView : Screen(route = "current_class_attendance_parent_view_screen/{$NAVKEY}"){
        fun currentClassAttendanceForParentViewID(key: String): String{
            return "current_class_attendance_parent_view_screen/$key"
        }
    }
    object PreviousClassAttendanceForParentView : Screen(route = "previous_class_attendance_parent_view_screen/{$NAVKEY}"){
        fun PreviousClassAttendanceForParentViewID(key: String): String{
            return "previous_class_attendance_parent_view_screen/$key"
        }
    }

    object FamilyParentView : Screen(route = "family_parent_view_screen/{$NAVKEY}"){
        fun FamilyParentViewID(key: String): String{
            return "family_parent_view_screen/$key"
        }
    }

    // STAFF ATTENDANCE ROUTE
    object StaffDashboard : Screen(route = "staff_dashboard_route")
    object StaffDailyReport : Screen(route = "staff_daily_report_screen")

    object PastStaffReport : Screen(route = "past_staff_report_screen")
    object StaffOnboard : Screen(route = "staff_onboard_screen")
    object OnboardStaff : Screen(route = "onboard_staff_screen")

    object StaffProfile : Screen(route = "staff_profile_screen/{$NAVKEY}"){
        fun staffProfileID(key: String): String{
            return "staff_profile_screen/$key"
        }
    }

    object StaffSignInManual : Screen(route = "staff_sign_in_manual_screen")
    object StaffSignInCard : Screen(route = "staff_sign_in_card_screen")

    object StaffSignInAndOut : Screen(route = "staff_sign_in_and_out_screen/{$NAVKEY}"){
        fun staffSignInAndOutId(key: String): String{
            return "staff_sign_in_and_out_screen/$key"
        }
    }

    object StaffReport : Screen(route = "staff_report_screen/{$NAVKEY}"){
        fun staffReportId(key: String): String{
            return "staff_report_screen/$key"
        }
    }

    object StaffReportMonths : Screen(route = "staff_report_month_screen/{$NAVKEY}"){
        fun staffReportMonthsId(key: String): String{
            return "staff_report_month_screen/$key"
        }
    }

    object PastStaffReportProfile : Screen(route = "past_staff_report_screen/{$NAVKEY}"){
        fun pastStaffReportProfileId(key: String): String{
            return "past_staff_report_screen/$key"
        }
    }

    object PastStaffAttendanceReport : Screen("past_staff_attendance_report_screen/{$NAVKEY}/{$NAVKEY_TWO}"){
        fun pastStaffAttendanceReport(key: String, key2: String): String{
            return "past_staff_attendance_report_screen/$key/$key2"
        }
    }

    object PastStaffAttendanceReportMonth : Screen("past_staff_attendance_report_month_screen/{$NAVKEY}/{$NAVKEY_TWO}"){
        fun pastStaffAttendanceReportMonthId(key: String, key2: String): String{
            return "past_staff_attendance_report_month_screen/$key/$key2"
        }
    }

    object AbsentStaffRecordList: Screen(route = "absent_staff_record_screen")

    object AbsentStaffRecord : Screen(route = "past_staff_report_each_screen/{$NAVKEY}/{$NAVKEY_TWO}"){
        fun absentStaffRecordId(key: String, key2: String): String{
            return "past_staff_report_each_screen/$key/$key2"
        }
    }

    object MovementMainScreen: Screen(route = "movement_main_screen")

    object ActivateStaffScreen: Screen(route = "activate_staff_screen")

    object MovementLogScreen: Screen(route = "movement_log_screen")

    object StaffMovementScanIdScreen: Screen(route = "staff_movement_scan_log_screen")

    object StaffListMovementScreen: Screen(route = "staff_list_movement_screen")

    object StaffMovementHistoryScreen: Screen(route = "staff_movement_history_screen{$NAVKEY}/{$NAVKEY_TWO}"){
        fun staffMovementHistoryScreenId(key: String, key2: String): String{
            return "staff_movement_history_screen/$key/$key2"
        }
    }



}