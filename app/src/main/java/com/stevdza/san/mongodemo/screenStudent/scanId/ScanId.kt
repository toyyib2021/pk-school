package com.stevdza.san.mongodemo.screenStudent.scanId

import androidx.compose.runtime.Composable
import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.stevdza.san.mongodemo.screenStudent.QrCodeAnalyzer
import com.stevdza.san.mongodemo.screenStudent.component.ParentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.family.FamilyVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun ScanId(
    navToAttendanceScreen:(String)->Unit,
    onBackClick:()->Unit
){
    val familyVM: FamilyVM = viewModel()
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


    LaunchedEffect(key1 = code){
        if (code.isNotEmpty()){

            val parent = familyVM.parentData.value
            val listOfFamilyId = parent.map { it._id.toHexString() }
            val validOrInvalidId = listOfFamilyId.find { it == code }
            if (validOrInvalidId != null){
                familyVM.objectID.value = validOrInvalidId
                familyVM.getParentWithID()
            }else{
                familyVM.objectID.value = "error"
            }

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
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
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

        if (familyVM.objectID.value != "error"){

            familyVM.oneParentData.value?.let{
                    val bit = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                    if (bit != null) {
                    ParentCard(
                        parent = it,
                            bitmap = bit.asImageBitmap()
                    ) {
                        navToAttendanceScreen(it._id.toHexString())
                    }
//                    }
                }

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



        Log.e(TAG, "ScanId: ${familyVM.oneParentData.value}")
        Log.e(TAG, "ScanId: $code", )
//            Text(text = "Student ID $code", fontWeight = FontWeight.Bold, fontSize = 16.sp)

    }
}