package com.stevdza.san.mongodemo.util

object Constants {
    const val APP_ID = "pure-knowledge-demo-idwyu"
    const val CLIENT_ID = "252146713683-9nvbdjfi207v1e982bnsc7hbvusvvai7.apps.googleusercontent.com"

    const val ADMIN = "Admin"
    const val ATTENDANCE_COLLECTED = "Attendance Collector"
    const val DIRECTOR = "Director"
    const val DAILY_REPORT = "Daily Report"
    const val CLASS = "Class"
    const val SESSION = "Session"
    const val ONBOARDED_STUDENT = "onboarded_student"
    const val ATTENDANCE_BY_ADMIN = "manual_attendance"

    const val FAMILY = "family"
    const val STUDENT_EDIT = "student_edit"
    const val PARENT_ASSOCIATE_EDIT = "parent_associate_edit"
    const val PARENT_EDIT = "parent_edit"

    // Navigation Constant
    const val ROOT_GRAPH_ROUTE = "root"
    const val AUTH_GRAPH_ROUTE = "auth"
    const val DASHBOARD_GRAPH_ROUTE = "dashboard"
    const val ATTENDANCE_GRAPH_ROUTE = "attendance"
    const val STAFF_ATTENDANCE_GRAPH_ROUTE = "staff attendance"
    const val PARENT_VIEW_DASHBOARD_GRAPH_ROUTE = "parent_view"

    // Term Constant
    const val FIRST_TERM = "First Term"
    const val SECOND_TERM = "Second Term"
    const val THIRD_TERM = "Third Term"

    // Attendance In and Out
    const val IN = "in"
    const val OUT = "Out"



}


enum class Gender {
    Male,
    Female
}
val gender = listOf<String>("Male", "Female")