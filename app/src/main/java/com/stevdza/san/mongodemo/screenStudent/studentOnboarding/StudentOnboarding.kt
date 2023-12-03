package com.stevdza.san.mongodemo.screenStudent.studentOnboarding

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.gender
import com.stevdza.san.mongodemo.util.isInternetAvailable
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun StudentOnboardingScreen(
    onBackCLick:()->Unit,
    navToFamilyScreen:()->Unit,
    familyID:String
){

    val studentOnboardingVM: StudentOnboardingVM= viewModel()

    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    studentOnboardingVM.dateCreated.value = formattedDate
    studentOnboardingVM.statusDate.value = formattedDate
    val cless = studentOnboardingVM.classNames.value.sortedByDescending { it._id }
    val clessTwo = cless.map { it.className }
    val clessArm = studentOnboardingVM.armsNames.value.map { it.armsName }

    studentOnboardingVM.familyId.value = familyID
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            studentOnboardingVM.bitmap.value = it
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

    StudentOnboardingScreen(
        surname = studentOnboardingVM.surname.value,
        surnameLabel = "Surname",
        onSurnameChange = {studentOnboardingVM.surname.value=it},
        otherNames = studentOnboardingVM.otherNames.value,
        otherNamesLabel = "Other Names",
        onOtherNamesChange = {studentOnboardingVM.otherNames.value=it},
        studentApplicationId = studentOnboardingVM.studentApplicationID.value,
        studentApplicationLabel = "Student Application Id",
        onStudentApplicationChange = {studentOnboardingVM.studentApplicationID.value=it},
        onBackClick = {onBackCLick() },
        onPreviewClick = {
            Log.e(TAG, "surname: ${studentOnboardingVM.surname.value}", )
            Log.e(TAG, "otherNames: ${studentOnboardingVM.otherNames.value}", )
            Log.e(TAG, "cless: ${studentOnboardingVM.cless.value}", )
            Log.e(TAG, "arms: ${studentOnboardingVM.arms.value}", )
            Log.e(TAG, "dateOfBirth: ${studentOnboardingVM.dateOfBirth.value}", )
            Log.e(TAG, "gender: ${studentOnboardingVM.gender.value}", )
            Log.e(TAG, "studentApplicationID: ${studentOnboardingVM.studentApplicationID.value}", )
            Log.e(TAG, "familyId: ${studentOnboardingVM.familyId.value}", )
            Log.e(TAG, "bitmap: ${studentOnboardingVM.bitmap.value}", )
            Log.e(TAG, "familyID: $familyID", )
            studentOnboardingVM.insertStudent(
                onError = {

                           },
                onSuccess = {
                    navToFamilyScreen()
                            },
            ) },
        onGetImageFromCamClick = {
                        imageUri = null
                        launcher.launch()
                    },
        onGetImageFromGalleryClick = {  launcherForGallery.launch("image/*") },
        imageFromGallery = {
                        if (imageUri == null){
                            studentOnboardingVM.bitmap.value.let {
                                if (it != null) {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(200.dp)
                                    )
                                }
                            }
                        }else{
                            imageUri?.let {
                                if (Build.VERSION.SDK_INT < 28) {
                                    studentOnboardingVM.bitmap.value = MediaStore.Images
                                        .Media.getBitmap(context.contentResolver, it)

                                } else {
                                    val source = ImageDecoder
                                        .createSource(context.contentResolver, it)
                                    studentOnboardingVM.bitmap.value = ImageDecoder.decodeBitmap(source)
                                }

                                studentOnboardingVM.bitmap.value?.let { btm ->
                                    Image(
                                        bitmap = btm.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(200.dp)
                                    )


                                }
                            }
                        }
                    },
//        snackbarVisible =snackbarVisible ,
//        snackbarMessage = snackbarMessage,
//        onDismissClick = { snackbarVisible=false },
        onGenderClick={studentOnboardingVM.gender.value=it},
        genderType=studentOnboardingVM.gender.value,
        genders= gender,
        clesses = clessTwo,
        onClessClick = {
            studentOnboardingVM.cless.value=it },
        cless = studentOnboardingVM.cless.value,
        onDateOfBirthChange = {studentOnboardingVM.dateOfBirth.value=it},
        dateOfBirth =studentOnboardingVM.dateOfBirth.value ,
        dateOfBirthLabel = "Date of Birth (YYYY-MM-DD)",
        classArms = clessArm,
        onArmsClick={studentOnboardingVM.arms.value=it},
        arms=studentOnboardingVM.arms.value,
        familyID = familyID,
        bitmap = studentOnboardingVM.bitmap.value,
        context = context

    )
}


@Composable
fun StudentOnboardingScreen(
    surname:String,
    surnameLabel:String,
    onSurnameChange:(String)->Unit,
    otherNames:String,
    otherNamesLabel:String,
    onOtherNamesChange:(String)->Unit,
    studentApplicationId:String,
    studentApplicationLabel:String,
    onStudentApplicationChange:(String)->Unit,
    onBackClick:()->Unit,
    onPreviewClick:()->Unit,
    onGetImageFromCamClick:()->Unit,
    onGetImageFromGalleryClick:()->Unit,
    imageFromGallery:@Composable () -> Unit,
//    snackbarVisible: Boolean,
//    snackbarMessage: String,
//    onDismissClick: ()->Unit,
    onGenderClick:(String)->Unit,
    genderType:String,
    genders:List<String>,
    clesses: List<String>,
    onClessClick:(String)->Unit,
    cless: String,
    onDateOfBirthChange:(String)->Unit,
    dateOfBirth: String,
    dateOfBirthLabel:String,
    classArms: List<String>,
    onArmsClick:(String)->Unit,
    arms:String,
    familyID: String,
    bitmap: Bitmap?,
    context: Context

    ){

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Cream)) {
        var openList by remember { mutableStateOf(false) }
        var openList2 by remember { mutableStateOf(false) }
//        var openList3 by remember { mutableStateOf(false) }
        Column(modifier = Modifier
            .weight(1f)){
                    TopBar(onBackClick =onBackClick ,
                        title = "Onboarding")
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                }

        Column(modifier = Modifier.weight(8f)
        ) {
            LazyColumn{
                item{

                    TextFieldForm(value = surname,
                        label =surnameLabel , onValueChange ={onSurnameChange(it)} )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    TextFieldForm(value = otherNames,
                        label =otherNamesLabel , onValueChange ={onOtherNamesChange(it)} )

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    TextFieldForm(value = studentApplicationId,
                        label =studentApplicationLabel , onValueChange ={onStudentApplicationChange(it)} )

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))

                    Text( modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                        text = "Class", fontSize =14.sp)
                    Card( modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        elevation = 10.dp
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openList = true
                                }
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (openList){

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = 10.dp,
                                    backgroundColor = Color.White
                                ) {
                                    Column() {
                                        clesses.forEach {
                                            Text(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .clickable {
                                                    onClessClick(it)
                                                    openList = false
                                                },
                                                text = it, textAlign = TextAlign.Center )
                                            Divider(thickness = 2.dp)
                                        }
                                    }

                                }
                            }else{

                                Text(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),text = cless )
                                Icon(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                    imageVector = Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
                            }

                        }
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))

                    Text( modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                        text = "Arms", fontSize =14.sp)
                    Card( modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        elevation = 10.dp
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openList2 = true
                                }
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (openList2){

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = 10.dp,
                                    backgroundColor = Color.White
                                ) {
                                    Column() {
                                        classArms.forEach {
                                            Text(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .clickable {
                                                    onArmsClick(it)
                                                    openList2 = false
                                                },
                                                text = it, textAlign = TextAlign.Center )
                                            Divider(thickness = 2.dp)
                                        }
                                    }

                                }
                            }else{

                                Text(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),text = arms )
                                Icon(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                    imageVector = Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
                            }

                        }
                    }
//                    Card( modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 20.dp),
//                        elevation = 10.dp
//                    ) {
//
//                        Text(modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),text = StudentStatusType.ACTIVE.status )
//                        Icon(modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),
//                            imageVector = Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
//
//                        }


                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))

                    Text( modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                        text = "Gender", fontSize =14.sp)
                    Card( modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        elevation = 10.dp
                    ) {
                        var openListGender by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openListGender = true
                                }
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (openListGender){

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = 10.dp,
                                    backgroundColor = Color.White
                                ) {
                                    Column() {
                                        genders.forEach {
                                            Text(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .clickable {
                                                    onGenderClick(it)
                                                    openListGender = false
                                                },
                                                text = it, textAlign = TextAlign.Center )
                                            Divider(thickness = 2.dp)
                                        }
                                    }

                                }
                            }else{

                                Text(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),text = genderType )
                                Icon(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                    imageVector = Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
                            }

                        }
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    TextFieldForm(value = dateOfBirth,
                        onValueChange ={onDateOfBirthChange(it)},
                        backgroundColor = if (dateOfBirth.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            Color.White
                        }else{
                            Color.Red
                        },
                        cursorColor = if (dateOfBirth.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            Color.Black
                        }else{
                            Color.White
                        },
                        cardBackgroundColor =  if (dateOfBirth.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            Color.White
                        }else{
                            Color.Red
                        },
                        label=  dateOfBirthLabel,
                    )

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(5f),
                            content = {
                                Button(onClick = {
                                    onGetImageFromCamClick()
                                },
                                    content = {
                                        Text(text = "Image From Camera")
                                    })
                            }
                        )
                        Column(modifier = Modifier
                            .padding(10.dp)
                            .weight(5f)) {
                            Button(onClick = {
                                onGetImageFromGalleryClick()
                            }) {
                                Text(text = "Pick image")
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        imageFromGallery()
                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                }

                }
            }


        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            val bitSize = bitmap?.byteCount?.div(1024.0)
            Log.e(TAG, "bitSize: $bitSize", )
            if (isInternetAvailable(context)){
                if ( surname.isNotEmpty() && otherNames.isNotEmpty() &&
                    cless != "Select" &&
                    familyID.isNotEmpty() ){

                    if (bitmap == null){
                        Text(modifier = Modifier
                            .fillMaxWidth(), text = "Upload Image", fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }else{
                        if (bitSize != null) {
                            if (bitSize <= 150.0){
                                Button( modifier = Modifier
                                    .fillMaxWidth(),
                                    onClick = { onPreviewClick() },
                                    shape = RectangleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.White,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(modifier = Modifier
                                        .fillMaxWidth(), text = "Submit", fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }else{
                                Text(modifier = Modifier
                                    .fillMaxWidth(), text = "Image size is too large", fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }



                }else{
                    Text(modifier = Modifier
                        .fillMaxWidth(), text = "Fill all the empty form", fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }else{
                Text(modifier = Modifier
                    .fillMaxWidth(), text = "No Internet Connection", fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }


        }
    }

}



@Composable
fun MySpinner(
    onOpen3Click: ()->Unit,
    onClose3Click: ()->Unit,
    studentStatusList: List<String>,
    onStatusClick: (String)-> Unit,
    openList3: MutableState<Boolean>,
    startText: String
){
    Card( modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
        elevation = 10.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onOpen3Click()
                }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (openList3.value){

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 10.dp,
                    backgroundColor = Color.White
                ) {
                    Column() {
                        studentStatusList.forEach {
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    onStatusClick(it)
                                    onClose3Click()
                                },
                                text = it, textAlign = TextAlign.Center )
                            Divider(thickness = 2.dp)
                        }
                    }

                }
            }else{

                Text(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),text = startText )
                Icon(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                    imageVector = Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
            }

        }
    }
}
