package com.stevdza.san.mongodemo.screenStudent.parentView

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.screenStudent.component.ParentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.onboardedParent.OnBoardedParentVM
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.StaffOnboardVM
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.SearchTopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun ParentListInParentView(onBackClick:()->Unit, navToDashboardScreen:(String)->Unit){

    val onBoardedParentVM: OnBoardedParentVM = viewModel()

    val data = onBoardedParentVM.parentData.value.sortedByDescending { it._id }
    Log.e(ContentValues.TAG, "ParentListInParentView: ${data.size}", )
    data.forEach {
//        val bit = it.pics?.let { it1 -> onBoardedParentVM.byteArrayToBitmap(it1) }
//        val bitSize = bit?.byteCount
//        val bitSizeInKB = bitSize?.div(1024.0)
//        Log.e(ContentValues.TAG, "OnboardedParent: $bitSizeInKB", )

//        if (bit != null){
//            val bitSize2 = bit.byteCount
//            val bitSizeInKB2 = bitSize2 / 1024.0
//            Log.e(ContentValues.TAG, "OnboardedParent: $bitSizeInKB2", )
//        }

        Log.e(TAG, "ParentListInParentView: ${it._id}", )
        Log.e(TAG, "ParentListInParentView: ${it.surname}", )
        Log.e(TAG, "ParentListInParentView: ${it.otherNames}", )

    }

    BackHandler() {
        onBackClick()
    }

    LaunchedEffect(key1 =onBoardedParentVM.surname.value ){
        if (onBoardedParentVM.surname.value.isEmpty()){
            onBoardedParentVM.getAllParent()
        }
    }



    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Cream)){

        item {
            Column() {
                TopBar(
                    onBackClick =onBackClick ,
                    title = "Parent")

                SearchTopBar(
                    value =onBoardedParentVM.surname.value ,
                    onValueChange = { onBoardedParentVM.surname.value=it},
                    onSearchClick ={ onBoardedParentVM.getParentWithName()},
                    label = "Enter Parent Name Here"
                )
            }

        }
        items(data){
            Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                val imageBitmap = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                if (imageBitmap != null) {
                    ParentCard(
                        parent = it,
                        bitmap = imageBitmap.asImageBitmap()
                        ) {
                        navToDashboardScreen(it._id.toHexString())
                    }
//                }
            }

        }
    }

}