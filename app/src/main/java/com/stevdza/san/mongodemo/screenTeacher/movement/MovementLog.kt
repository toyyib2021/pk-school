package com.stevdza.san.mongodemo.screenTeacher.movement

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
import com.stevdza.san.mongodemo.screenStudent.component.StaffAbsentCard
import com.stevdza.san.mongodemo.screenStudent.component.StaffCard
import com.stevdza.san.mongodemo.screenStudent.component.TextFieldForm
import com.stevdza.san.mongodemo.screenStudent.component.TopBarWithDate
import com.stevdza.san.mongodemo.screenStudent.onboardedParent.SearchTopBar
import com.stevdza.san.mongodemo.screenTeacher.scanCard.StaffScanId
import com.stevdza.san.mongodemo.ui.theme.Cream
import com.stevdza.san.mongodemo.util.conversion.convertBase64ToBitmap
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovementLog(
    onBackClick:()->Unit
){
    val movementVM: MovementVM = viewModel()
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
    val context = LocalContext.current
    val staffMovementData = movementVM.staffMovementData.value.filter { it.date == formattedDate }
    val er = staffMovementData.reversed()
    Log.e(TAG, "MovementLog: staffMovementData-> ${staffMovementData.size}", )
    val staff = movementVM.teacherData.value
    val accountTypeKey: AccountTypeKey = AccountTypeKey(context)
    val accountType = accountTypeKey.getKey.collectAsState(initial = "")
    var scanIdState by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    BackHandler() {onBackClick()}

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Cream)
    ) {

        TopBarWithDate(
            onBackClick = { onBackClick() }, title = "Movement Log",
            date = formattedDate, ondateClick = {}
        )
        SearchTopBar(value = movementVM.searchBarText.value,
            onValueChange = { movementVM.searchBarText.value = it },
            onSearchClick = { /*TODO*/ }, label = "staff name")

        LazyColumn{
            items(er){movement->
                val st = staff.find { it.staffId == movement.staffId }
                if (st != null) {
                    val bit = convertBase64ToBitmap(st.pics)
                    StaffAbsentCard(
                        staff = st,
                        bitmap = bit.asImageBitmap(),
                        reasonForAbsent = movement.reason,
                        absentDate = "${movement.timeIn} ${movement.timeOut}",
                        delete = { /*TODO*/ },
                        edit = { /*TODO*/ },
                        accountType = accountType.value
                    )
                }
            }


        }

    }

}




