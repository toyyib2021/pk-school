package com.stevdza.san.mongodemo.screenStudent.onboardedStudent

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.ClassArms
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentSelectable
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId


class OnBoardedStudentVM(): ViewModel() {

    var studentClass = mutableStateOf(emptyList<ClassName>())
    var studentArm = mutableStateOf(emptyList<ClassArms>())
    var studentData = mutableStateOf(emptyList<StudentData>())
    var studentData2 = mutableStateOf(emptyList<StudentData>())
    var stu = mutableListOf<StudentSelectable>()

    var objectId = mutableStateOf("")
    var familyId = mutableStateOf("")
    var cless = mutableStateOf("")
    var surname = mutableStateOf("")

    init {
        viewModelScope.launch {
            AlkhairDB.getStudentData().collect {
                studentData.value = it
            }
        }
        viewModelScope.launch {
            AlkhairDB.getClassNames().collect {
                studentClass.value = it
            }
        }
        viewModelScope.launch {
            AlkhairDB.getClassArms().collect {
                studentArm.value = it
            }
        }
    }


    fun getStudentWithSurname(){
        viewModelScope.launch {
            AlkhairDB.getStudentWithSurname(surname = surname.value).collect {
                studentData2.value = it
            }
        }
    }

    fun upDateStudentFamilyId(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectId.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateStudentFamilyId(student = StudentData().apply {
                            _id = BsonObjectId(hexString = objectId.value)
                            familyId = this@OnBoardedStudentVM.familyId.value
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


    fun deleteStudent(
        onSuccess:()->Unit,
        onError:()->Unit,
        emptyFeild:()->Unit,
    ) {
        viewModelScope.launch {
            if (objectId.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        viewModelScope.launch {
                            AlkhairDB.deleteStudent(id = BsonObjectId(hexString = objectId.value))
                        }
                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "updateClassNames: ${e.message}",)
                    onError()
                }

            }else{
                emptyFeild()
            }
        }

    }

    fun upDateClass(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

                try {
                    val result = withContext(Dispatchers.IO) {
                        val stuOnlyTrue = stu.filter { it.selected.value }
                        stuOnlyTrue.forEach {
                            if (
                                it.student._id.toHexString().isNotEmpty()
                            ) {
                                AlkhairDB.updateStudentClass(student = StudentData().apply {
                                    _id = BsonObjectId(hexString = it.student._id.toHexString())
                                    cless = this@OnBoardedStudentVM.cless.value

                                })
                            }

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