package com.example.proyectopoli.screens.fragments.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectopoli.R

@Composable
fun InicioFragment() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(id = R.drawable.catsocial), // Reemplaza con tu imagen
                contentDescription = "Logo de la App",
                modifier = Modifier
                    .size(420.dp) // Tamaño de la imagen
                    .padding(bottom = 16.dp) // Espaciado con el texto
            )
            Text(
            text = stringResource(R.string.app_slogan),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )}

    }
}