package com.example.proyectopoli.screens.fragments.content

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.proyectopoli.R
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import com.example.proyectopoli.utils.guardarImagenPermanente
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoilApi::class)
private fun clearImageCache(context: Context, url: String?) {
    if (url != null) {
        val imageLoader = ImageLoader(context)
        imageLoader.memoryCache?.remove(MemoryCache.Key(url))
        imageLoader.diskCache?.remove(url)
    }
}

@Composable
fun PerfilFragment(mascotaPreferences: MascotaPreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mascota by remember { mutableStateOf(MascotaPerfil()) }
    var isEditing by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<String?>(null) }

    // Un contador para forzar la recomposición
    var forceUpdateCounter by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect {
            Log.d("PerfilFragment", "Valor emitido por mascotaFlow: ${it.fotoUri}")
            mascota = it
            imageUri = it.fotoUri
            Log.d("PerfilFragment", "Estado imageUri (collect): $imageUri")
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val rutaImagen = guardarImagenPermanente(context, it)
            Log.d("PerfilFragment", "Ruta de la imagen guardada (launcher): $rutaImagen")

            // Guardamos temporalmente la ruta de la imagen
            tempImageUri = rutaImagen

            // Mostramos el diálogo de confirmación
            showConfirmDialog = true
        }
    }

    // Función para aplicar los cambios de la imagen
    fun applyImageChange() {
        tempImageUri?.let { newUri ->
            // Limpiamos la caché de la imagen anterior
            clearImageCache(context, imageUri)

            // Actualizamos los estados
            imageUri = newUri
            mascota = mascota.copy(fotoUri = newUri)

            // Incrementamos el contador para forzar la recomposición
            forceUpdateCounter++

            // Guardamos en preferencias
            scope.launch {
                mascotaPreferences.guardarMascota(mascota)
                Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()

                // Forzamos otra recomposición después de un breve retraso
                delay(100)
                forceUpdateCounter++
            }
        }
    }

    // Diálogo de confirmación
    if (showConfirmDialog) {
        AlertDialog(
            containerColor = Color.White.copy(alpha = 0.7f),
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Confirmar cambio de foto",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            },
            text = {
                Text(
                    "¿Deseas actualizar la foto de perfil?",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                ) {
                    Button(
                        onClick = {
                            applyImageChange()
                            showConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B5BFE).copy(alpha = 0.8f),
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            "Guardar",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            },
            dismissButton = {
                Column (modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)){
                Button(
                    onClick = {
                        tempImageUri = null
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B5BFE).copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text("Cancelar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White)
                }
            }}
        )
    }

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
                .weight(0.2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isEditing) {
                EditarNombre(mascota) { mascotaActualizada ->
                    mascota = mascotaActualizada
                }
            } else {
                MostrarNombre(mascota)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Este key fuerza a Compose a recrear el Box cuando forceUpdateCounter cambia
        key(forceUpdateCounter) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(imageUri ?: R.drawable.raster_logo)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.DISABLED) // Desactivamos la caché de disco
                        .memoryCachePolicy(CachePolicy.DISABLED) // Desactivamos la caché de memoria
                        .build()
                )

                val state = painter.state

                // Cuando la imagen está cargando, podemos mostrar un indicador
                if (state is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF3B5BFE)
                    )
                }

                Image(
                    painter = painter,
                    contentDescription = "Foto de Mascota",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .shadow(8.dp, CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-80).dp, y = (-5).dp)
                        .background(Color.White, CircleShape)
                        .border(BorderStroke(1.dp, Color.White), CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Editar Foto",
                        tint = Color(0xFF3B5BFE),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(0.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.4f)
                .background(Color(0xFFF0F7FF)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isEditing) {
                            EditarEdad(mascota) { mascotaActualizada ->
                                mascota = mascotaActualizada
                            }
                        } else {
                            MostrarEdad(mascota)
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isEditing) {
                            EditarPeso(mascota) { mascotaActualizada ->
                                mascota = mascotaActualizada
                            }
                        } else {
                            MostrarPeso(mascota)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(0.dp))
            Row(
                modifier = Modifier.weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isEditing) {
                            EditarRaza(mascota) { mascotaActualizada ->
                                mascota = mascotaActualizada
                            }
                        } else {
                            MostrarRaza(mascota)
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isEditing) {
                            EditarDuenio(mascota) { mascotaActualizada ->
                                mascota = mascotaActualizada
                            }
                        } else {
                            MostrarDuenio(mascota)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(0.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            if (isEditing) {
                EditarDescripcion(mascota) { mascotaActualizada ->
                    mascota = mascotaActualizada
                }
            } else {
                MostrarDescripcion(mascota)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.raster_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(75.dp)
                    .width(95.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (isEditing) {
                        scope.launch {
                            mascotaPreferences.guardarMascota(mascota)
                            Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                        }
                    }
                    isEditing = !isEditing
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (isEditing) {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A237E), Color(0xFF536DFE))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A237E), Color(0xFF536DFE))
                            )
                        },
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = if (isEditing) "Guardar" else "Editar",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EditarDescripcion(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var descripcion by remember { mutableStateOf(mascota.descripcion) }
    var isEditing by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
                onValueChange(mascota.copy(descripcion = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontSize = 18.sp, color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F7FF))
                .border(2.dp, Color(0xFFEEF1FF), RoundedCornerShape(2.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    if (descripcion.isEmpty()) {
                        Text(
                            text = "Ingresa una descripción de su petfriend",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun MostrarDescripcion(mascota: MascotaPerfil) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = mascota.descripcion,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center, fontSize = 20.sp
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun EditarPeso(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var peso by remember { mutableStateOf(mascota.peso) }
    var isEditing by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = peso,
            onValueChange = {
                peso = it
                onValueChange(mascota.copy(peso = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontSize = 18.sp, color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F7FF))
                .border(2.dp, Color(0xFFEEF1FF), RoundedCornerShape(2.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    if (peso.isEmpty()) {
                        Text(
                            text = "Peso", // Placeholder
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { isEditing = false }) {
                Text("Cancelar")
            }
            TextButton(onClick = { isEditing = false }) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun EditarDuenio(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var duenio by remember { mutableStateOf(mascota.duenio) }
    var isEditing by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = duenio,
            onValueChange = {
                duenio = it
                onValueChange(mascota.copy(duenio = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontSize = 18.sp, color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F7FF))
                .border(2.dp, Color(0xFFEEF1FF), RoundedCornerShape(2.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    if (duenio.isEmpty()) {
                        Text(
                            text = "Nombre del dueño",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { isEditing = false }) {
                Text("Cancelar")
            }
            TextButton(onClick = { isEditing = false }) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun MostrarDuenio(mascota: MascotaPerfil) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Dueño de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.duenio,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun EditarRaza(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var raza by remember { mutableStateOf(mascota.raza) }
    var isEditing by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = raza,
            onValueChange = {
                raza = it
                onValueChange(mascota.copy(raza = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontSize = 18.sp, color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F7FF))
                .border(2.dp, Color(0xFFEEF1FF), RoundedCornerShape(2.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    if (raza.isEmpty()) {
                        Text(
                            text = "Raza",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 18.sp
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { isEditing = false }) {
                Text("Cancelar")
            }
            TextButton(onClick = { isEditing = false }) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun MostrarRaza(mascota: MascotaPerfil) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = "Raza de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.raza,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun MostrarPeso(mascota: MascotaPerfil) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = "Peso de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.peso,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun EditarEdad(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var edad by remember { mutableStateOf(mascota.edad) }
    var isEditing by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = edad,
            onValueChange = {
                edad = it
                onValueChange(mascota.copy(edad = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontSize = 18.sp, color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F7FF))
                .border(2.dp, Color(0xFFEEF1FF), RoundedCornerShape(2.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    if (edad.isEmpty()) {
                        Text(
                            text = "Edad", // Placeholder text
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { isEditing = false }) {
                Text("Cancelar")
            }
            TextButton(onClick = { isEditing = false }) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun MostrarEdad(mascota: MascotaPerfil) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cake,
                contentDescription = "Edad de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.edad,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun EditarNombre(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var nombre by remember { mutableStateOf(mascota.nombre) }
    var isEditing by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                onValueChange(mascota.copy(nombre = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontSize = 20.sp, color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F7FF))
                .border(2.dp, Color(0xFFEEF1FF), RoundedCornerShape(2.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    if (nombre.isEmpty()) {
                        Text(
                            text = " Ingrese el nombre de su mascota",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 18.sp
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { isEditing = false }) {
                Text("Cancelar")
            }
            TextButton(onClick = { isEditing = false }) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun MostrarNombre(mascota: MascotaPerfil) {
    Text(
        text = mascota.nombre,
        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}