package com.example.proyectopoli.screens.fragments.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectopoli.R

@Composable
@Preview
fun InicioFragment() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(1.dp)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White,          // Blanco arriba
                    Color(0xFFFEFEFF),    // Azul claro en el centro
                    Color(0xFFFDFEFF)     // Azul muy claro casi blanco abajo
                )
            )
        ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally){
        Column(modifier = Modifier.weight(1.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ){
            Image(
            painter = painterResource(id = R.drawable.catsocial),
            contentDescription = "Gato con un teléfono",
            modifier = Modifier
                .height(300.dp)
                .width(200.dp)
        )}
        Column(modifier = Modifier.weight(1.5f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = stringResource(R.string.app_slogan),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(id = R.drawable.raster_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(75.dp)
                    .width(95.dp)
            )
            Text(
                text = stringResource(R.string.raster_string),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
    }