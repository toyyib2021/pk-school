package com.stevdza.san.mongodemo.screenStudent.pinLogin

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.Access
import com.stevdza.san.mongodemo.util.Constants.ADMIN
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_COLLECTED
import com.stevdza.san.mongodemo.util.Constants.DIRECTOR
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class PinVM (): ViewModel() {

    var objectId = mutableStateOf("")
    val account = mutableStateOf("")
    val passcode = mutableStateOf("")
    val newPassword = mutableStateOf("")
    val changePasswordState = mutableStateOf(false)

    val adminPass = mutableStateOf("123456")
    val attendanceCollectorPass = mutableStateOf("123456")
    val schoolOwnerPass = mutableStateOf("123456")

    val adminDB = mutableStateOf("")
    val attendanceCollectorDB = mutableStateOf("")
    val schoolOwnerDB = mutableStateOf("")

    var data = mutableStateOf(emptyList<Access>())
    var accessPass = mutableStateOf<Access?>(null)

    init {
        getAccessDB()
//        insertAccessDB()
    }

     fun getAccessDB() {
        viewModelScope.launch {
            AlkhairDB.getAccessData().collect {
                data.value = it
            }
        }
    }

    fun confirmAdmin(
        passcodeNotCorrect:()-> Unit,
        passwordFieldEmpty:()-> Unit,
        navToHomeScreen:()-> Unit,
        updateAccess:()-> Unit,
        passcodeMoreThanSix:()-> Unit,
    ){
        when(account.value){
            ADMIN ->{
                if (changePasswordState.value){
                    if (passcode.value.isNotEmpty() &&
                        newPassword.value.isNotEmpty()){
                        if (newPassword.value.length <= 6){
                            // check if user put correct admin passcode
                            if (passcode.value == adminDB.value){
                                adminPass.value = newPassword.value
                                schoolOwnerPass.value =schoolOwnerDB.value
                                attendanceCollectorPass.value = attendanceCollectorDB.value
                                updateAccess()
                            }else{
                                passcodeNotCorrect()
                            }
                        }else{
                            passcodeMoreThanSix()
                        }

                    }else{
                        passwordFieldEmpty()
                    }

                }
                else{
                    if (passcode.value.isNotEmpty()){
                        // check if user put correct admin passcode
                        if (passcode.value == adminDB.value){
                            navToHomeScreen()
                        }else{
                            passcodeNotCorrect()
                        }
                    }else{
                        passwordFieldEmpty()
                    }
                }
            }
            DIRECTOR ->{
                if (changePasswordState.value){
                    if (passcode.value.isNotEmpty() &&
                        newPassword.value.isNotEmpty()){
                        if (newPassword.value.length <= 6){
                            // check if user put correct admin passcode
                            if (passcode.value == schoolOwnerDB.value){
                                adminPass.value = adminDB.value
                                schoolOwnerPass.value =newPassword.value
                                attendanceCollectorPass.value = attendanceCollectorDB.value
                                updateAccess()
                            }else{
                                passcodeNotCorrect()
                            }
                        }else{
                            passcodeMoreThanSix()
                        }

                    }else{
                        passwordFieldEmpty()
                    }

                }
                else{
                    if (passcode.value.isNotEmpty()){
                        // check if user put correct admin passcode
                        if (passcode.value == schoolOwnerDB.value){
                            navToHomeScreen()
                        }else{
                            passcodeNotCorrect()
                        }
                    }else{
                        passwordFieldEmpty()
                    }
                }
            }
            ATTENDANCE_COLLECTED ->{
                if (changePasswordState.value){
                    if (passcode.value.isNotEmpty() &&
                        newPassword.value.isNotEmpty()){
                        if (newPassword.value.length <= 6){
                            // check if user put correct admin passcode
                            if (passcode.value == attendanceCollectorDB.value){
                                adminPass.value = adminDB.value
                                schoolOwnerPass.value =schoolOwnerDB.value
                                attendanceCollectorPass.value = newPassword.value
                                updateAccess()
                            }else{
                                passcodeNotCorrect()
                            }
                        }else{
                            passcodeMoreThanSix()
                        }

                    }else{
                        passwordFieldEmpty()
                    }

                }
                else{
                    if (passcode.value.isNotEmpty()){
                        // check if user put correct admin passcode
                        if (passcode.value == attendanceCollectorDB.value){
                            navToHomeScreen()
                        }else{
                            passcodeNotCorrect()
                        }
                    }else{
                        passwordFieldEmpty()
                    }
                }
            }
        }
    }



    fun updateAccessPass(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            if (
                objectId.value.isNotEmpty() && adminPass.value.isNotEmpty()
                && schoolOwnerPass.value.isNotEmpty() && attendanceCollectorPass.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateAccess(access = Access().apply {
                            _id = BsonObjectId(hexString = this@PinVM.objectId.value)
                            admin = this@PinVM.adminPass.value
                            schoolOwner = this@PinVM.schoolOwnerPass.value
                            attendanceCollected = this@PinVM.attendanceCollectorPass.value
                        })
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



    fun getAccessPassWIthID() {
        viewModelScope.launch(Dispatchers.IO) {
            if (objectId.value.isNotEmpty()) {
                try {
                    AlkhairDB.getAccessWithID(_id = BsonObjectId(hexString = objectId.value))
                        .let {
                            accessPass.value = it
                        }
                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
                }
            }

        }
    }

}