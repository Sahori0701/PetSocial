package com.example.proyectopoli.screens.fragments.content

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectopoli.R
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import com.example.proyectopoli.utils.guardarImagenPermanente
import kotlinx.coroutines.launch

@Composable

fun PerfilFragment(mascotaPreferences: MascotaPreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mascota by remember { mutableStateOf(MascotaPerfil()) }
    var isEditing by remember { mutableStateOf(false) }
    var nuevaUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar datos desde DataStore
    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect { mascota = it }
    }

    // Lanzador para seleccionar una imagen de la galería
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            nuevaUri = it
            val rutaImagen =
                guardarImagenPermanente(context, it) // Guarda la imagen en almacenamiento interno
            scope.launch {
                mascotaPreferences.guardarMascota(
                    mascota.copy(fotoUri = rutaImagen) // Guardar la nueva foto
                )
                Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
            }
        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if (mascota.fotoUri.isNullOrEmpty()) {
                    painterResource(id = R.drawable.raster_logo)
                } else {
                    rememberAsyncImagePainter(mascota.fotoUri)
                },
                contentDescription = "Foto de Mascota",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .border(BorderStroke(2.dp, Color.White), CircleShape) // 🟢 Borde gris
                    .clip(CircleShape)
            )

            // 📷 Botón de cámara superpuesto en la esquina inferior derecha
            Box(
                modifier = Modifier
                    .size(40.dp) // 📌 Tamaño del botón
                    .align(Alignment.BottomEnd) // 📌 Se coloca en la parte inferior derecha
                    .offset(
                        x = (-100).dp,
                        y = -10.dp
                    ) // 📌 Ajuste fino para que toque el borde de la imagen
                    .background(Color.White, CircleShape) // 🟢 Fondo gris claro
                    .border(BorderStroke(1.dp, Color.White), CircleShape) // 🟢 Borde gris oscuro
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Editar Foto",
                    tint = Color(0xFF3B5BFE),
                    modifier = Modifier.size(20.dp) // 📌 Tamaño del icono ajustado para no sobresalir
                )
            }

        }
        Spacer(modifier = Modifier.height(15.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.4f),
                //.background(Color(0xFFBBD6FF)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isEditing) {
                        EditarEdad(mascota) { mascotaActualizada ->
                            mascota = mascotaActualizada
                        }
                    } else {
                        MostrarEdad(mascota)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isEditing) {
                        EditarPeso(mascota) { mascotaActualizada ->
                            mascota = mascotaActualizada
                        }
                    } else {
                        MostrarPeso(mascota)
                    }
                }
            }
            Spacer(modifier = Modifier.height(0.dp))
            Row(
                modifier = Modifier.weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isEditing) {
                        EditarRaza(mascota) { mascotaActualizada ->
                            mascota = mascotaActualizada
                        }
                    } else {
                        MostrarRaza(mascota)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color(0xFFF0F7FF)),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    .height(87.dp)
                    .width(100.dp)
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
                    containerColor = Color.Transparent, // Fondo transparente para aplicar el degradado
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (isEditing) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF536DFE)
                                ) // Degradado azul oscuro a claro
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF536DFE)
                                ) // Azul estándar para editar
                            )
                        },
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = if (isEditing) "Guardar" else "Editar",
                    style = MaterialTheme.typography.bodySmall,
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))

    ) {
        BasicTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
                onValueChange(mascota.copy(descripcion = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
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
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {

            Text(
                text = mascota.descripcion,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(start = 8.dp) // Agrega un poco de espacio entre la imagen y el texto
            )
        }
    }
}

@Composable
fun EditarPeso(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var peso by remember { mutableStateOf(mascota.peso) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))

    ) {
        BasicTextField(
            value = peso,
            onValueChange = {
                peso = it
                onValueChange(mascota.copy(peso = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditarDuenio(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var duenio by remember { mutableStateOf(mascota.duenio) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))

    ) {
        BasicTextField(
            value = duenio,
            onValueChange = {
                duenio = it
                onValueChange(mascota.copy(duenio = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
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
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person, // 📌 Ícono de cumpleaños como representación de la edad
                contentDescription = "Edad de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.duenio,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    //fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                ),
                modifier = Modifier.padding(start = 8.dp) // Agrega un poco de espacio entre la imagen y el texto
            )
        }
    }
}

@Composable
fun EditarRaza(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var raza by remember { mutableStateOf(mascota.raza) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))

    ) {
        BasicTextField(
            value = raza,
            onValueChange = {
                raza = it
                onValueChange(mascota.copy(raza = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
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
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets, // 📌 Ícono de cumpleaños como representación de la edad
                contentDescription = "Edad de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.raza,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    //fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                ),
                modifier = Modifier.padding(start = 8.dp) // Agrega un poco de espacio entre la imagen y el texto
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
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety, // 📌 Ícono de cumpleaños como representación de la edad
                contentDescription = "Edad de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.peso,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    //fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                ),
                modifier = Modifier.padding(start = 8.dp) // Agrega un poco de espacio entre la imagen y el texto
            )
        }
    }
}

@Composable
fun EditarEdad(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var edad by remember { mutableStateOf(mascota.edad) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))

    ) {
        BasicTextField(
            value = edad,
            onValueChange = {
                edad = it
                onValueChange(mascota.copy(edad = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
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
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cake, // 📌 Ícono de cumpleaños como representación de la edad
                contentDescription = "Edad de la Mascota",
                tint = Color(0xFF3B5BFE),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = mascota.edad,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    //fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                ),
                modifier = Modifier.padding(start = 8.dp) // Agrega un poco de espacio entre la imagen y el texto
            )
        }
    }
}


@Composable
fun EditarNombre(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    var nombre by remember { mutableStateOf(mascota.nombre) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))

    ) {
        BasicTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                onValueChange(mascota.copy(nombre = it))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MostrarNombre(mascota: MascotaPerfil) {
    Text(
        text = mascota.nombre,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
    )
}


