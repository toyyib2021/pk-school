package com.stevdza.san.mongodemo.screenStudent.studentOnboarding

import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.ClassArms
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentOnboardingVM (): ViewModel() {

    var objectId = mutableStateOf("")
    var surname = mutableStateOf("")
    var otherNames = mutableStateOf("")
    var cless = mutableStateOf("Select")
    var arms = mutableStateOf("Select")
    var dateOfBirth = mutableStateOf("")
    var gender = mutableStateOf("")
    var dateCreated = mutableStateOf("")
    var statusDate = mutableStateOf("")
    var studentApplicationID = mutableStateOf("")
    var familyId = mutableStateOf("")

    var bitmap = mutableStateOf<Bitmap?>(null)
    var studentStatus = mutableStateOf(StudentStatusType.ACTIVE.status)
    var pics = mutableStateOf("")

    var classNames = mutableStateOf(emptyList<ClassName>())
    var armsNames = mutableStateOf(emptyList<ClassArms>())


    init {
        viewModelScope.launch {
            AlkhairDB.getClassNames().collect {
                classNames.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.getClassArms().collect {
                armsNames.value = it
            }
        }
    }


    fun insertStudent(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            pics.value = bitmap.value?.let { bitmapToBase64(it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                cless.value != "Select"  && familyId.value.isNotEmpty()
                && bitmap.value != null && pics.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AlkhairDB.insertStudent(student = StudentData().apply {
                            surname = this@StudentOnboardingVM.surname.value
                            otherNames = this@StudentOnboardingVM.otherNames.value
                            cless= this@StudentOnboardingVM.cless.value
                            gender = this@StudentOnboardingVM.gender.value
                            familyId = this@StudentOnboardingVM.familyId.value
                            datOfBirth = this@StudentOnboardingVM.dateOfBirth.value
                            arms = this@StudentOnboardingVM.arms.value
                            dateCreated = this@StudentOnboardingVM.dateCreated.value
                            studentApplicationID = this@StudentOnboardingVM.studentApplicationID.value
                            pics = this@StudentOnboardingVM.pics.value
                            studentStatus = StudentStatus().apply {
                                status = this@StudentOnboardingVM.studentStatus.value
                                date = this@StudentOnboardingVM.statusDate.value
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