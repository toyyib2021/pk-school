package com.stevdza.san.mongodemo.screenTeacher.onboardStaff

import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.ClassArms
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.StaffStatusType
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.model.TeacherProfile
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StaffOnboardingVM (): ViewModel() {

    var objectId = mutableStateOf("")
    var surname = mutableStateOf("")
    var otherNames = mutableStateOf("")
    var dateOfBirth = mutableStateOf("")
    var gender = mutableStateOf("")

    var staffApplicationID = mutableStateOf("")
    var post = mutableStateOf("")
    var relationshipStatus = mutableStateOf("")
    var phone = mutableStateOf("")
    var address = mutableStateOf("")


    var dateCreated = mutableStateOf("")
    var statusDate = mutableStateOf("")


    var bitmap = mutableStateOf<Bitmap?>(null)
    var studentStatus = mutableStateOf(StaffStatusType.ACTIVE.status)
    var pics = mutableStateOf("")







    fun insertStudent(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            pics.value = bitmap.value?.let { bitmapToBase64(it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                 staffApplicationID.value.isNotEmpty() && post.value.isNotEmpty()
                && bitmap.value != null && pics.value.isNotEmpty() && address.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertTeacher(teacherProfile = TeacherProfile().apply {
                            surname = this@StaffOnboardingVM.surname.value
                            otherNames = this@StaffOnboardingVM.otherNames.value
                            gender = this@StaffOnboardingVM.gender.value
                            address = this@StaffOnboardingVM.address.value
                            dateOfBirth = this@StaffOnboardingVM.dateOfBirth.value
                            dateCreated = this@StaffOnboardingVM.dateCreated.value
                            staffId = this@StaffOnboardingVM.staffApplicationID.value
                            phone = this@StaffOnboardingVM.phone.value
                            position = this@StaffOnboardingVM.post.value
                            relationshipStatus = this@StaffOnboardingVM.relationshipStatus.value
                            pics = this@StaffOnboardingVM.pics.value
                            teacherStatus = StudentStatus().apply {
                                status = this@StaffOnboardingVM.studentStatus.value
                                date = this@StaffOnboardingVM.statusDate.value
                            }
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

}