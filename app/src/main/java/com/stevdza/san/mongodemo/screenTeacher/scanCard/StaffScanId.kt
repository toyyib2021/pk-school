package com.stevdza.san.mongodemo.screenTeacher.scanCard

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.StaffStatusType
import com.stevdza.san.mongodemo.screenStudent.QrCodeAnalyzer
import com.stevdza.san.mongodemo.screenStudent.component.ParentCard
import com.stevdza.san.mongodemo.screenStudent.component.StaffCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenTeacher.dashboard.StaffDashboardVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StaffScanId(
    navToAttendanceScreen:(String)->Unit,
    onBackClick:()->Unit
){
    val familyVM: StaffDashboardVM = viewModel()


    var code by remember { mutableStateOf("") }
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

    LaunchedEffect(key1 = code){
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

        if (staffId != "error"){
            val staffList = familyVM.teacherData.value
            val staff = staffList.find { it.staffId == staffId}

            staff?.let{ staffObject->
                if ( staffObject.teacherStatus?.status == StaffStatusType.ACTIVE.status){
                    val bit = staffObject.pics.let { it1 -> convertBase64ToBitmap(it1) }
                    StaffCard(staff = staffObject, bitmap = bit.asImageBitmap(), onStaffClick = {navToAttendanceScreen(staffObject._id.toHexString())})
                }else{
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                            .background(Cream),
                        text = "Card Deactivated",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, textAlign = TextAlign.Center
                    )
                }


            }


            Log.e(ContentValues.TAG, "ScanId: $staff")

        }else{

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .background(Cream),
                text = "Error Reading Card, Try Another Card",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp, textAlign = TextAlign.Center
            )
        }



        Log.e(ContentValues.TAG, "ScanId: $code", )
//            Text(text = "Student ID $code", fontWeight = FontWeight.Bold, fontSize = 16.sp)

    }
}

