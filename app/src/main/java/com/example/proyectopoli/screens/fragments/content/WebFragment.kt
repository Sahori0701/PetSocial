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
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.ArrowBack

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
            Column( // Main column
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section (Conecta and Pet Photo) - Occupies the full width
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Conecta",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
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

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    modifier = Modifier
                        .weight(0.30f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = {
                            Text(
                                "Buscar URL",
                                style = TextStyle(fontSize = 20.sp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(1f),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 15.sp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (searchQuery.isNotBlank()) {
                                        currentUrl = searchQuery.trim()
                                        if (!currentUrl.startsWith("http://") && !currentUrl.startsWith("https://")) {
                                            currentUrl = "https://$currentUrl"
                                        }
                                        showWebView = true
                                    }
                                },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.raster_logo),
                                    contentDescription = "Buscar",
                                    tint = Color(0xFF3B5BFE),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxWidth()
                        .fillMaxHeight(), // Make the Column take up all available vertical space
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(), // Make the Box fill the entire Column
                        contentAlignment = Alignment.TopCenter // Align the *content* of the Box to the top center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.web_animals),
                            contentDescription = "Background Image",
                            modifier = Modifier.fillMaxSize(), // Make the Image fill the entire Box
                            contentScale = ContentScale.Crop // Optional: Control how the image is scaled
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top, // Align items within this Column to the top
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.raster_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .height(75.dp) // Increase height to 75% of original
                                    .width(95.dp)  // Increase width to 75% of original
                            )
                            Text(
                                text = stringResource(R.string.raster_string),
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp), // Increase font size to 75% of original
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
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
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: ""
                                    view?.loadUrl(url)
                                    return true
                                }

                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: Bitmap?
                                ): Unit {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?): Unit {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: android.webkit.WebResourceError?
                                ): Unit {
                                    Log.e(
                                        "WebViewError",
                                        "Error al cargar la página: ${error?.description}"
                                    )
                                    // Aquí podrías mostrar un mensaje de error al usuario
                                    super.onReceivedError(view, request, error)
                                }
                            }
                            loadUrl(currentUrl)
                            webView = this
                        }
                    },
                    update = {
                        it.loadUrl(currentUrl)
                        webView = it
                    }
                )
// Botón para regresar con fondo transparente
                FloatingActionButton(
                    onClick = {
                        showWebView = false
                        currentUrl = "" // Reinicia la URL
                        searchQuery = "" // Reinicia el texto de búsqueda
                        webView?.loadUrl("about:blank") // Limpia el WebView
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp),
                    containerColor = Color.Black.copy(alpha = 0.05f), // Fondo negro con 30% de opacidad
                    contentColor = Color.Black // Color del icono
                ) {
                    Icon(Icons.Filled.ArrowBack, "Regresar") // Icono de regresar
                }
            }
        }
    }
}