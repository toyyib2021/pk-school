package com.stevdza.san.mongodemo.screenTeacher.staffProfile

import android.content.ContentValues.TAG
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StaffStatusType
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenTeacher.onboardStaff.OnboardStaffScreen
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.StaffOnboardVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.ui.theme.CreamTwo
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import com.stevdza.san.mongodemo.util.gender
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StaffProfile(
    id:String, onBackClick:()->Unit
){
    val staffOnboardVM: StaffOnboardVM = viewModel()
    val data = staffOnboardVM.teacherData.value
    val teacherProfile = data.find { it._id.toHexString() == id }
    var editState by remember { mutableStateOf(false) }

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    Log.e(TAG, "StaffProfile: id-> $id", )
    Log.e(TAG, "StaffProfile: id-> ${teacherProfile?.surname}", )
    Log.e(TAG, "StaffProfile: id-> ${teacherProfile?.position}", )
    Log.e(TAG, "StaffProfile: id-> ${teacherProfile?.phone}", )

    LaunchedEffect(key1 =  staffOnboardVM.staffStatus.value){
        staffOnboardVM.updateStudentStatus(
            onError = { Log.e(TAG, "Family: error updating student Status",) },
            onSuccess = {
                Log.e(TAG, "Family: success updating student Status",)
            }
        )
    }

    BackHandler {
        if (editState){
            editState = false
        }else{
            onBackClick()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent ={
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            staffOnboardVM.staffStatus.value = StaffStatusType.ACTIVE.status
                            scope.launch {
                                sheetState.collapse()
                            }
                        },
                    text = StaffStatusType.ACTIVE.status,
                    fontSize = 14.sp)
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            staffOnboardVM.staffStatus.value = StaffStatusType.RESIGNED.status
                            scope.launch {
                                sheetState.collapse()
                            }
                        },
                    text = StaffStatusType.RESIGNED.status,
                    fontSize = 14.sp)
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            staffOnboardVM.staffStatus.value = StaffStatusType.SACKED.status
                            scope.launch {
                                sheetState.collapse()
                            }
                        },
                    text = StaffStatusType.SACKED.status,
                    fontSize = 14.sp)
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
            }

        },
        content = {
            if (editState){
                    val launcher =
                        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                            staffOnboardVM.bitmap.value = it
                        }

                    var imageUri by remember {
                        mutableStateOf<Uri?>(null)
                    }
                    val context = LocalContext.current


                    val launcherForGallery = rememberLauncherForActivityResult(
                        contract =
                        ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        imageUri = uri
                    }

                    OnboardStaffScreen(
                        surname = staffOnboardVM.surnameEdit.value,
                        surnameLabel = "Surname",
                        onSurnameChange = {staffOnboardVM.surnameEdit.value=it},
                        otherNames = staffOnboardVM.otherNames.value,
                        otherNamesLabel = "Other Names",
                        onOtherNamesChange = { staffOnboardVM.otherNames.value=it},
                        staffId =  staffOnboardVM.staffId.value,
                        staffIdLabel = "Student Application Id",
                        onStaffIdChange = { staffOnboardVM.staffId.value=it},
                        onBackClick = {editState  = false },
                        onPreviewClick = {
                            Log.e(TAG, "surname: ${staffOnboardVM.surnameEdit.value}", )
                            Log.e(TAG, "otherNames: ${ staffOnboardVM.otherNames.value}", )
                            Log.e(TAG, "dateOfBirth: ${ staffOnboardVM.dateOfBirth.value}", )
                            Log.e(TAG, "gender: ${ staffOnboardVM.gender.value}", )
                            Log.e(TAG, "staffApplicationID: ${ staffOnboardVM.staffId.value}", )
                            Log.e(TAG, "phone: ${staffOnboardVM.phone.value}", )
                            Log.e(TAG, "bitmap: ${staffOnboardVM.bitmap.value}", )
                            Log.e(TAG, "address: ${ staffOnboardVM.address.value}", )
                            staffOnboardVM.updateTeacherProfile(
                                onError = {

                                },
                                onSuccess = {
                                    staffOnboardVM.bitmap.value = null
                                    editState = false
                                },
                            )
                        },
                        onGetImageFromCamClick = {
                            imageUri = null
                            launcher.launch()
                        },
                        onGetImageFromGalleryClick = {  launcherForGallery.launch("image/*") },
                        imageFromGallery = {
                            if (imageUri == null){
                                staffOnboardVM.bitmap.value.let {
                                    if (it != null) {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )
                                    }else{
                                        val bit =  staffOnboardVM.pics.value.let { it1 -> convertBase64ToBitmap(it1) }

//                                        if (bit != null) {
                                        staffOnboardVM.bitmap.value = bit
                                        Image(
                                            bitmap = bit.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )
//                                        }
                                    }
                                }
                            }
                            else{
                                imageUri?.let {
                                    if (Build.VERSION.SDK_INT < 28) {
                                        staffOnboardVM.bitmap.value = MediaStore.Images
                                            .Media.getBitmap(context.contentResolver, it)

                                    } else {
                                        val source = ImageDecoder
                                            .createSource(context.contentResolver, it)
                                        staffOnboardVM.bitmap.value = ImageDecoder.decodeBitmap(source)
                                    }

                                    staffOnboardVM.bitmap.value?.let { btm ->
                                        Image(
                                            bitmap = btm.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )


                                    }
                                }
                            }
                        },

                        onGenderClick={ staffOnboardVM.gender.value=it},
                        genderType= staffOnboardVM.gender.value,
                        genders= gender,
                        onDateOfBirthChange = { staffOnboardVM.dateOfBirth.value=it},
                        dateOfBirth = staffOnboardVM.dateOfBirth.value,
                        dateOfBirthLabel = "Date of Birth (YYYY-MM-DD)",
                        bitmap = staffOnboardVM.bitmap.value,
                        context = context,
                        phone =  staffOnboardVM.phone.value,
                        phoneLabel = "Phone",
                        onPhoneChange = { staffOnboardVM.phone.value=it},
                        post =  staffOnboardVM.position.value,
                        postLabel = "Position",
                        onPostLabelChange = { staffOnboardVM.position.value=it},
                        relationship =  staffOnboardVM.relationshipStatus.value,
                        relationshipLabel = "Relationship Status",
                        onRelationshipChange = { staffOnboardVM.relationshipStatus.value=it},
                        contactAddress =  staffOnboardVM.address.value,
                        contactAddressLabel = "Contact Address",
                        onContactAddressChange = { staffOnboardVM.address.value=it},
                    )

            }else{
                teacherProfile?.let {
                    val imageBitmap = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
                    StaffProfileScreen(
                        onBackClick = { onBackClick() },
                        bitmap = imageBitmap.asImageBitmap(),
                        name = "${it.surname} ${it.otherNames}",
                        id = it.staffId,
                        phone = it.phone,
                        work = it.position,
                        gender = it.gender,
                        dateOfBirth = it.dateOfBirth,
                        relationship = it.relationshipStatus,
                        address =it.address,
                        status = it.teacherStatus?.status ?: "",
                        onStatusClick ={
                            staffOnboardVM.objectIdEdit.value = it._id.toHexString()
                            scope.launch {
                                if (sheetState.isCollapsed) {
                                    sheetState.expand()
                                } else {
                                    sheetState.collapse()
                                }
                            }
                        },
                        onEditProfileClick = {
                            staffOnboardVM.objectIdEditStaffProfile.value =  it._id.toHexString()
                            staffOnboardVM.surnameEdit.value = it.surname
                            staffOnboardVM.otherNames.value = it.otherNames
                            staffOnboardVM.staffId.value = it.staffId
                            staffOnboardVM.phone.value = it.phone
                            staffOnboardVM.position.value = it.position
                            staffOnboardVM.gender.value = it.gender
                            staffOnboardVM.dateOfBirth.value = it.dateOfBirth
                            staffOnboardVM.relationshipStatus.value = it.relationshipStatus
                            staffOnboardVM.address.value = it.address
                            staffOnboardVM.pics.value = it.pics


                            editState = true
                            Log.e(TAG, "StaffProfile: ${staffOnboardVM.teacher.value?.staffId}", )

                        },
                        _id = it._id.toHexString()
                    )
                }
            }

        },
        sheetPeekHeight = 0.dp
    )


}




@Composable
fun StaffProfileScreen(
    onBackClick:()->Unit,
    bitmap: ImageBitmap,
    name: String, icon1: ImageVector = Icons.Default.Person,
    id: String, icon2: ImageVector = Icons.Default.PermIdentity,
    phone: String, icon3: ImageVector = Icons.Default.Phone,
    work: String, icon4: ImageVector = Icons.Default.Work,
    gender: String, icon5: ImageVector = Icons.Default.Female,
    dateOfBirth: String, icon6: ImageVector = Icons.Default.DateRange,
    relationship: String, icon7: ImageVector = Icons.Default.Transgender,
    address: String, icon8: ImageVector = Icons.Default.LocationOn,
    status: String, onStatusClick: ()-> Unit, onEditProfileClick:()->Unit,
    _id: String
){

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
            colors = CardDefaults.cardColors(
                containerColor = Cream,
                contentColor = Color.White
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(onBackClick = { onBackClick() }, title = "Staff Profile", textColor = Color.White, iconColor = Color.White)
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(modifier = Modifier.clickable { onStatusClick() }, text = status, fontSize = 12.sp)
                }
                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = CircleShape
                ) {
                    Image( modifier = Modifier
                        .size(100.dp), bitmap = bitmap,
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth)
                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp))
            }
        }



        LazyColumn(modifier = Modifier.weight(6f)){
            item{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StaffNameAndId(icon = icon1, staffName = name, id = _id)
                    StaffItems(icon = icon2, item = id)
                    StaffItems(icon = icon3, item = phone)
                    StaffItems(icon = icon4, item = work)
                    StaffItems(icon = icon5, item = gender)
                    StaffItems(icon = icon6, item = dateOfBirth)
                    StaffItems(icon = icon7, item = relationship)
                    StaffItems(icon = icon8, item = address)
                }

            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditProfileClick() }
//                .padding(vertical = 20.dp)
                .background(Cream)
        ) {
            Text(modifier = Modifier.padding(20.dp), text = "Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }


    }

}




@Composable
fun StaffItems(
    icon: ImageVector,
    item: String
){
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "report"
            )
            Spacer(modifier = Modifier.padding(30.dp))
            Text(text = item, fontSize = 14.sp)
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp, color = Cream)
    }

}

@Composable
fun StaffNameAndId(
    icon: ImageVector,
    staffName: String,
    id: String
){
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "report"
            )
            Spacer(modifier = Modifier.padding(30.dp))
            SelectionContainer {
                Column {
                    Text(text = staffName, fontSize = 14.sp)
                    Text(text = id, fontSize = 12.sp)
                }

            }
        }

        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp, color = Cream)
    }

}



//@Preview
//@Composable
//fun StaffProfilePreview(){
//    StaffProfileScreen {
//
//    }
//}