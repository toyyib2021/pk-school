package com.stevdza.san.mongodemo.screenStudent.family

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.model.Parent
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.AssociatedParentCardEdit
import com.stevdza.san.mongodemo.screenStudent.component.ParentCardEdit
import com.stevdza.san.mongodemo.screenStudent.component.StudentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.onboardedParent.SearchTopBar
import com.stevdza.san.mongodemo.screenStudent.parentOnboarding.ParentOnboardingScreen
import com.stevdza.san.mongodemo.screenStudent.studentOnboarding.StudentOnboardingScreen
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.Constants.FAMILY
import com.stevdza.san.mongodemo.util.Constants.PARENT_ASSOCIATE_EDIT
import com.stevdza.san.mongodemo.util.Constants.PARENT_EDIT
import com.stevdza.san.mongodemo.util.Constants.STUDENT_EDIT
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import com.stevdza.san.mongodemo.util.gender
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Family(familyID: String,
           navToHome:()->Unit,
           onAddGuardianClick:()->Unit,
           onAddStudentClick:()->Unit
){

    val familyVM: FamilyVM = viewModel()
    val context = LocalContext.current
    familyVM.objectID.value = familyID

    val accountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")

    familyVM.associateParentData.value.forEach {
        Log.e(TAG, "Family: ${it.familyId}", )
        Log.e(TAG, "Family: ${it.surname}", )
        Log.e(TAG, "Family: ${it.otherNames}", )
    }

    if(familyVM.parentData.value.isNotEmpty()){
        familyVM.getParentWithID()
    }

    if(familyVM.childrenData.value.isNotEmpty()){
        familyVM.getChildrenWIthFamilyID()
    }

    if(familyVM.associateParentData.value.isNotEmpty()){
        familyVM.getAssociateParentWithFamilyID()
    }

    var familyScreenState by remember { mutableStateOf(FAMILY) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            familyVM.bitmapEdit.value = it
        }

    familyVM.familyId.value = familyID
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcherForGallery = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

//    var snackbarMessage by remember { mutableStateOf("") }
//    var snackbarVisible by remember { mutableStateOf(false) }


    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    familyVM.date.value = formattedDate

    LaunchedEffect(key1 =  familyVM.studentStatus.value){
        familyVM.upDateStudentStatus(
            onError = { Log.e(TAG, "Family: error updating student Status",) },
            onSuccess = {
                Log.e(TAG, "Family: success updating student Status",)
            }
        )
    }

    BackHandler() {
        navToHome()
    }

    var bottomSheetState by remember { mutableStateOf(false) }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent ={
            var studentId by remember { mutableStateOf("") }
            var onSearch by remember { mutableStateOf(false) }
            if (bottomSheetState){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Cream)
                ){
                    SearchTopBar(
                        value = studentId,
                        onValueChange = {studentId =it},
                        onSearchClick = {
                            onSearch = !onSearch
                            familyVM.studentData.value = familyVM.childrenDataTwo.value.find { it.studentApplicationID == studentId }
//                            Log.e(TAG, "Family: studentData -> ${nu?.studentApplicationID}",)
                        },
                        label = "Enter Student Id"
                    )

                    if (onSearch){
                        familyVM.studentData.value?.let {
                            val bit = convertBase64ToBitmap(it.pics)
                            StudentCard(
                                student = it,
                                bitmap =bit.asImageBitmap() ,
                                onStudentClick = {
                                    familyVM.objectIdEdit.value = it._id.toHexString()
                                    familyVM.upDateStudentFamilyId(
                                        onError = {},
                                        onSuccess = {
                                            scope.launch {
                                                sheetState.collapse()
                                            }}
                                    )
                                    Log.e(TAG, "Family: objectIdEdit -> ${familyVM.objectIdEdit.value}",)
                                    Log.e(TAG, "Family: familyId -> ${familyVM.familyId.value}",)
                                    Log.e(TAG, "Family: familyIdChange -> ${it.familyId}",)
                                }
                            )
                        }

                    }else{
                        Spacer(modifier = Modifier.fillMaxWidth().padding(20.dp))
                        Text(
                            text = "Add A Student",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp)
                                .clickable { onAddStudentClick() },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.fillMaxWidth().padding(20.dp))
                    }


                }

            }
            else{
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Cream)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                familyVM.studentStatus.value = StudentStatusType.ACTIVE.status
                                scope.launch {
                                    sheetState.collapse()
                                }
                            },
                        text = StudentStatusType.ACTIVE.status,
                        fontSize = 14.sp)
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                familyVM.studentStatus.value = StudentStatusType.DROP_OUT.status
                                scope.launch {
                                    sheetState.collapse()
                                }
                            },
                        text = StudentStatusType.DROP_OUT.status,
                        fontSize = 14.sp)
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                familyVM.studentStatus.value = StudentStatusType.SUSPENDED.status
                                scope.launch {
                                    sheetState.collapse()
                                }
                            },
                        text = StudentStatusType.SUSPENDED.status,
                        fontSize = 14.sp)
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                }
            }

        },
        content = {
            when(familyScreenState){

                FAMILY->{
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(Cream)){

                        LazyColumn(modifier = Modifier.weight(9f)){
                            item{
                                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                    val oneParent = familyVM.oneParentData.value
                                    val bitmap = oneParent?.let { it.pics.let { it1 -> convertBase64ToBitmap(it1) } }
                                    if (oneParent != null) {
                                        if (bitmap != null) {
                                            ParentDetails(
                                                parent = oneParent,
                                                navToEditParentDetails = {
                                                    familyVM.objectIdEdit.value = oneParent._id.toHexString()
                                                    familyVM.surname.value = oneParent.surname
                                                    familyVM.otherNames.value = oneParent.otherNames
                                                    familyVM.phone.value = oneParent.phone
                                                    familyVM.address.value = oneParent.address
                                                    familyVM.gender.value = oneParent.gender
                                                    familyVM.relationship.value = oneParent.relationship
                                                    familyVM.picsEdit.value = oneParent.pics
                                                    familyScreenState = PARENT_EDIT },
                                                navToHome = {navToHome()},
                                                bitmap = bitmap.asImageBitmap()
                                            )
                                        }
                                    }
                                }

                            }

                            if(familyVM.childrenData.value.isNotEmpty()){
                                item{
                                    StudentDetails(
                                        familyVM = familyVM,
                                        onEditStudentProfileClick={
                                            familyVM.objectIdEdit.value = it._id.toHexString()
                                            familyVM.surname.value = it.surname
                                            familyVM.otherNames.value = it.otherNames
                                            familyVM.cless.value = it.cless
                                            familyVM.arms.value = it.arms
                                            familyVM.gender.value = it.gender
                                            familyVM.dateOfBirth.value = it.datOfBirth
                                            familyVM.studentApplicationID.value = it.studentApplicationID
                                            familyVM.familyId.value = it.familyId
                                            familyVM.picsEdit.value = it.pics
                                            familyScreenState = STUDENT_EDIT },
                                        onStudentStatusClick ={
                                            bottomSheetState = false
                                            familyVM.objectIdEdit.value = it._id.toHexString()
                                            scope.launch {
                                                if (sheetState.isCollapsed) {
                                                    sheetState.expand()
                                                } else {
                                                    sheetState.collapse()
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            item{
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                )
                                Text(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp),
                                    text = "Parent Associate", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            if(familyVM.associateParentData.value.isNotEmpty()){
                                items(familyVM.associateParentData.value){
                                    val bitmapAssociateParent = convertBase64ToBitmap(it.pics)
//                                    if (bitmapAssociateParent != null) {
                                        AssociatedParentCardEdit(
                                            associateParent = it,
                                            bitmap = bitmapAssociateParent.asImageBitmap(),
                                            onParentClick = {
                                                familyVM.objectIdEdit.value = it._id.toHexString()
                                                familyVM.surname.value = it.surname
                                                familyVM.otherNames.value = it.otherNames
                                                familyVM.phone.value = it.phone
                                                familyVM.address.value = it.address
                                                familyVM.gender.value = it.gender
                                                familyVM.relationship.value = it.relationship
                                                familyVM.familyId.value = it.familyId
                                                familyVM.picsEdit.value = it.pics
                                                familyScreenState = PARENT_ASSOCIATE_EDIT }
                                        )
//                                    }
                                }
                            }


                        }

                        when(accountType.value){
                            Constants.ATTENDANCE_COLLECTED -> {}
                            else -> {
                                Row(modifier = Modifier
                                    .padding(10.dp)
                                    .weight(1f)
                                ) {
                                    Column(modifier = Modifier
                                        .weight(5f)
                                        .clickable { onAddGuardianClick() }
                                        .fillMaxSize(),
//                        .padding(horizontal = 20.dp, vertical = 20.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Add a Guardian",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp)
                                    }

                                    Column(modifier = Modifier
                                        .weight(5f)
                                        .background(Color.White)
                                        .clickable {
                                            bottomSheetState = true
                                            scope.launch {
                                                if (sheetState.isCollapsed) {
                                                    sheetState.expand()
                                                } else {
                                                    sheetState.collapse()
                                                }
                                            }
                                        }
                                        .fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Add a Student",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp)
                                    }

                                }
                            }
                        }

                    }

                }
                STUDENT_EDIT->{

                    val cless = familyVM.classNames.value.map { it.className }
                    val clessArm = familyVM.armsNames.value.map { it.armsName }
                    val openList3 = remember { mutableStateOf(false) }

                    StudentOnboardingScreen(
                        surname = familyVM.surname.value,
                        surnameLabel = "Surname",
                        onSurnameChange = {familyVM.surname.value=it},
                        otherNames = familyVM.otherNames.value,
                        otherNamesLabel = "Other Names",
                        onOtherNamesChange = {familyVM.otherNames.value=it},
                        studentApplicationId = familyVM.studentApplicationID.value,
                        studentApplicationLabel = "Student Application Id",
                        onStudentApplicationChange = {familyVM.studentApplicationID.value=it},
                        onBackClick = {familyScreenState = FAMILY },
                        onPreviewClick = {
                            Log.e(TAG, "surname: ${familyVM.surname.value}", )
                            Log.e(TAG, "otherNames: ${familyVM.otherNames.value}", )
                            Log.e(TAG, "cless: ${familyVM.cless.value}", )
                            Log.e(TAG, "arms: ${familyVM.arms.value}", )
                            Log.e(TAG, "dateOfBirth: ${familyVM.dateOfBirth.value}", )
                            Log.e(TAG, "gender: ${familyVM.gender.value}", )
                            Log.e(TAG, "studentApplicationID: ${familyVM.studentApplicationID.value}", )
                            Log.e(TAG, "familyId: ${familyVM.familyId.value}", )
                            Log.e(TAG, "bitmap: ${familyVM.bitmapEdit.value}", )
                            Log.e(TAG, "familyID: ${familyVM.objectID.value}", )
                            familyVM.updateStudent(
                                onError = {
                                },
                                onSuccess = {
                            familyVM.bitmapEdit.value = null
                                    familyScreenState = FAMILY

                                },
//                           emptyFeild = {
//                               snackbarMessage ="empty fields"
//                               snackbarVisible=true
//                           }
                            )
                        },
                        onGetImageFromCamClick = {
                            imageUri = null
                            launcher.launch()
                        },
                        onGetImageFromGalleryClick = {  launcherForGallery.launch("image/*") },
                        imageFromGallery = {
                            if (imageUri == null){
                                familyVM.bitmapEdit.value.let {
                                    if (it != null) {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )
                                    }else{
                                        val bit =  familyVM.picsEdit.value.let { it1 -> convertBase64ToBitmap(it1) }

//                                        if (bit != null) {
                                            familyVM.bitmapEdit.value = bit
                                            Image(
                                                bitmap = bit.asImageBitmap(),
                                                contentDescription = null,
                                                modifier = Modifier.size(200.dp)
                                            )
//                                        }
                                    }
                                }
                            }else{
                                imageUri?.let {
                                    if (Build.VERSION.SDK_INT < 28) {
                                        familyVM.bitmapEdit.value = MediaStore.Images
                                            .Media.getBitmap(context.contentResolver, it)

                                    } else {
                                        val source = ImageDecoder
                                            .createSource(context.contentResolver, it)
                                        familyVM.bitmapEdit.value = ImageDecoder.decodeBitmap(source)
                                    }

                                    familyVM.bitmapEdit.value?.let { btm ->
                                        Image(
                                            bitmap = btm.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )


                                    }
                                }
                            }
                        },
//                snackbarVisible =snackbarVisible ,
//                snackbarMessage = snackbarMessage,
//                onDismissClick = { snackbarVisible=false },
                        onGenderClick={familyVM.gender.value=it},
                        genderType=familyVM.gender.value,
                        genders= gender,

                        clesses = cless,
                        onClessClick = {
                            familyVM.cless.value=it
                        },
                        cless = familyVM.cless.value,
                        onDateOfBirthChange = {familyVM.dateOfBirth.value=it},
                        dateOfBirth =familyVM.dateOfBirth.value,
                        dateOfBirthLabel = "Date of Birth (YYYY-MM-DD)",
                        classArms = clessArm,
                        onArmsClick={familyVM.arms.value=it},
                        arms=familyVM.arms.value,
                        familyID = familyID,
                        bitmap = familyVM.bitmapEdit.value,
                        context = context
                    )
                }
                PARENT_ASSOCIATE_EDIT->{

//            val familyVM: FamilyVM = hiltViewModel()


                    ParentOnboardingScreen(
                        surname=familyVM.surname.value,
                        surnameLabel="Surname",
                        onSurnameChange={familyVM.surname.value= it},
                        otherNames=familyVM.otherNames.value,
                        otherNamesLabel="Other Names",
                        onOtherNamesChange={familyVM.otherNames.value=it},
                        phone=familyVM.phone.value,
                        phoneLabel="Phone",
                        onPhoneChange={familyVM.phone.value=it},
                        relationship=familyVM.relationship.value,
                        relationshipLabel="Relationship",
                        onRelationshipChange={familyVM.relationship.value = it},
                        contactAddress=familyVM.address.value,
                        contactAddressLabel="Contact Address",
                        onContactAddressChange={familyVM.address.value=it},
                        onBackClick ={familyScreenState = FAMILY},

                        onPreviewClick= {
                            Log.e(TAG, "surname: ${familyVM.surname.value}", )
                            Log.e(TAG, "otherNames: ${familyVM.otherNames.value}", )
                            Log.e(TAG, "phone: ${familyVM.phone.value}", )
                            Log.e(TAG, "address: ${familyVM.address.value}", )
                            Log.e(TAG, "relationship: ${familyVM.relationship.value}", )
                            Log.e(TAG, "gender: ${familyVM.gender.value}", )
                            Log.e(TAG, "familyId: ${familyVM.familyId.value}", )
                            Log.e(TAG, "bitmapEdit: ${familyVM.bitmapEdit.value}", )
                            Log.e(TAG, "bitmap: ${familyVM.bitmap.value}", )
                            Log.e(TAG, "objectID: ${familyVM.objectIdEdit.value}", )
                            familyVM.upDateParentAssociate(
                                onError ={},
                                onSuccess = {
                                    familyVM.bitmapEdit.value = null
                                    familyScreenState = FAMILY
                                }
                            ) },
                        onGetImageFromCamClick = {
                            imageUri = null
                            launcher.launch() },
                        onGetImageFromGalleryClick = {
                            launcherForGallery.launch("image/*") },
                        imageFromGallery = {
                            if (imageUri == null){
                                familyVM.bitmapEdit.value.let {
                                    if (it != null) {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )
                                    }else{
                                        val bit =  familyVM.picsEdit.value.let { it1 -> convertBase64ToBitmap(it1) }
                                        familyVM.bitmapEdit.value = bit
                                        Image(
                                            bitmap = bit.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )
                                    }
                                }
                            }else{
                                imageUri?.let {
                                    if (Build.VERSION.SDK_INT < 28) {
                                        familyVM.bitmapEdit.value = MediaStore.Images
                                            .Media.getBitmap(context.contentResolver, it)
                                    } else {
                                        val source = ImageDecoder
                                            .createSource(context.contentResolver, it)
                                        familyVM.bitmapEdit.value = ImageDecoder.decodeBitmap(source)
                                    }

                                    familyVM.bitmapEdit.value?.let { btm ->
                                        Image(
                                            bitmap = btm.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )


                                    }
                                }
                            } },

                        onGenderClick={familyVM.gender.value = it},
                        genders= gender,
                        genderType=familyVM.gender.value,
                        bitmap = familyVM.bitmapEdit.value,
                        context = context
                    )
                }

                PARENT_EDIT->{

                    ParentOnboardingScreen(
                        surname=familyVM.surname.value,
                        surnameLabel="Surname",
                        onSurnameChange={familyVM.surname.value= it},
                        otherNames=familyVM.otherNames.value,
                        otherNamesLabel="Other Names",
                        onOtherNamesChange={familyVM.otherNames.value=it},
                        phone=familyVM.phone.value,
                        phoneLabel="Phone",
                        onPhoneChange={familyVM.phone.value=it},
                        relationship=familyVM.relationship.value,
                        relationshipLabel="Relationship",
                        onRelationshipChange={familyVM.relationship.value = it},
                        contactAddress=familyVM.address.value,
                        contactAddressLabel="Contact Address",
                        onContactAddressChange={familyVM.address.value=it},
                        onBackClick ={familyScreenState = FAMILY},

                        onPreviewClick= {
                            Log.e(TAG, "surname: ${familyVM.surname.value}", )
                            Log.e(TAG, "otherNames: ${familyVM.otherNames.value}", )
                            Log.e(TAG, "phone: ${familyVM.phone.value}", )
                            Log.e(TAG, "address: ${familyVM.address.value}", )
                            Log.e(TAG, "relationship: ${familyVM.relationship.value}", )
                            Log.e(TAG, "gender: ${familyVM.gender.value}", )
                            Log.e(TAG, "familyId: ${familyVM.familyId.value}", )
                            Log.e(TAG, "bitmapEdit: ${familyVM.bitmapEdit.value}", )
                            Log.e(TAG, "objectID: ${familyVM.objectIdEdit.value}", )
                            familyVM.upDateParent(
                                onError ={},
                                onSuccess = {
                                    familyVM.bitmapEdit.value = null
                                    familyScreenState = FAMILY
                                }
                            )
                        },
                        onGetImageFromCamClick = {
                            imageUri = null
                            launcher.launch() },
                        onGetImageFromGalleryClick = {
                            launcherForGallery.launch("image/*")
                        },
                        imageFromGallery = {
                            if (imageUri == null){
                                familyVM.bitmapEdit.value.let {
                                    if (it != null) {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )
                                    }else{
                                        val bit =  familyVM.picsEdit.value.let { it1 -> convertBase64ToBitmap(it1) }

//                                        if (bit != null) {
                                            familyVM.bitmapEdit.value = bit
                                            Image(
                                                bitmap = bit.asImageBitmap(),
                                                contentDescription = null,
                                                modifier = Modifier.size(200.dp)
                                            )
//                                        }
                                    }
                                }
                            }else{
                                imageUri?.let {
                                    if (Build.VERSION.SDK_INT < 28) {
                                        familyVM.bitmapEdit.value = MediaStore.Images
                                            .Media.getBitmap(context.contentResolver, it)

                                    } else {
                                        val source = ImageDecoder
                                            .createSource(context.contentResolver, it)
                                        familyVM.bitmapEdit.value = ImageDecoder.decodeBitmap(source)
                                    }

                                    familyVM.bitmapEdit.value?.let { btm ->
                                        Image(
                                            bitmap = btm.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )


                                    }
                                }
                            }

                        },
                        onGenderClick={familyVM.gender.value = it},
                        genders= gender,
                        genderType=familyVM.gender.value,
                        bitmap = familyVM.bitmapEdit.value,
                        context = context
                    )
                }
            }
        },
        sheetPeekHeight = 0.dp
    )







}

@Composable
private fun StudentDetails(
    familyVM: FamilyVM,
    onEditStudentProfileClick:(StudentData)->Unit,
    onStudentStatusClick:(StudentData)->Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        LazyRow() {
            items(familyVM.childrenData.value) {
                val bitmapImage = convertBase64ToBitmap(it.pics)
//                if (bitmapImage != null) {

//                    val sizeInBytes = bitmapImage.byteCount
//                    val sizeInKB = sizeInBytes / 1024.0
//                    Log.e(TAG, "StudentDetails: $sizeInKB", )
                    StudentCard(
                        bitmap = bitmapImage.asImageBitmap(),
                        student = it,
                        onEditStudentProfileClick = { onEditStudentProfileClick(it) },
                        onStudentStatusClick = {onStudentStatusClick(it)}
                    )
//                }
            }
        }




    }
}


@Composable
fun ParentDetails (
    parent:Parent,
    navToEditParentDetails:()->Unit,
    navToHome:()->Unit,
    bitmap: ImageBitmap
){

    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        TopBar(onBackClick = { navToHome() }, title = parent.surname)

        ParentCardEdit(
            parent = parent,
            bitmap = bitmap,
            onParentClick = { navToEditParentDetails() }
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        text = "Student", fontSize = 14.sp, fontWeight = FontWeight.Bold)

    }
}


