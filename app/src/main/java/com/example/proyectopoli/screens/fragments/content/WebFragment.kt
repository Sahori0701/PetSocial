package com.example.proyectopoli.screens.fragments.content

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.proyectopoli.R
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebFragment(mascotaPreferences: MascotaPreferences) {
    val context = LocalContext.current
    var mascotaUri by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var currentUrl by remember { mutableStateOf("") }
    var webView: WebView? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(false) }
    var expandedMenu by remember { mutableStateOf(false) }
    var fullscreenMode by rememberSaveable { mutableStateOf(false) }
    var mascota by remember { mutableStateOf(MascotaPerfil()) }
    var showWebView by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect { mascota = it }
        mascotaPreferences.mascotaFlow.collect {
            mascotaUri = it.fotoUri?.toString() ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = !fullscreenMode && !showWebView,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column( // Columna dentro de AnimatedVisibility
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column( // Columna para "Conecta" y foto
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Conecta",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = rememberAsyncImagePainter(mascota.fotoUri),
                            contentDescription = "Foto de Mascota",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .border(BorderStroke(2.dp, Color.White), CircleShape)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.conecta),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .offset(y = (-50).dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    val query = Uri.encode(searchQuery)
                                    val url = "https://www.google.com/search?q=$query"
                                    currentUrl = url
                                    showWebView = true
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
                                    .offset(y = (-30).dp)
                            )
                            Text(
                                text = stringResource(R.string.raster_string),
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = (-30).dp)
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.web_animals),
                            contentDescription = "Imagen de mascotas",
                            modifier = Modifier
                                .height(400.dp)
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .offset(y = (1).dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
            }
        }

        // WebView integrado
        AnimatedVisibility(
            visible = showWebView,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = {
                        WebView(it).apply {
                            settings.javaScriptEnabled = true
                            webViewClient = WebViewClient()
                            loadUrl(currentUrl)
                            webView = this
                        }
                    },
                    update = {
                        it.loadUrl(currentUrl)
                        webView = it
                    }
                )
                // Botón para regresar
                FloatingActionButton(
                    onClick = { showWebView = false },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, "Regresar")
                }
            }
        }
    }
}