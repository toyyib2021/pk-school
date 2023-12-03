package com.stevdza.san.mongodemo.screenStudent.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginVM (): ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")


    fun loginBtn(){
        if (email.value.isNotEmpty() && password.value.isNotEmpty()){

        }
    }

}