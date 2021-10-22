package com.otorresd.ram.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.otorresd.ram.model.SettingsViewModel

@Composable
fun Settings(settingsViewModel: SettingsViewModel = viewModel()){
    val checkedState by settingsViewModel.isDarkMode.collectAsState(isSystemInDarkTheme())
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
            horizontalArrangement= Arrangement.SpaceAround){
            Text("Dark mode", fontSize = 18.sp)
            Switch(
                checked = checkedState,
                onCheckedChange = {
                    settingsViewModel.setDarkMode(it)
                                  }
            )
        }
    }
}