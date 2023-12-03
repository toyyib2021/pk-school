package com.stevdza.san.mongodemo.model

data class StaffAttendancePdf(
    val schoolNme: String,
    val title: String,
    val date: String,
    val attendance: List<StaffAttendance>,
    val presentDays: String,
    val absentDays: String,
    val signatureUrl: String?,
    val hourIn: Int,
    val minIn: Int,
    val hourOut: Int,
    val minOut: Int,
    val presentDaysPercent: String,
    val absentDaysPercent: String,
)



data class StudentAttendancePdf(
    val schoolNme: String,
    val title: String,
    val date: String,
    val attendance: List<AttendanceStudent>,
    val presentDays: String,
    val absentDays: String,
    val signatureUrl: String?,
    val hourIn: Int,
    val minIn: Int,
    val hourOut: Int,
    val minOut: Int,
    val presentDaysPercent: String,
    val absentDaysPercent: String,
)