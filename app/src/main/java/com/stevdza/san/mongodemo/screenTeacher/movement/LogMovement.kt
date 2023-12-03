package com.stevdza.san.mongodemo.screenTeacher.movement

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.dataStore.AccountTypeKey
import com.stevdza.san.mongodemo.screenStudent.component.BtnNormal
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.stevdza.san.mongodemo.model.Movement
import com.stevdza.san.mongodemo.screenStudent.QrCodeAnalyzer
import com.stevdza.san.mongodemo.screenStudent.component.ParentCard
import com.stevdza.san.mongodemo.screenStudent.component.StaffCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.family.FamilyVM
import com.stevdza.san.mongodemo.screenTeacher.dashboard.StaffDashboardVM
import com.stevdza.san.pk.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun MovementScanId(
    navToAttendanceScreen:(String)->Unit,
    navToStaffDasboard:()->Unit,
    onBackClick:()->Unit
) {
    val familyVM: StaffDashboardVM = viewModel()
    val movementVM: MovementVM = viewModel()
    var code by remember { mutableStateOf("") }

    val currentDate = LocalDate.now()
    val currentTime = LocalDateTime.now()
    val timeFormat = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    movementVM.date.value = formattedDate
    movementVM.timeIn.value = timeFormat
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }


    var staffId by remember { mutableStateOf("") }

    LaunchedEffect(key1 = code) {
        if (code.isNotEmpty()) {

            val staffList = familyVM.teacherData.value
            val listOfFamilyId = staffList.map { it.staffId }
            staffId = listOfFamilyId.find { it == code } ?: "error"
        }
    }

    BackHandler() {
        onBackClick()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(onBackClick = { onBackClick() }, title = "Scan Card")
        if (hasCamPermission) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(
                            Size(
                                previewView.width,
                                previewView.height
                            )
                        )
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QrCodeAnalyzer { result ->
                            code = result
                        }
                    )
                    try {
                        cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                },
                modifier = Modifier.weight(1f)
            )
        }

        if (staffId != "error") {
            val staffList = familyVM.teacherData.value
            val staff = staffList.find { it.staffId == staffId }
            val staffMovement = movementVM.staffMovementData.value.filter {
                it.staffId == staffId && it.date == formattedDate
            }

            val lastStaffMovement = staffMovement.lastOrNull()
            var timeOutState by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
            ) {

                staff?.let {
                    val bit = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
                    movementVM.staffId.value = it.staffId
                    StaffCard(staff = it, bitmap = bit.asImageBitmap(),
                        onStaffClick = { navToAttendanceScreen(it._id.toHexString()) })
                    Spacer(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    )


                    if (lastStaffMovement == null) {
                        TextFieldForm(
                            value = movementVM.reason.value, label = "Reason",
                            onValueChange = { movementVM.reason.value = it },
                            labelColor = Color.White,
                            cardBackgroundColor = Cream,
                            backgroundColor = Cream
                        )
                    } else {
                        if (timeOutState == "") {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center) {

                                Text(
                                    text = "Welcome Back", fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp, color = Color.White
                                )
                                Image(painter = painterResource(id = R.drawable.welcome), contentDescription ="" , modifier = Modifier.size(50.dp))
                            }
                        } else {
                            TextFieldForm(
                                value = movementVM.reason.value, label = "Reason",
                                onValueChange = { movementVM.reason.value = it },
                                labelColor = Color.White,
                                cardBackgroundColor = Cream,
                                backgroundColor = Cream
                            )


                        }

                    }


                    Spacer(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    )

                    lastStaffMovement?.let {
                        timeOutState = it.timeOut
                        movementVM.objectId.value = it._id.toHexString()
                    }
                    if (lastStaffMovement == null) {
                        BtnNormal(
                            onclick = {
                                movementVM.insertStaffMovement(
                                    onError = {}, onSuccess = { navToStaffDasboard() },
                                    emptyFeilds = {
                                        Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
                                    })

                            },
                            text = "Done"
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                    } else {
                        if (timeOutState == "") {
                            movementVM.timeOut.value = timeFormat

                            BtnNormal(
                                onclick = {
                                    movementVM.updateStaffMovement(
                                        onError = {}, onSuccess = { navToStaffDasboard() })
                                },
                                text = "Updt"
                            )
                            Spacer(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            )

                        } else {
                            BtnNormal(
                                onclick = {
                                    movementVM.insertStaffMovement(
                                        onError = {}, onSuccess = { navToStaffDasboard() },
                                        emptyFeilds = {
                                            Toast.makeText(context, "empty", Toast.LENGTH_SHORT)
                                                .show()
                                        })
                                },
                                text = "Done"
                            )
                            Spacer(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            )

                        }

                    }


                }

            }

        } else {

            androidx.compose.material.Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .background(Cream),
                text = "Error Reading Card, Try Another Card",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp, textAlign = TextAlign.Center
            )
        }



        Log.e(ContentValues.TAG, "ScanId: $code",)
//            Text(text = "Student ID $code", fontWeight = FontWeight.Bold, fontSize = 16.sp)

    }
}








