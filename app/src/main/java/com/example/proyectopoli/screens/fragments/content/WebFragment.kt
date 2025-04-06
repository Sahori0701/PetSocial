package com.example.proyectopoli.screens.fragments.content

import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.proyectopoli.R
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebFragment(mascotaPreferences: MascotaPreferences) {
    val context = LocalContext.current
    var mascotaUri by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var currentUrl by remember { mutableStateOf("") }
    var webView: WebView? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(false) }
    var expandedMenu by remember { mutableStateOf(false) }
    var fullscreenMode by remember { mutableStateOf(false) }

    var mascota by remember { mutableStateOf(MascotaPerfil()) }
    var nuevaUri by remember { mutableStateOf<Uri?>(null) }

    val paginasPreferidas = listOf(
        "https://puppis.com.ar" to "Puppis",
        "https://royalcanin.com" to "Royal Canin",
        "https://purina.com" to "Purina",
        "https://petngo.com.mx" to "Pet n' Go",
        "https://hillspet.com" to "Hill's Pet"
    )

    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect { mascota = it }
        mascotaPreferences.mascotaFlow.collect {
            mascotaUri = it.fotoUri?.toString() ?: ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = !fullscreenMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
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
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.2f)
                        .background(Color(0xFFF0F7FF)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {




                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "https://sitiosweb.com",
                                    fontSize = 14.sp
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3B5BFE),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF3B5BFE)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                        )

                        Button(
                            onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    val url = if (searchQuery.startsWith("http://") || searchQuery.startsWith("https://")) {
                                        searchQuery
                                    } else {
                                        "https://$searchQuery"
                                    }
                                    currentUrl = url
                                    webView?.loadUrl(url)
                                    fullscreenMode = true
                                }
                            },
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B5BFE)
                            ),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text(
                                "Buscar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }

                        Box {
                            IconButton(onClick = { expandedMenu = true }) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Mostrar páginas preferidas",
                                    tint = Color(0xFF3B5BFE)
                                )
                            }

                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false },
                                modifier = Modifier
                                    .width(250.dp)
                                    .background(Color.White)
                            ) {
                                paginasPreferidas.forEach { (url, nombre) ->
                                    DropdownMenuItem(
                                        text = { Text(nombre) },
                                        onClick = {
                                            currentUrl = url
                                            searchQuery = url
                                            webView?.loadUrl(url)
                                            expandedMenu = false
                                            fullscreenMode = true
                                        }
                                    )
                                }
                            }
                        }
                    }




                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.raster_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(87.dp)
                            .width(100.dp)
                    )
                    Text(
                        text = stringResource(R.string.raster_string),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }


            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (fullscreenMode) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    request?.url?.toString()?.let {
                                        currentUrl = it
                                    }
                                    return false
                                }

                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: Bitmap?
                                ) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    url?.let { currentUrl = it }
                                }
                            }
                            webChromeClient = WebChromeClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.setSupportZoom(true)
                            if (currentUrl.isNotEmpty()) loadUrl(currentUrl)
                            webView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF3F51B5)
                    )
                }
            } else {
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }

        if (!isLoading && currentUrl == "https://puppis.com.ar" && !fullscreenMode) {
            Image(
                painter = painterResource(id = R.drawable.raster_logo),
                contentDescription = "Collage de mascotas",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }

}