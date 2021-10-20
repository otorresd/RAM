package com.otorresd.ram.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.otorresd.ram.ui.theme.Background
import com.otorresd.ram.ui.theme.TextOrange

@Preview
@Composable
fun Settings(){
    val checkedState = remember { mutableStateOf(true) }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Background),) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
            horizontalArrangement= Arrangement.SpaceAround){
            Text("Dark mode", fontSize = 18.sp, color = Color.White)
            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it },
                colors = SwitchDefaults.colors(checkedThumbColor = TextOrange)
            )
        }
    }
}