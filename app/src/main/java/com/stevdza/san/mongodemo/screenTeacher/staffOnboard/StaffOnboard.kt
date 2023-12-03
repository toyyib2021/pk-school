package com.stevdza.san.mongodemo.screenTeacher.staffOnboard

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
import com.stevdza.san.mongodemo.model.StaffStatusType
import com.stevdza.san.mongodemo.screenStudent.component.BottomBar
import com.stevdza.san.mongodemo.screenStudent.component.StaffCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap

@Composable
fun StaffOnboard(onBackClick:()->Unit, navToStaffProfile:(String)->Unit){

    val onStaffBoardedVM: StaffOnboardVM = viewModel()

    val context = LocalContext.current
    val data = onStaffBoardedVM.teacherData.value.filter { it.teacherStatus?.status == StaffStatusType.ACTIVE.status }
    val activeStaff = data.sortedByDescending { it._id }
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

        Log.e(TAG, "OnboardedParent: id-> ${it._id.toHexString()}", )
        Log.e(TAG, "OnboardedParent: surname-> ${it.surname}", )
        Log.e(TAG, "OnboardedParent: otherNames-> ${it.otherNames}", )

    }

    LaunchedEffect(key1 =onStaffBoardedVM.surname.value ){
        if (onStaffBoardedVM.surname.value.isEmpty()){
            onStaffBoardedVM.getAllTeacher()
        }
    }



    BackHandler() {
        onBackClick()
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Cream)
    ){

            item {
                Column() {
                    TopBar(
                        onBackClick =onBackClick ,
                        title = "Staff's")

                    SearchTopBar(
                        value =onStaffBoardedVM.surname.value ,
                        onValueChange = { onStaffBoardedVM.surname.value=it},
                        onSearchClick ={ onStaffBoardedVM.getParentWithName()},
                        label = "Enter Parent Name Here"
                    )
                }

            }

            items(activeStaff){staff ->

                Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                    val imageBitmap = staff.pics.let { it1 -> convertBase64ToBitmap(it1) }
                    StaffCard(
                        staff = staff,
                        bitmap = imageBitmap.asImageBitmap(),
                        onStaffClick = {
                        navToStaffProfile(staff._id.toHexString())
//                            Log.e(TAG, "StaffOnboard: ${staff.surname} ${staff.otherNames}", )
                        }
                    )

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
    modifier: Modifier = Modifier
){
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {

        TextField(
            value =value,
            onValueChange = { onValueChange(it) },
            shape = RectangleShape,
            label = {
               Text(text = label)
            },
            modifier = Modifier.weight(9f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
               onSearchClick()
            }),
            trailingIcon = {
                Icon(modifier = Modifier.clickable { onSearchClick() },
                    imageVector = Icons.Default.Search, contentDescription ="Search" )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Cream
            )
        )

    }
}