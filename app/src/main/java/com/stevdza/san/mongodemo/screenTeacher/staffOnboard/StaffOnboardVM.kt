package com.stevdza.san.mongodemo.screenTeacher.staffOnboard

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.StaffAttendance
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonBinary
import org.mongodb.kbson.BsonObjectId

class StaffOnboardVM () : ViewModel() {

    var teacherData = mutableStateOf(emptyList<TeacherProfile>())
    var surname = mutableStateOf("")
    var objectId = mutableStateOf("")
    var teacher = mutableStateOf<TeacherProfile?>(null)



    var objectIdEditStaffProfile = mutableStateOf("")
    var staffId = mutableStateOf("")
    var surnameEdit= mutableStateOf("")
    var otherNames= mutableStateOf("")
    var position= mutableStateOf("")
    var address= mutableStateOf("")
    var phone= mutableStateOf("")
    var gender = mutableStateOf("")
    var dateOfBirth = mutableStateOf("")
    var pics = mutableStateOf("")
    var dateCreated = mutableStateOf("")
    var relationshipStatus = mutableStateOf("")
    var bitmap = mutableStateOf<Bitmap?>(null)


    init {
        getAllTeacher()
    }

    var objectIdEdit = mutableStateOf("")
    var staffStatus = mutableStateOf("")
    var date = mutableStateOf("")

    fun updateStudentStatus(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectIdEdit.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateTeacherStatus(teacherProfile = TeacherProfile().apply {
                            _id = BsonObjectId(hexString = objectIdEdit.value)
                            teacherStatus = StudentStatus().apply {
                                status = this@StaffOnboardVM.staffStatus.value
                                date = this@StaffOnboardVM.date.value
                            }
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


     fun getAllTeacher() {
        viewModelScope.launch {
            AlkhairDB.getTeacherData().collect {
                teacherData.value = it
            }
        }
    }


    fun getParentWithName() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getTeacherWithSurname(surname =surname.value ).collect {
                    teacherData.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting Member with Id: $e",)
            }
        }
    }

    fun getParentWithID() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AlkhairDB.getTeacherWithID(_id =BsonObjectId(hexString = objectId.value)).let {
                    teacher.value = it
                }
            } catch (e: RealmException) {
                Log.e(ContentValues.TAG, "error Getting Member with Id: $e",)
            }
        }
    }


    fun updateTeacherProfile(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            pics.value = bitmap.value?.let { bitmapToBase64(it) }.toString()

            if ( objectIdEditStaffProfile.value.isNotEmpty()) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.updateTeacher(teacherProfile = TeacherProfile().apply {
                            _id = BsonObjectId(hexString = objectIdEditStaffProfile.value)
                            surname = this@StaffOnboardVM.surnameEdit.value
                            otherNames = this@StaffOnboardVM.otherNames.value
                            gender = this@StaffOnboardVM.gender.value
                            address = this@StaffOnboardVM.address.value
                            dateOfBirth = this@StaffOnboardVM.dateOfBirth.value
                            dateCreated = this@StaffOnboardVM.dateCreated.value
                            staffId = this@StaffOnboardVM.staffId.value
                            phone = this@StaffOnboardVM.phone.value
                            position = this@StaffOnboardVM.position.value
                            relationshipStatus = this@StaffOnboardVM.relationshipStatus.value
                            pics = this@StaffOnboardVM.pics.value

                        })

                        return@withContext true
                    }
                    withContext(Dispatchers.Main) {
                        if (result) {
                            onSuccess()
                        }
                    }

                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "update teacher Profile: ${e.message}",)
                    onError()
                }

            }
        }
    }

    fun byteArrayToBitmap(binData: BsonBinary): Bitmap? {
        // Get the byte array from the BinData
        val byteArray = binData.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

}