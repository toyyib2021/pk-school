package com.stevdza.san.mongodemo.screenTeacher.onboardStaff

import android.content.ContentValues
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.stevdza.san.mongodemo.screenStudent.studentOnboarding.StudentOnboardingVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.gender
import com.stevdza.san.mongodemo.util.isInternetAvailable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun OnboardStaff(
    onBackCLick:()->Unit,
    navToStaffOnboardScreen: ()->Unit
){

    val staffOnboardingVM: StaffOnboardingVM = viewModel()

    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    staffOnboardingVM.dateCreated.value = formattedDate
    staffOnboardingVM.statusDate.value = formattedDate


    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            staffOnboardingVM.bitmap.value = it
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
        surname = staffOnboardingVM.surname.value,
        surnameLabel = "Surname",
        onSurnameChange = {staffOnboardingVM.surname.value=it},
        otherNames = staffOnboardingVM.otherNames.value,
        otherNamesLabel = "Other Names",
        onOtherNamesChange = {staffOnboardingVM.otherNames.value=it},
        staffId = staffOnboardingVM.staffApplicationID.value,
        staffIdLabel = "Student Application Id",
        onStaffIdChange = {staffOnboardingVM.staffApplicationID.value=it},
        onBackClick = {onBackCLick() },
        onPreviewClick = {
            Log.e(ContentValues.TAG, "surname: ${staffOnboardingVM.surname.value}", )
            Log.e(ContentValues.TAG, "otherNames: ${staffOnboardingVM.otherNames.value}", )
            Log.e(ContentValues.TAG, "dateOfBirth: ${staffOnboardingVM.dateOfBirth.value}", )
            Log.e(ContentValues.TAG, "gender: ${staffOnboardingVM.gender.value}", )
            Log.e(ContentValues.TAG, "staffApplicationID: ${staffOnboardingVM.staffApplicationID.value}", )
            Log.e(ContentValues.TAG, "phone: ${staffOnboardingVM.phone.value}", )
            Log.e(ContentValues.TAG, "bitmap: ${staffOnboardingVM.bitmap.value}", )
            Log.e(ContentValues.TAG, "address: ${staffOnboardingVM.address.value}", )
            staffOnboardingVM.insertStudent(
                onError = {

                },
                onSuccess = {
                    navToStaffOnboardScreen()
                },
            ) },
        onGetImageFromCamClick = {
            imageUri = null
            launcher.launch()
        },
        onGetImageFromGalleryClick = {  launcherForGallery.launch("image/*") },
        imageFromGallery = {
            if (imageUri == null){
                staffOnboardingVM.bitmap.value.let {
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
                        staffOnboardingVM.bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)

                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        staffOnboardingVM.bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    staffOnboardingVM.bitmap.value?.let { btm ->
                        Image(
                            bitmap = btm.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )


                    }
                }
            }
        },

        onGenderClick={staffOnboardingVM.gender.value=it},
        genderType=staffOnboardingVM.gender.value,
        genders= gender,
        onDateOfBirthChange = {staffOnboardingVM.dateOfBirth.value=it},
        dateOfBirth =staffOnboardingVM.dateOfBirth.value ,
        dateOfBirthLabel = "Date of Birth (YYYY-MM-DD)",
        bitmap = staffOnboardingVM.bitmap.value,
        context = context,
        phone = staffOnboardingVM.phone.value,
        phoneLabel = "Phone",
        onPhoneChange = {staffOnboardingVM.phone.value=it},
        post = staffOnboardingVM.post.value,
        postLabel = "Position",
        onPostLabelChange = {staffOnboardingVM.post.value=it},
        relationship = staffOnboardingVM.relationshipStatus.value,
        relationshipLabel = "Relationship Status",
        onRelationshipChange = {staffOnboardingVM.relationshipStatus.value=it},
        contactAddress = staffOnboardingVM.address.value,
        contactAddressLabel = "Contact Address",
        onContactAddressChange = {staffOnboardingVM.address.value=it},

    )
}

@Composable
fun OnboardStaffScreen(
    surname:String,
    surnameLabel:String,
    onSurnameChange:(String)->Unit,
    otherNames:String,
    otherNamesLabel:String,
    onOtherNamesChange:(String)->Unit,
    phone:String,
    phoneLabel:String,
    onPhoneChange:(String)->Unit,
    relationship:String,
    relationshipLabel:String,
    onRelationshipChange:(String)->Unit,
    contactAddress:String,
    contactAddressLabel:String,
    onContactAddressChange:(String)->Unit,
    onBackClick:()->Unit,
    onPreviewClick:()->Unit,
    onGetImageFromCamClick:()->Unit,
    onGetImageFromGalleryClick:()->Unit,
    imageFromGallery:@Composable () -> Unit,
    onGenderClick:(String)->Unit,
    genders: List<String>,
    genderType: String,
    bitmap: Bitmap?,
    context: Context,
    staffId: String,
    staffIdLabel: String,
    onStaffIdChange: (String)->Unit,
    dateOfBirth: String,
    onDateOfBirthChange:(String)->Unit,
    dateOfBirthLabel: String,
    post: String,
    postLabel:String,
    onPostLabelChange:(String)->Unit
    ){


        Column(modifier = Modifier
            .fillMaxSize()
            .background(Cream)) {


            Column(modifier = Modifier.weight(1f)) {
                TopBar(onBackClick =onBackClick ,
                    title = "Onboarding")
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            }

            LazyColumn(modifier = Modifier.weight(8f)) {
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
                    TextFieldForm(value = staffId,
                        label =staffIdLabel , onValueChange ={onStaffIdChange(it) })
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
                        var openList by remember { mutableStateOf(false) }

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
                                        genders.forEach {
                                            Text(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .clickable {
                                                    onGenderClick(it)
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
                    TextFieldForm(value = post,
                        label =postLabel , onValueChange ={onPostLabelChange(it)} )


                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    TextFieldForm(value = phone,
                        label =phoneLabel , onValueChange ={onPhoneChange(it.filter { it.isDigit() })} )

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    TextFieldForm(value = relationship,
                        label =relationshipLabel , onValueChange ={onRelationshipChange(it)} )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    TextFieldForm(value = contactAddress,
                        label =contactAddressLabel , onValueChange ={onContactAddressChange(it)} )
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

            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                val bitSize = bitmap?.byteCount?.div(1024.0)
                Log.e(ContentValues.TAG, "bitSize: $bitSize", )
                if (isInternetAvailable(context)){
                    if ( surname.isNotEmpty() && otherNames.isNotEmpty() &&
                        phone.isNotEmpty() && relationship.isNotEmpty() &&
                        contactAddress.isNotEmpty() && post.isNotEmpty() &&
                                staffId.isNotEmpty()
                    ){

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