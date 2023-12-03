package com.stevdza.san.mongodemo.screenStudent.onboardedParent

import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.screenStudent.component.ParentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenTeacher.staffOnboard.StaffOnboardVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun OnboardedParent(onBackClick:()->Unit, navToFamilyScreen:(String)->Unit){

    val onBoardedParentVM: OnBoardedParentVM = viewModel()

    val context = LocalContext.current
    val data = onBoardedParentVM.parentData.value.sortedByDescending { it._id }
    val data2 = onBoardedParentVM.parentData2.value.sortedByDescending { it._id }
    Log.e(TAG, "OnboardedParent: ${data.size}", )
    data.forEach {
//        val bit = it.pics?.let { it1 -> onBoardedParentVM.byteArrayToBitmap(it1) }
//        val bitSize = bit?.byteCount
//        val bitSizeInKB = bitSize?.div(1024.0)
//        Log.e(TAG, "OnboardedParent: bitSizeInKB-> $bitSizeInKB", )

//        if (bit != null){
//            val bitSize2 = bit.byteCount
//            val bitSizeInKB2 = bitSize2 / 1024.0
//            Log.e(TAG, "OnboardedParent: bitSizeInKB2-> $bitSizeInKB2", )
//        }

        Log.e(TAG, "OnboardedParent: id-> ${it._id}", )
        Log.e(TAG, "OnboardedParent: surname-> ${it.surname}", )
        Log.e(TAG, "OnboardedParent: otherNames-> ${it.otherNames}", )

    }



    BackHandler() {
        onBackClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column() {
            TopBar(
                onBackClick =onBackClick ,
                title = "Parent")

            com.stevdza.san.mongodemo.screenTeacher.staffOnboard.SearchTopBar(
                value = onBoardedParentVM.surname.value,
                onValueChange = { onBoardedParentVM.surname.value = it },
                onSearchClick = { onBoardedParentVM.getParentWithName() },
                label = "Enter Parent Name Here"
            )
        }

        LazyColumn(
        ){

            if (data2.isNotEmpty()){

                items(data2){
                    Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                        val imageBitmap = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
//               if (imageBitmap != null) {
                        ParentCard(
                            parent = it,
                            bitmap = imageBitmap.asImageBitmap()
                        ) {
                            navToFamilyScreen(it._id.toHexString())
                        }
//              }

                    }

                }
            }else{
                items(data){

                    Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                        val imageBitmap = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
//               if (imageBitmap != null) {
                        ParentCard(
                            parent = it,
                            bitmap = imageBitmap.asImageBitmap()
                        ) {
                            navToFamilyScreen(it._id.toHexString())
                            Log.e(TAG, "OnboardedParent: ${it.surname} ${it.otherNames}" )
                            Log.e(TAG, "OnboardedParent: ${it._id.toHexString()}", )
                        }
//              }

                    }

                }
            }




        }


    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    value:String,
    onValueChange:(String)->Unit,
    onSearchClick:()->Unit,
    label:String,
    color: Color = Color.Black,
    iconColor: Color = Color.Black

){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
    ) {
        TextField(
            value =value,
            onValueChange = { onValueChange(it) },
            shape = RectangleShape,
            label = {
                Text(text = label, color = color)
            },
            modifier = Modifier.weight(9f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchClick()
            }),
            trailingIcon = {
                Icon(modifier = Modifier.clickable { onSearchClick() },
                    imageVector = Icons.Default.Search, contentDescription ="Search", tint = iconColor )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Cream
            )
        )

    }
}