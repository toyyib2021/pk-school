package com.stevdza.san.mongodemo.screenStudent.parentAssociateOnboarding

import android.content.ContentValues.TAG
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.screenStudent.parentOnboarding.ParentOnboardingScreen
import com.stevdza.san.mongodemo.util.gender

@Composable
fun ParentAssociateOnboarding(
    onBackClick:()->Unit,
    navToFamily:()->Unit,
    familyId:String
){

    val parentAssociateOnboardingVM: ParentAssociateOnboardingVM = viewModel()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            parentAssociateOnboardingVM.bitmap.value = it
        }

    parentAssociateOnboardingVM.familyId.value = familyId
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

    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }



            ParentOnboardingScreen(
                surname=parentAssociateOnboardingVM.surname.value,
                surnameLabel="Surname",
                onSurnameChange={parentAssociateOnboardingVM.surname.value= it},
                otherNames=parentAssociateOnboardingVM.otherNames.value,
                otherNamesLabel="Other Names",
                onOtherNamesChange={parentAssociateOnboardingVM.otherNames.value=it},
                phone=parentAssociateOnboardingVM.phone.value,
                phoneLabel="Phone",
                onPhoneChange={parentAssociateOnboardingVM.phone.value=it},
                relationship=parentAssociateOnboardingVM.relationship.value,
                relationshipLabel="Relationship",
                onRelationshipChange={parentAssociateOnboardingVM.relationship.value = it},
                contactAddress=parentAssociateOnboardingVM.address.value,
                contactAddressLabel="Contact Address",
                onContactAddressChange={parentAssociateOnboardingVM.address.value=it},
                onBackClick =onBackClick,
                onPreviewClick= {
                    Log.e(TAG, "surname: ${parentAssociateOnboardingVM.surname.value}", )
                    Log.e(TAG, "otherNames: ${parentAssociateOnboardingVM.otherNames.value}", )
                    Log.e(TAG, "phone: ${parentAssociateOnboardingVM.phone.value}", )
                    Log.e(TAG, "address: ${parentAssociateOnboardingVM.address.value}", )
                    Log.e(TAG, "relationship: ${parentAssociateOnboardingVM.relationship.value}", )
                    Log.e(TAG, "gender: ${parentAssociateOnboardingVM.gender.value}", )
                    Log.e(TAG, "familyId: ${parentAssociateOnboardingVM.familyId.value}", )
                    Log.e(TAG, "bitmap: ${parentAssociateOnboardingVM.bitmap.value}", )
                    parentAssociateOnboardingVM.insertParentAssociate(
                        onError ={
                            snackbarMessage = "An Error Just Error"
                            snackbarVisible = true
                        } ,
                        onSuccess = {navToFamily()}
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
                        parentAssociateOnboardingVM.bitmap.value.let {
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
                                parentAssociateOnboardingVM.bitmap.value = MediaStore.Images
                                    .Media.getBitmap(context.contentResolver, it)

                            } else {
                                val source = ImageDecoder
                                    .createSource(context.contentResolver, it)
                                parentAssociateOnboardingVM.bitmap.value = ImageDecoder.decodeBitmap(source)
                            }

                            parentAssociateOnboardingVM.bitmap.value?.let { btm ->
                                Image(
                                    bitmap = btm.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                        }
                    }

                },
//                snackbarVisible=snackbarVisible,
//                snackbarMessage=snackbarMessage,
//                onDismissClick={snackbarVisible=false},
                onGenderClick={parentAssociateOnboardingVM.gender.value=it},
                genderType=parentAssociateOnboardingVM.gender.value,
                genders= gender,
                bitmap =parentAssociateOnboardingVM.bitmap.value,
                context = context
            )
        }
