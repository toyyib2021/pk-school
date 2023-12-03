package com.stevdza.san.mongodemo.model

import androidx.compose.runtime.MutableState

data class StudentSelectable(
    val student: StudentData,
    var selected: MutableState<Boolean>,
){
    fun  toggle(
        addSelectedStudent:(StudentData)->Unit,
        removeSelectedStudent:(StudentData)->Unit
    ){
        selected.value =!selected.value
        if (selected.value){
            addSelectedStudent(student)
        }else{
            removeSelectedStudent(student)
        }
    }
    fun selectedFalse(){
        selected.value = false
    }
}
