package com.stevdza.san.mongodemo.screenStudent.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza.san.mongodemo.ui.theme.Blue

@Composable
fun TextFieldSigningInAndSignOutText(
    value: String,
    label: String,
    onValueChange: (String)->Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier

){
    Card(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(5.dp)) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            value = value,
            onValueChange = { onValueChange(it) },
            leadingIcon = {
                Icon(imageVector =imageVector , contentDescription = "", tint = Color.Black)
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, cursorColor = Color.Black,
                backgroundColor = Color.White, unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Blue, unfocusedLabelColor = Color.Black
            ),
            label = {
                Text(text = label, color = Color.Black)
            }
        )
    }




}

@Composable
fun TextFieldForm(
    value: String,
    label: String,
    onValueChange: (String)->Unit,
    modifier: Modifier = Modifier,
    labelColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    cardBackgroundColor: Color = Color.White,
    cursorColor: Color = Color.Black

){
    Column() {
        Text( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
            text = label, fontSize =14.sp, color = labelColor)
        Card(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(15.dp),
            backgroundColor = cardBackgroundColor
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                value = value,
                onValueChange = { onValueChange(it) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, cursorColor = cursorColor,
                    backgroundColor = backgroundColor, unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Blue, unfocusedLabelColor = Color.Black
                ),
            )
        }
    }





}

@Composable
fun TextFieldPassword(
    password:String,
    label:String,
    onPasswordChange: (String)->Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
){
    var confirmPasswordVisibility by remember { mutableStateOf(true) }
    Card(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(5.dp)) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            value = password, onValueChange = { onPasswordChange(it) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, cursorColor = Color.Black,
                backgroundColor = Color.White, unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Blue, unfocusedLabelColor = Color.Black
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password",
                    tint = Color.Black)
            },
            label = {
                Text(text = label, color = Color.Black)
            },
            trailingIcon = {
                IconButton(onClick = {
                    confirmPasswordVisibility = !confirmPasswordVisibility
                }) {
                    Icon(
                        imageVector = if (confirmPasswordVisibility) Icons.Filled.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisibility)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)

        )
    }

}