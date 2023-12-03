package com.stevdza.san.mongodemo.screenTeacher.movement

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.Movement
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.TeacherAbsentAttendance
import com.stevdza.san.mongodemo.model.TeacherProfile
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class MovementVM(): ViewModel() {
    val searchBarText = mutableStateOf("")
    val staffId = mutableStateOf("")
    val reason = mutableStateOf("")
    val date = mutableStateOf("")
    val timeIn = mutableStateOf("")
    val timeOut = mutableStateOf("")
    val objectId = mutableStateOf("")

    var teacherData = mutableStateOf(emptyList<TeacherProfile>())
    var staffMovementData = mutableStateOf(emptyList<Movement>())


    init {
        viewModelScope.launch {
            AlkhairDB.getTeacherData().collect {
                teacherData.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.staffMovementData().collect {
                staffMovementData.value = it
            }
        }
    }


    fun insert(){
        viewModelScope.launch {
            AlkhairDB.insertStaffMovement(movement = Movement().apply {
                staffId =this@MovementVM.staffId.value
                reason =this@MovementVM.reason.value
                date = this@MovementVM.date.value
                timeIn =this@MovementVM.timeIn.value
                timeOut = this@MovementVM.timeOut.value
            })
        }

    }


    fun insertStaffMovement(
        onError:()-> Unit,
        onSuccess:()-> Unit,
        emptyFeilds:()-> Unit,
    ) {
        viewModelScope.launch {
            if(
                staffId.value.isNotEmpty() && reason.value.isNotEmpty() && date.value.isNotEmpty()
                && timeIn.value.isNotEmpty()
            ){
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertStaffMovement(movement = Movement().apply {
                            staffId =this@MovementVM.staffId.value
                            reason =this@MovementVM.reason.value
                            date = this@MovementVM.date.value
                            timeIn =this@MovementVM.timeIn.value
                            timeOut = this@MovementVM.timeOut.value
                        })
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "insertStudent: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeilds()
            }
        }
    }


    fun updateStaffMovement(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectId.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateStaffMovement(movement = Movement().apply {
                            _id = BsonObjectId(hexString = objectId.value)
                            timeOut = this@MovementVM.timeOut.value
                        })
                    }
                    return@withContext true
                }
                withContext(Dispatchers.Main) {
                    if (result) {
                        onSuccess()
                    }
                }

            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "insertAdmin: ${e.message}",)
                onError()
            }


        }
    }




}