package com.example.proyectopoli.screens.fragments.content

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.proyectopoli.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebFragment() {
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current // Obtener el contexto aquí

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Conecta",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .offset(y = 160.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (searchText.isNotBlank()) {
                            val query = Uri.encode(searchText)
                            val url = "https://www.google.com/search?q=$query"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent) // Usar context.startActivity
                        }
                    },

                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.magnifying_glass),
                        contentDescription = "Buscar",
                        tint = Color(0xFF3B5BFE),
                        modifier = Modifier.size(50.dp)

                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.raster_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(87.dp)
                            .width(100.dp)
                            .offset(y = 220.dp)
                    )
                    Text(
                        text = stringResource(R.string.raster_string),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 220.dp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.web_animals),
                    contentDescription = "Imagen de mascotas",
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .offset(y = 290.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }
    }
}
