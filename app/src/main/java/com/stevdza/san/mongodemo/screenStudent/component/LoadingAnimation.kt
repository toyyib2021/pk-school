package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza.san.mongodemo.ui.theme.Cream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    cornerRadius: Dp = 16.dp,
    progressIndicatorColor: Color = Color(0xFF35898f),
    progressIndicatorSize: Dp = 80.dp,
    viewModel: LoadingVM,


) {
//    val viewModel: MyViewModel = viewModel()



    val showDialog by viewModel.open.observeAsState(initial = false) // initially, don't show the dialog

    if (showDialog) {
        Column(modifier = Modifier.fillMaxSize().background(Cream)) {
            AlertDialog(
                onDismissRequest = {
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false // disable the default size so that we can customize it
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 42.dp, end = 42.dp) // margin
                        .background(color = Cream, shape = RoundedCornerShape(cornerRadius))
                        .padding(top = 36.dp, bottom = 36.dp), // inner padding
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ProgressIndicatorLoading(
                        progressIndicatorSize = progressIndicatorSize,
                        progressIndicatorColor = progressIndicatorColor
                    )

                    // Gap between progress indicator and text
                    Spacer(modifier = Modifier.height(32.dp))

                    // Please wait text
                    Text(
                        text = "Please wait...",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }

    }

//    Button(
//        onClick = {
//            viewModel.open.value = true
//            viewModel.startThread()
//        }
//    ) {
//        Text(text = "Show Loading Dialog")
//    }
}




@Composable
private fun ProgressIndicatorLoading(
    progressIndicatorSize: Dp,
    progressIndicatorColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}




class LoadingVM : ViewModel() {
    var open = MutableLiveData<Boolean>()

    fun startThread(backgroundTask:()->Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // Do the background work here
                // I'm adding delay
//                delay(3000)
                backgroundTask()
            }

//            closeDialog()
        }
    }

     fun closeDialog() {
        open.value = false
    }
}

