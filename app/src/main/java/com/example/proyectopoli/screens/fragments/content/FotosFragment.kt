package com.example.proyectopoli.screens.fragments.content

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import coil.compose.rememberAsyncImagePainter
import com.example.proyectopoli.R
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.*
import java.io.InputStream
import android.content.ContentResolver
import android.content.Intent
import android.provider.MediaStore
import java.io.FileOutputStream
import kotlinx.coroutines.delay

data class FotoItem(
    val uri: Uri,
    val descripcion: String
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FotosFragment(mascotaPreferences: MascotaPreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mascota by remember { mutableStateOf(MascotaPerfil()) }

    // Estado para la URI de la foto temporal
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar datos desde DataStore
    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect { mascota = it }
    }

    var fotos by remember { mutableStateOf(loadFotos(context)) }
    var selectedFotoIndex by remember { mutableStateOf<Int?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var descripcionTemporal by remember { mutableStateOf("") }
    var showFotoSourceDialog by remember { mutableStateOf(false) }

    // Permisos de cámara
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Seleccionar imagen de la galería
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                tempPhotoUri = it
                showAddDialog = true
            }
        }

    // Tomar foto con la cámara - Simplificando usando TakePicture contract
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            showAddDialog = true
        } else {
            Toast.makeText(context, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
            tempPhotoUri = null
        }
    }

    if (showAddDialog && tempPhotoUri != null) {
        AlertDialog(
            containerColor = Color.White.copy(alpha = 0.7f),
            onDismissRequest = {
                showAddDialog = false
                tempPhotoUri = null
                descripcionTemporal = ""
            },
            title = {
                Text(
                    "Descripción",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            },
            text = {
                TextField(
                    value = descripcionTemporal,
                    onValueChange = { descripcionTemporal = it },
                    label = {
                        Text(
                            "Descripción de la foto",
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.7f)),
                    textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Serif)
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .padding(1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            tempPhotoUri?.let { uri ->
                                fotos = fotos + FotoItem(uri, descripcionTemporal)
                                saveFotos(context, fotos)
                                Toast.makeText(context, "Foto agregada", Toast.LENGTH_SHORT).show()
                                showAddDialog = false
                                tempPhotoUri = null
                                descripcionTemporal = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B5BFE).copy(alpha = 0.8f),
                            contentColor = Color(0xFF3B5BFE).copy(alpha = 0.8f)
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Guardar",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            },
            dismissButton = {
                Column(modifier = Modifier
                    .padding(1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            showAddDialog = false
                            tempPhotoUri = null
                            descripcionTemporal = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B5BFE).copy(alpha = 0.8f),
                            contentColor = Color(0xFF3B5BFE).copy(alpha = 0.8f)
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Cancelar",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        )
    }

    if (selectedFotoIndex != null) {
        FullScreenPhotoViewer(
            fotos = fotos,
            initialIndex = selectedFotoIndex!!,
            onClose = { selectedFotoIndex = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFFEFEFF),
                            Color(0xFFFDFEFF)
                        )
                    )
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fotos",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    contentAlignment = Alignment.Center
                ) {
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
                    text = stringResource(R.string.fotos),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(0.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.4f)
                    .background(Color(0xFFF0F7FF)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 5.dp,
                        end = 10.dp,
                        bottom = 5.dp
                    )
                ) {
                    itemsIndexed(fotos) { index, item ->
                        FotoThumbnail(
                            fotoItem = item,
                            onClick = { selectedFotoIndex = index },
                            onDelete = {
                                fotos = fotos.filterIndexed { i, _ -> i != index }
                                saveFotos(context, fotos)
                            }
                        )
                    }
                    item {
                        AddFotoButton {
                            showFotoSourceDialog = true
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

    // Diálogo para seleccionar el origen de la foto (galería o cámara)
    if (showFotoSourceDialog) {
        AlertDialog(
            containerColor = Color.White.copy(alpha = 0.6f),
            onDismissRequest = { showFotoSourceDialog = false },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Fotos",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .padding(1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showFotoSourceDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B5BFE).copy(alpha = 0.8f),
                            contentColor = Color(0xFF3B5BFE).copy(alpha = 0.8f)
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Galería",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Button(
                        onClick = {
                            // Crear un archivo temporal para la foto
                            val photoFile = File(
                                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "foto_mascota_${System.currentTimeMillis()}.jpg"
                            )

                            // Conseguir una URI para este archivo
                            val photoUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                photoFile
                            )

                            // Guardar la URI para usarla después
                            tempPhotoUri = photoUri

                            // Lanzar la cámara con esta URI
                            cameraLauncher.launch(photoUri)
                            showFotoSourceDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B5BFE).copy(alpha = 0.8f),
                            contentColor = Color(0xFF3B5BFE).copy(alpha = 0.8f)
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Cámara",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun FotoThumbnail(fotoItem: FotoItem, onClick: () -> Unit, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
            .border(2.dp, Color(0xFFF0F7FF))
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(fotoItem.uri),
            contentDescription = "Foto de la mascota",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = { onDelete() },
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.TopEnd)
                .offset(
                    x = (8).dp,
                    y = -8.dp
                )
                .background(Color(0xFFF0F7FF), CircleShape)
                .border(BorderStroke(1.dp, Color(0xFFF0F7FF)), CircleShape)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AddFotoButton(onAddFoto: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
            .border(2.dp, Color(0xFFF0F7FF))
            .clickable { onAddFoto() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Agregar Foto",
            tint = Color(0xFF3B5BFE),
            modifier = Modifier.size(36.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenPhotoViewer(fotos: List<FotoItem>, initialIndex: Int, onClose: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { fotos.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(fotos[page].uri),
                    contentDescription = "Foto en pantalla completa",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = fotos[page].descripcion,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        IconButton(
            onClick = { onClose() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.8f), shape = CircleShape)
                .size(45.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }

        Text(
            text = "${pagerState.currentPage + 1}/${fotos.size}",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.8f), shape = CircleShape)
                .padding(8.dp)
        )
    }
}

// Guardar y cargar fotos en almacenamiento interno
fun saveFotos(context: Context, fotos: List<FotoItem>) {
    val directory = File(context.filesDir, "fotos")
    if (!directory.exists()) directory.mkdir()

    val fotosData = fotos.mapIndexed { index, fotoItem ->
        val fileName = "foto_${System.currentTimeMillis()}_$index.jpg"
        val internalFile = File(directory, fileName)
        var savedPath: String? = null
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(fotoItem.uri)
            if (inputStream == null) {
                Log.e("saveFotos", "Error: InputStream is null for ${fotoItem.uri}")
                return@mapIndexed null
            }

            internalFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
                savedPath = internalFile.absolutePath
            }

            Log.d("saveFotos", "Successfully saved to ${internalFile.absolutePath}, size: ${internalFile.length()} bytes")

        } catch (e: Exception) {
            Log.e("saveFotos", "Error saving photo ${fotoItem.uri}: ${e.message}")
        } finally {
            inputStream?.close()
        }

        if (savedPath != null) {
            "$savedPath|${fotoItem.descripcion}"
        } else {
            null
        }
    }.filterNotNull()

    val file = File(context.filesDir, "fotos.txt")
    file.writeText(fotosData.joinToString("\n"))
}

fun loadFotos(context: Context): List<FotoItem> {
    val file = File(context.filesDir, "fotos.txt")
    return if (file.exists()) {
        file.readLines().mapNotNull { line ->
            val parts = line.split("|", limit = 2)
            if (parts.size == 2) {
                val path = parts[0]
                val descripcion = parts[1]
                val fotoFile = File(path)
                if (fotoFile.exists()) {
                    FotoItem(Uri.fromFile(fotoFile), descripcion)
                } else null
            } else null
        }
    } else {
        emptyList()
    }
}