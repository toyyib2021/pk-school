package com.stevdza.san.mongodemo.screenStudent.onboardedStudent


import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.model.StudentStatusType
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.ParentCard
import com.stevdza.san.mongodemo.screenStudent.component.StudentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.onboardedParent.OnBoardedParentVM
import com.stevdza.san.mongodemo.screenStudent.onboardedParent.SearchTopBar
import com.stevdza.san.mongodemo.screenStudent.parentOnboarding.ParentOnboardingScreen
import com.stevdza.san.mongodemo.screenStudent.parentOnboarding.ParentOnboardingVM
import com.stevdza.san.mongodemo.ui.theme.Blue
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.Constants
import com.stevdza.san.mongodemo.util.Constants.ATTENDANCE_BY_ADMIN
import com.stevdza.san.mongodemo.util.Constants.ONBOARDED_STUDENT
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import com.stevdza.san.mongodemo.util.gender
import com.stevdza.san.pk.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OnboardedStudent(
    onBackClick:()->Unit,
    navToFamily:(String)->Unit,
    navToTakeAttendance:(String)->Unit,
    takeAttendanceOrGoToFamily: String
){

    val onBoardedStudentVM: OnBoardedStudentVM = viewModel()
    val onBoardedParentVM: OnBoardedParentVM = viewModel()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Log.e(TAG, "takeAttendanceOrGoToFamily: $takeAttendanceOrGoToFamily", )

    val data = onBoardedStudentVM.studentData.value
    val data2 = onBoardedStudentVM.studentData2.value
    val parentData = onBoardedParentVM.parentData2.value


    val clesses = onBoardedStudentVM.studentClass.value.sortedByDescending { it._id }
    val clessesTwo = clesses.map { it.className }
    val armses = onBoardedStudentVM.studentArm.value.map { it.armsName }
    var selectedClass by remember { mutableStateOf("All") }
    var selectedArms by remember { mutableStateOf("All") }
    val studentByClass= data.filter {
        it.cless == selectedClass && it.studentStatus?.status == StudentStatusType.ACTIVE.status}
    val studentByClassAndArms= data.filter {
        it.cless == selectedClass && it.arms == selectedArms && it.studentStatus?.status == StudentStatusType.ACTIVE.status}


    var addFamily by remember { mutableStateOf(false) }

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState )

    var studentId by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            when(addFamily){
                true -> {
                    val parentOnboardingVM: ParentOnboardingVM = viewModel()
                    val launcher =
                        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                            parentOnboardingVM.bitmap.value = it
                        }

                    var imageUri by remember {
                        mutableStateOf<Uri?>(null)
                    }
//                               val context = LocalContext.current


                    val launcherForGallery = rememberLauncherForActivityResult(
                        contract =
                        ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        imageUri = uri
                    }

                    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                    ParentOnboardingScreen(
                        surname=parentOnboardingVM.surname.value,
                        surnameLabel="Surname",
                        onSurnameChange={parentOnboardingVM.surname.value= it},
                        otherNames=parentOnboardingVM.otherNames.value,
                        otherNamesLabel="Other Names",
                        onOtherNamesChange={parentOnboardingVM.otherNames.value=it},
                        phone=parentOnboardingVM.phone.value,
                        phoneLabel="Phone",
                        onPhoneChange={parentOnboardingVM.phone.value=it},
                        relationship=parentOnboardingVM.relationship.value,
                        relationshipLabel="Relationship",
                        onRelationshipChange={parentOnboardingVM.relationship.value = it},
                        contactAddress=parentOnboardingVM.address.value,
                        contactAddressLabel="Contact Address",
                        onContactAddressChange={parentOnboardingVM.address.value=it},
                        onBackClick = {addFamily = false},
                        onPreviewClick= {
                            parentOnboardingVM.insertParentPass(
                                onError ={},
                                onSuccess = {addFamily = false}
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
                                parentOnboardingVM.bitmap.value.let {
                                    if (it != null) {
                                        bitmap = it
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
                                        parentOnboardingVM.bitmap.value = MediaStore.Images
                                            .Media.getBitmap(context.contentResolver, it)

                                    } else {
                                        val source = ImageDecoder
                                            .createSource(context.contentResolver, it)
                                        parentOnboardingVM.bitmap.value = ImageDecoder.decodeBitmap(source)
                                    }

                                    parentOnboardingVM.bitmap.value?.let { btm ->
                                        bitmap = btm
                                        Image(
                                            bitmap = btm.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(200.dp)
                                        )


                                    }
                                }
                            }

                        },
                        onGenderClick={parentOnboardingVM.gender.value = it},
                        genders= gender,
                        genderType=parentOnboardingVM.gender.value,
                        bitmap = bitmap,
                        context = context
                    )
                }
                false ->{
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(Cream)) {
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .background(Blue)
                            ) {
                                Spacer(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp))
                                Card(modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    shape = RectangleShape,
                                ) {
                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { addFamily = true },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "Connect Parent", modifier = Modifier
                                            .padding(10.dp),  fontWeight = FontWeight.Bold)
                                    }
                                }

                                SearchTopBar(
                                    value =onBoardedParentVM.surname.value ,
                                    onValueChange = { onBoardedParentVM.surname.value=it},
                                    onSearchClick ={ onBoardedParentVM.getParentWithName() },
                                    label = "Enter Parent Name Here", iconColor = Color.White, color = Color.White
                                )
                                Spacer(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp))
                                LazyColumn(){
                                    items(parentData){
                                        Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                                            var hide by remember { mutableStateOf(false) }
                                            val imageBitmap = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
                                            ParentCard(
                                                parent = it,
                                                bitmap = imageBitmap.asImageBitmap()
                                            ) {
                                                hide = !hide
                                            }
                                            if (hide){
                                                BtnNormal(onclick = {
                                                    onBoardedStudentVM.objectId.value = studentId
                                                    onBoardedStudentVM.familyId.value = it._id.toHexString()
                                                    onBoardedStudentVM.upDateStudentFamilyId(onSuccess = {
                                                        navToFamily(it._id.toHexString())
                                                    }, onError = {})
                                                }, text = "Connect ${it.surname} ${it.otherNames} with $studentName",)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        },
        content = {
            if (sheetState.isExpanded) {
                Column(
                    modifier = Modifier.fillMaxSize().background(Cream),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(id = R.drawable.pk_logo), contentDescription = "")
                }
            }
            else {
                OnboardedStudentScreen(
                    cless = selectedClass,
                    arms = selectedArms,
                    onBackClick = { onBackClick() },
                    clesses = clessesTwo,
                    armses = armses,
                    getClass = {
                        selectedArms = "All"
                        onBoardedStudentVM.surname.value = ""
                        selectedClass = it
                    },
                    getArms = { selectedArms = it },
                    student = when (selectedArms) {
                        "All" -> {
                            studentByClass.sortedBy { it.surname }

                        }

                        "Search" -> {
                            data2

                        }

                        else -> {
                            studentByClassAndArms.sortedBy { it.surname }
                        }
                    },
                    navToFamily = {
                        if (takeAttendanceOrGoToFamily == ATTENDANCE_BY_ADMIN) {
                            if (it.familyId != "r") {
                                navToTakeAttendance(it.familyId)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Student do not have a family",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            Log.e(TAG, "otherNames: ${it.otherNames}",)
                            Log.e(TAG, "otherNames: ${it.familyId}",)
                        } else if (ONBOARDED_STUDENT == takeAttendanceOrGoToFamily) {
                            if (it.familyId != "r") {
                                navToFamily(it.familyId)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Student do not have a family",
                                    Toast.LENGTH_SHORT
                                ).show()
                                scope.launch {
                                    if (sheetState.isCollapsed) {
                                        sheetState.expand()
                                    } else {
                                        sheetState.collapse()
                                    }
                                }
                                studentName = "${it.surname} ${it.otherNames}"
                                studentId = it._id.toHexString()
                            }
                            Log.e(TAG, "otherNames: ${it.otherNames}",)
                            Log.e(TAG, "otherNames: ${it.familyId}",)
                        }

                    },
                    onSearchClick = {
                        selectedArms = "Search"
                        onBoardedStudentVM.getStudentWithSurname()
                    },
                    onValueChange = { onBoardedStudentVM.surname.value = it },
                    value = onBoardedStudentVM.surname.value,
                    takeAttendanceOrGoToFamily = takeAttendanceOrGoToFamily
                )
            }

        },
        sheetPeekHeight = 0.dp
    )
}


@Composable
fun OnboardedStudentScreen(
    cless:String,
    arms:String,
    student: List<StudentData>,
    onBackClick:()->Unit,
    clesses: List<String>,
    armses: List<String>,
    getClass:(String)->Unit,
    getArms:(String)->Unit,
    navToFamily:(StudentData)->Unit,
    onSearchClick:()->Unit,
    onValueChange:(String)->Unit,
    value: String,
    takeAttendanceOrGoToFamily: String
){

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)) {
        TopBar(onBackClick = { onBackClick() }, title = "Students")
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
        ) {
            var hideClass by remember { mutableStateOf(false) }
            var hideArms by remember { mutableStateOf(false) }
            Column(modifier = Modifier
                .weight(6f)
                .padding(vertical = 5.dp)
                .clickable { hideClass = !hideClass }
                .background(Color.White)
            ) {

                if (hideClass){
                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Class", fontSize = 12.sp)
                    Spacer(modifier = Modifier.padding(5.dp))
                    clesses.forEach {
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                getClass(it)
                                hideClass = false
                            }
                            .padding(horizontal = 10.dp),
                            text = it, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.padding(5.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                    }


                }else{

                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Class", fontSize = 12.sp)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable { hideClass = true }) {
                        Text(text = cless, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "ArrowDropDown", tint = Color.Black)
                    }
                }

            }
            Column(modifier = Modifier
                .weight(4f)
                .padding(vertical = 5.dp)
                .clickable { hideArms = !hideArms }
                .background(Color.Black)) {
                if (hideArms){
                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Arms", fontSize = 12.sp)
                    Spacer(modifier = Modifier.padding(5.dp))
                    armses.forEach {
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                getArms(it)
                                hideArms = false
                            }
                            .padding(horizontal = 10.dp),
                            text = it, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.padding(5.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 3.dp)
                    }
                }else{

                    Text(modifier = Modifier.padding(start = 10.dp),
                        text = "Arms", fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.padding(5.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable { hideArms = true }) {
                        Text(text = arms, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "ArrowDropDown", tint = Color.White)
                    }
                }

            }
        }

        if (takeAttendanceOrGoToFamily == ONBOARDED_STUDENT){
            SearchTopBar(
                value =value ,
                onValueChange = { onValueChange(it)},
                onSearchClick ={ onSearchClick()},
                label = "Enter Student Name Here"
            )
        }

        LazyColumn{
            items(student){
                val imageBitmap = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
                StudentCard(
                    student = it,
                    bitmap =imageBitmap.asImageBitmap() ,
                    onStudentClick = {navToFamily(it)})
            }
        }

    }
}
