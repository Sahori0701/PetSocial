package com.example.proyectopoli.screens.fragments.content

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.proyectopoli.R
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import kotlinx.coroutines.launch
import java.io.File

data class VideoItem(
    val uri: Uri,
    val descripcion: String
)

@Composable
fun VideosFragment(mascotaPreferences: MascotaPreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mascota by remember { mutableStateOf(MascotaPerfil()) }
    var nuevaUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar datos desde DataStore
    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect { mascota = it }
    }

    var videos by remember { mutableStateOf(loadVideos(context)) }
    var selectedVideoIndex by remember { mutableStateOf<Int?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var descripcionTemporal by remember { mutableStateOf("") }

    val videoPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                nuevaUri = it
                showAddDialog = true
            }
        }

    if (showAddDialog && nuevaUri != null) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                nuevaUri = null
                descripcionTemporal = ""
            },
            title = { Text("Agregar descripción") },
            text = {
                TextField(
                    value = descripcionTemporal,
                    onValueChange = { descripcionTemporal = it },
                    label = { Text("Descripción del video") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    nuevaUri?.let { uri ->
                        videos = videos + VideoItem(uri, descripcionTemporal)
                        saveVideos(context, videos)
                        Toast.makeText(context, "Video agregado", Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                        nuevaUri = null
                        descripcionTemporal = ""
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAddDialog = false
                    nuevaUri = null
                    descripcionTemporal = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (selectedVideoIndex != null) {
        VideoPlayer(
            videos = videos,
            initialIndex = selectedVideoIndex!!,
            onClose = { selectedVideoIndex = null }
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
                    .weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Videos",
                    style = MaterialTheme.typography.headlineLarge,
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
                    text = stringResource(R.string.videos),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.5f)
                    .background(Color(0xFFF0F7FF)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 5.dp,
                        end = 10.dp,
                        bottom = 5.dp
                    )
                ) {
                    itemsIndexed(videos) { index, videoItem ->
                        VideoThumbnail(
                            videoItem = videoItem,
                            onClick = { selectedVideoIndex = index },
                            onDelete = {
                                videos = videos.filterIndexed { i, _ -> i != index }
                                saveVideos(context, videos)
                            }
                        )
                    }
                    item {
                        AddVideoButton { videoPicker.launch("video/*") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
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
}

@Composable
fun VideoThumbnail(videoItem: VideoItem, onClick: () -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    val thumbnailBitmap = remember(videoItem.uri) { getVideoThumbnail(context, videoItem.uri) }

    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
            .border(2.dp, Color(0xFFF0F7FF))
            .clickable { onClick() }
    ) {
        if (thumbnailBitmap != null) {
            Image(
                bitmap = thumbnailBitmap.asImageBitmap(),
                contentDescription = "Miniatura del video",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No disponible", color = Color.Gray)
            }
        }

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

        Icon(
            imageVector = Icons.Default.PlayCircle,
            contentDescription = "Reproducir",
            tint = Color(0xFFF0F7FF),
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
        )
    }
}

@Composable
fun AddVideoButton(onAddVideo: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
            .border(2.dp, Color(0xFFF0F7FF))
            .clickable { onAddVideo() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Agregar Video",
            tint = Color(0xFF3B5BFE),
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun VideoPlayer(videos: List<VideoItem>, initialIndex: Int, onClose: () -> Unit) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val scope = rememberCoroutineScope()

    DisposableEffect(currentIndex) {
        player.clearMediaItems()
        player.setMediaItem(MediaItem.fromUri(videos[currentIndex].uri))
        player.prepare()
        player.playWhenReady = true

        // Listener para detectar fin del video
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    // Si es el último video, volver al inicio
                    if (currentIndex < videos.size - 1) {
                        scope.launch {
                            currentIndex++
                        }
                    }
                }
            }
        })

        onDispose { player.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { PlayerView(context).apply {
                this.player = player
                useController = true
                controllerShowTimeoutMs = 3000
            }},
            modifier = Modifier.fillMaxSize()
        )

        // Controles de navegación
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            IconButton(
                onClick = { onClose() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (currentIndex > 0) {
                            currentIndex--
                        }
                    },
                    enabled = currentIndex > 0
                ) {
                    Icon(
                        Icons.Default.NavigateBefore,
                        contentDescription = "Video anterior",
                        tint = if (currentIndex > 0) Color.White else Color.Gray
                    )
                }

                Text(
                    text = "${currentIndex + 1}/${videos.size}",
                    color = Color.White
                )

                IconButton(
                    onClick = {
                        if (currentIndex < videos.size - 1) {
                            currentIndex++
                        }
                    },
                    enabled = currentIndex < videos.size - 1
                ) {
                    Icon(
                        Icons.Default.NavigateNext,
                        contentDescription = "Video siguiente",
                        tint = if (currentIndex < videos.size - 1) Color.White else Color.Gray
                    )
                }
            }
        }

        // Descripción en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp)
        ) {
            Text(
                text = videos[currentIndex].descripcion,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Obtener miniatura del video
fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val bitmap = retriever.getFrameAtTime(1_000_000) // Frame en el segundo 1
        retriever.release()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Convertir URI a ruta real del archivo
fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    context.contentResolver.query(contentUri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            return cursor.getString(columnIndex)
        }
    }
    return null
}

// Guardar y cargar videos en almacenamiento interno
fun saveVideos(context: Context, videos: List<VideoItem>) {
    val directory = File(context.filesDir, "videos")
    if (!directory.exists()) directory.mkdir()

    val videosData = videos.mapIndexed { index, videoItem ->
        val file = File(directory, "video_${System.currentTimeMillis()}_$index.mp4")
        val existingPath = getRealPathFromURI(context, videoItem.uri)

        if (existingPath != null) {
            try {
                val existingFile = File(existingPath)
                if (existingFile.exists()) {
                    existingFile.copyTo(file, overwrite = true)
                } else {
                    copyUriToFile(context, videoItem.uri, file)
                }
            } catch (e: Exception) {
                copyUriToFile(context, videoItem.uri, file)
            }
        } else {
            copyUriToFile(context, videoItem.uri, file)
        }

        "${file.absolutePath}|${videoItem.descripcion}"
    }

    val file = File(context.filesDir, "videos.txt")
    file.writeText(videosData.joinToString("\n"))
}

fun loadVideos(context: Context): List<VideoItem> {
    val file = File(context.filesDir, "videos.txt")
    return if (file.exists()) {
        file.readLines().mapNotNull { line ->
            val parts = line.split("|", limit = 2)
            if (parts.size == 2) {
                val path = parts[0]
                val descripcion = parts[1]
                val videoFile = File(path)
                if (videoFile.exists()) {
                    VideoItem(Uri.fromFile(videoFile), descripcion)
                } else null
            } else null
        }
    } else {
        emptyList()
    }
}

fun copyUriToFile(context: Context, uri: Uri, file: File) {
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}









