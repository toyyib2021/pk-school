package com.stevdza.san.mongodemo.screenStudent.parentView

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevdza.san.mongodemo.model.Parent
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.screenStudent.component.AssociatedParentCardEdit
import com.stevdza.san.mongodemo.screenStudent.component.ParentCardEdit
import com.stevdza.san.mongodemo.screenStudent.component.StudentCard
import com.stevdza.san.mongodemo.screenStudent.component.TopBar
import com.stevdza.san.mongodemo.screenStudent.family.FamilyVM
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FamilyParentView(familyID: String,
           navToHome:()->Unit,

){
    val familyVM: FamilyVM = viewModel()
    val context = LocalContext.current
    familyVM.objectID.value = familyID


    familyVM.associateParentData.value.forEach {
        Log.e(TAG, "Family: ${it.familyId}", )
        Log.e(TAG, "Family: ${it.surname}", )
        Log.e(TAG, "Family: ${it.otherNames}", )
    }

    if(familyVM.parentData.value.isNotEmpty()){
        familyVM.getParentWithID()
    }

    if(familyVM.childrenData.value.isNotEmpty()){
        familyVM.getChildrenWIthFamilyID()
    }

    if(familyVM.associateParentData.value.isNotEmpty()){
        familyVM.getAssociateParentWithFamilyID()
    }


    val scope = rememberCoroutineScope()
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    familyVM.date.value = formattedDate

     Column(modifier = Modifier
         .fillMaxSize()
         .background(Cream)){

         LazyColumn(modifier = Modifier.weight(9f)){
             item{
                 Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                     val oneParent = familyVM.oneParentData.value
                     val bitmap = oneParent?.let { it.pics.let { it1 -> convertBase64ToBitmap(it1) } }
                     if (oneParent != null) {
                         if (bitmap != null) {
                             ParentDetails(
                                 parent = oneParent,
                                 navToEditParentDetails = {},
                                 navToHome = {navToHome()},
                                 bitmap = bitmap.asImageBitmap()
                             )
                         }

                     }
                 }

             }

             if(familyVM.childrenData.value.isNotEmpty()){
                 item{
                     StudentDetails(
                         familyVM = familyVM,
                         onEditStudentProfileClick={},
                         onStudentStatusClick ={}
                     )
                 }
             }

             item{
                 Spacer(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(10.dp)
                 )
                 Text(modifier = Modifier
                     .fillMaxWidth()
                     .padding(start = 20.dp),
                     text = "Parent Associate", fontSize = 14.sp, fontWeight = FontWeight.Bold)
             }
             if(familyVM.associateParentData.value.isNotEmpty()){
                 items(familyVM.associateParentData.value){
                     val bitmapAssociateParent = it.pics.let { it1 -> convertBase64ToBitmap(it1) }
//                     if (bitmapAssociateParent != null) {
                         AssociatedParentCardEdit(
                             associateParent = it,
                             bitmap = bitmapAssociateParent.asImageBitmap(),
                             onParentClick = {}
                         )
//                     }
                 }
             }

         }
      }

}

@Composable
private fun StudentDetails(
    familyVM: FamilyVM,
    onEditStudentProfileClick:(StudentData)->Unit,
    onStudentStatusClick:(StudentData)->Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        LazyRow() {
            items(familyVM.childrenData.value) {
                val bitmapImage = convertBase64ToBitmap(it.pics)
//                if (bitmapImage != null) {

//                    val sizeInBytes = bitmapImage.byteCount
//                    val sizeInKB = sizeInBytes / 1024.0
//                    Log.e(TAG, "StudentDetails: $sizeInKB", )
                    StudentCard(
                        bitmap = bitmapImage.asImageBitmap(),
                        student = it,
                        onEditStudentProfileClick = { onEditStudentProfileClick(it) },
                        onStudentStatusClick = {onStudentStatusClick(it)}
                    )
//                }
            }
        }




    }
}


@Composable
fun ParentDetails (
    parent:Parent,
    navToEditParentDetails:()->Unit,
    navToHome:()->Unit,
    bitmap: ImageBitmap
){

    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        TopBar(onBackClick = { navToHome() }, title = parent.surname)

        ParentCardEdit(
            parent = parent,
            bitmap = bitmap,
            onParentClick = { navToEditParentDetails() }
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        text = "Student", fontSize = 14.sp, fontWeight = FontWeight.Bold)

    }
}


