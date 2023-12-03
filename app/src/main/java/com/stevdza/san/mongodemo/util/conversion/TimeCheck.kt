package com.stevdza.san.mongodemo.util.conversion

import java.time.LocalTime

fun isPastSignInTime(inputTime: String, hour: Int, min: Int): Boolean {
    try {
        // Parse the input time
        val parsedTime = LocalTime.parse(inputTime)

        // Define the target time (4:00 PM)
        val targetTime = LocalTime.of(hour, min)

        // Compare the parsed time with 4:00 PM
        return parsedTime.isAfter(targetTime)
    } catch (e: Exception) {
        // Handle parsing exceptions if needed
        e.printStackTrace()
        return false
    }
}