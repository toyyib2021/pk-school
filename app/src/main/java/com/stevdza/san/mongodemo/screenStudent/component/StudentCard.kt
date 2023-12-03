package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.model.StudentData
import com.stevdza.san.mongodemo.ui.theme.Cream

@Composable
fun StudentCard(
    bitmap: ImageBitmap,
    student: StudentData,
    onEditStudentProfileClick:()->Unit,
    onStudentStatusClick:()->Unit
) {
    var hide by remember { mutableStateOf(false) }

        Card(modifier = Modifier
            .clickable { hide = !hide }
            .size(width = 200.dp, height = 300.dp)
            .padding(10.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = Cream,
                contentColor = Color.Black
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            if (hide) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(state = rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .clickable { onEditStudentProfileClick() }
                            .fillMaxWidth(),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp))
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)) {
                        // Names
                        Text(text = "Name:" , fontSize = 10.sp)
                        Text(text = "${student.surname} ${student.otherNames}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Black)
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp))

                        // Student ID
                        Text(text = "Student ID:" , fontSize = 10.sp)
                        Text(text = student.studentApplicationID, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Black)
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp))

                        // Class
                        Text(text = "Class:" , fontSize = 10.sp)
                        Text(text = student.cless, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Black)
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp))

                        // Date of Birth
                        Text(text = "Date of Birth:" , fontSize = 10.sp)
                        Text(text = student.datOfBirth, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Black)


                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Card(
                            modifier = Modifier
                                .clickable { onStudentStatusClick() },
                            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth()
                                    .background(Color.White),
                                text = student.studentStatus?.status ?: "",
                                textAlign = TextAlign.Center, fontSize = 12.sp
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(4f)
                    ){
                        Card(
                            modifier = Modifier
                                .padding(5.dp),
                            shape = CircleShape
                        ) {
//                            Image(painter = painterResource(id = R.drawable.empty_image), contentDescription = "blank")
                            Image( modifier = Modifier
                                .size(100.dp), bitmap = bitmap, contentDescription = "", contentScale = ContentScale.FillWidth)
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                            colors = CardDefaults.cardColors(containerColor = Cream)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 7.dp, vertical = 5.dp)
                                        .fillMaxWidth(),
                                    text = "${student.surname} ${student.otherNames}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            text = "${student.cless} ", fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            text = "${student.arms} ", fontSize = 14.sp
                        )


                    }

                }
            }

        }


}
