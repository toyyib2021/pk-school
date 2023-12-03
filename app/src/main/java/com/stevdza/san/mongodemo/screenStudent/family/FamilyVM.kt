package com.stevdza.san.mongodemo.screenStudent.family

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.data.AlkhairDB
import com.stevdza.san.mongodemo.model.AssociateParent
import com.stevdza.san.mongodemo.model.ClassArms
import com.stevdza.san.mongodemo.model.ClassName
import com.stevdza.san.mongodemo.model.Parent
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatus
import com.stevdza.san.mongodemo.util.conversion.bitmapToBase64
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonBinary
import org.mongodb.kbson.BsonBinarySubType
import org.mongodb.kbson.BsonObjectId
import java.io.ByteArrayOutputStream

class FamilyVM (): ViewModel() {


    val objectID = mutableStateOf("")
    var pics = mutableStateOf<BsonBinary?>(null)
    var bitmap = mutableStateOf<Bitmap?>(null)

    // Update Associate Parent, Parent And Student
    var objectIdEdit = mutableStateOf("")
    var surname = mutableStateOf("")
    var otherNames = mutableStateOf("")
    var phone = mutableStateOf("")
    var relationship = mutableStateOf("")
    var address = mutableStateOf("")
    var gender = mutableStateOf("")
    var familyId = mutableStateOf("")
    var bitmapEdit = mutableStateOf<Bitmap?>(null)
    var picsEdit = mutableStateOf("")




    var cless = mutableStateOf("Select")
    var arms = mutableStateOf("Select")
    var dateOfBirth = mutableStateOf("")
    var studentApplicationID = mutableStateOf("")
    var date = mutableStateOf("")
    var studentStatus = mutableStateOf("")



    var childrenData = mutableStateOf(emptyList<StudentData>())
    var childrenDataTwo = mutableStateOf(emptyList<StudentData>())
    var nameList = mutableStateOf(emptyList<String>())
    var associateParentData = mutableStateOf(emptyList<AssociateParent>())
    var parentData = mutableStateOf(emptyList<Parent>())
    var oneParentData = mutableStateOf<Parent?>(null)
    var studentData = mutableStateOf<StudentData?>(null)


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

        viewModelScope.launch {
            AlkhairDB.getParentData().collect {
                parentData.value = it
            }
        }
        viewModelScope.launch {
            AlkhairDB.getAssociateParentData().collect {
                associateParentData.value = it
            }
        }
        viewModelScope.launch {
            AlkhairDB.getStudentData().collect {
                childrenData.value = it
            }
        }

        viewModelScope.launch {
            AlkhairDB.getStudentData().collect {
                childrenDataTwo.value = it
            }
        }

    }


    fun updateStudent(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            picsEdit.value = bitmapEdit.value?.let { bitmapToBase64(it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                cless.value != "Select" &&  familyId.value.isNotEmpty() &&
                bitmapEdit.value != null && objectIdEdit.value.isNotEmpty() && dateOfBirth.value.isNotEmpty()
                && studentApplicationID.value.isNotEmpty()
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {

                        AlkhairDB.updateStudent(student = StudentData().apply {
                            _id = BsonObjectId(hexString = this@FamilyVM.objectIdEdit.value)
                            surname = this@FamilyVM.surname.value
                            otherNames = this@FamilyVM.otherNames.value
                            cless= this@FamilyVM.cless.value
                            gender = this@FamilyVM.gender.value
                            familyId = this@FamilyVM.familyId.value
                            datOfBirth = this@FamilyVM.dateOfBirth.value
                            arms = this@FamilyVM.arms.value
                            studentApplicationID = this@FamilyVM.studentApplicationID.value
                            pics = this@FamilyVM.picsEdit.value
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



    fun upDateParentAssociate(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            picsEdit.value = bitmapEdit.value?.let { bitmapToBase64(it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                phone.value.isNotEmpty() && relationship.value.isNotEmpty() &&
                address.value.isNotEmpty() && familyId.value.isNotEmpty()
                && gender.value.isNotEmpty() && objectID.value.isNotEmpty() &&
                        bitmapEdit.value != null
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {

                        AlkhairDB.updateAssociateParent(associateParent = AssociateParent().apply {
                            _id = BsonObjectId(hexString = this@FamilyVM.objectIdEdit.value)
                            surname = this@FamilyVM.surname.value
                            otherNames = this@FamilyVM.otherNames.value
                            phone = this@FamilyVM.phone.value
                            relationship = this@FamilyVM.relationship.value
                            gender = this@FamilyVM.gender.value
                            address = this@FamilyVM.address.value
                            familyId = this@FamilyVM.familyId.value
                            pics = this@FamilyVM.picsEdit.value
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


    fun upDateParent(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {
            picsEdit.value = bitmapEdit.value?.let { bitmapToBase64(it) }.toString()
            if (
                surname.value.isNotEmpty() && otherNames.value.isNotEmpty() &&
                phone.value.isNotEmpty() && relationship.value.isNotEmpty() &&
                address.value.isNotEmpty() && gender.value.isNotEmpty()
                && objectID.value.isNotEmpty() && bitmapEdit.value != null
            ) {
                try {
                    val result = withContext(Dispatchers.IO) {

                        AlkhairDB.updateParent(parent = Parent().apply {
                            _id = BsonObjectId(hexString = this@FamilyVM.objectIdEdit.value)
                            surname = this@FamilyVM.surname.value
                            otherNames = this@FamilyVM.otherNames.value
                            phone = this@FamilyVM.phone.value
                            relationship = this@FamilyVM.relationship.value
                            gender = this@FamilyVM.gender.value
                            address = this@FamilyVM.address.value
                            pics = this@FamilyVM.picsEdit.value
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

    fun upDateStudentStatus(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectIdEdit.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateStudentStatus(student = StudentData().apply {
                            _id = BsonObjectId(hexString = objectIdEdit.value)
                            studentStatus = StudentStatus().apply {
                                status = this@FamilyVM.studentStatus.value
                                date = this@FamilyVM.date.value
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


    fun upDateStudentFamilyId(
        onError:()-> Unit,
        onSuccess:()-> Unit,
    ) {
        viewModelScope.launch {

            try {
                val result = withContext(Dispatchers.IO) {
                    if (objectIdEdit.value.isNotEmpty()
                    ) {
                        AlkhairDB.updateStudentFamilyId(student = StudentData().apply {
                            _id = BsonObjectId(hexString = objectIdEdit.value)
                            familyId = this@FamilyVM.familyId.value
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

    fun bitmapToByteArrayEdit(): BsonBinary {
        // Convert the Bitmap to a byte array
        val outputStream = ByteArrayOutputStream()
        bitmapEdit.value?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        // Create a BsonBinary instance with subtype 0x00 (generic binary data)
        return BsonBinary(BsonBinarySubType.BINARY, byteArray)
    }

    fun bitmapToByteArray(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.value?.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
        val byteArrayMain = byteArrayOutputStream.toByteArray()
        return byteArrayMain
    }




    fun getChildrenWIthFamilyID() {
        viewModelScope.launch(Dispatchers.IO) {
            if (objectID.value.isNotEmpty()) {
                try {
                    AlkhairDB.getStudentWithFamilyID(familyId = objectID.value)
                        .collect() {
                            childrenData.value = it
                        }
                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
                }
            }

        }
    }

    fun getAssociateParentWithFamilyID() {
        viewModelScope.launch(Dispatchers.IO) {
            if (objectID.value.isNotEmpty()) {
                try {
                    AlkhairDB.getAssociateParentWithFamilyId(familyId = objectID.value)
                        .collect() {
                            associateParentData.value = it
                        }
                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
                }
            }

        }
    }

    fun getParentWithID() {
        viewModelScope.launch(Dispatchers.IO) {
            if (objectID.value.isNotEmpty()) {
                try {
                    AlkhairDB.getParentWithID(_id = BsonObjectId(hexString = objectID.value))
                        .let {
                            oneParentData.value = it
                        }
                } catch (e: RealmException) {
                    Log.e(ContentValues.TAG, "error Getting access Pass with Id: $e",)
                }
            }

        }
    }



    fun byteArrayToBitmap(binData: BsonBinary): Bitmap? {
        val byteArray = binData.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    fun byteArrayToBitmapEdit(binData: BsonBinary): Bitmap? {
        val byteArray = binData.data
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
//    fun resizePhoto(bitmap: Bitmap):Bitmap{
//        val w = bitmap.width
//        val h = bitmap.height
//        val aspRatio = w/h
//        val W = 300
//        val H = w* aspRatio
//        val b = Bitmap.createScaledBitmap(bitmap,W, H, false)
//        return b
//    }
}

fun convertBitmapToBinData(bitmap: Bitmap): BsonBinary {
    // Convert the Bitmap to a byte array
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val byteArray = outputStream.toByteArray()

    // Create a BsonBinary instance with subtype 0x00 (generic binary data)
    return BsonBinary(BsonBinarySubType.BINARY, byteArray)
}


fun convertBinDataToBitmap(binData: BsonBinary): Bitmap {
    // Get the byte array from the BinData
    val byteArray = binData.data

    // Decode the byte array into a Bitmap
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}