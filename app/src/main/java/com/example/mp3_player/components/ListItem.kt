package com.example.mp3_player.components

import android.graphics.pdf.models.ListItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File

@Composable
fun ListItem(file: File, onClick: (File) -> Unit) {
    Column (
        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onClick(file) }
        ) {
        Text(file.name, fontSize = 18.sp)
    }
}