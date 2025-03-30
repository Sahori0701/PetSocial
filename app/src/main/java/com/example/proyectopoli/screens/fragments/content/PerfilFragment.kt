package com.example.proyectopoli.screens.fragments.content

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            val rutaImagen = guardarImagenPermanente(context, it) // Guarda la imagen en almacenamiento interno
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
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .size(120.dp)
                .background(Color.Gray, shape = CircleShape)
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
                    .size(120.dp)
                    .background(Color.LightGray, shape = CircleShape)
            )

            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(painterResource(id = R.drawable.raster_logo), contentDescription = "Editar Foto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            EditarCampos(mascota) { mascotaActualizada ->
                mascota = mascotaActualizada
            }
        } else {
            MostrarCampos(mascota)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isEditing) {
                    scope.launch {
                        mascotaPreferences.guardarMascota(mascota)
                        Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                    }
                }
                isEditing = !isEditing
            }
        ) {
            Text(if (isEditing) "Guardar" else "Editar")
        }
    }
}

@Composable
fun EditarCampos(mascota: MascotaPerfil, onValueChange: (MascotaPerfil) -> Unit) {
    Column {
        EditableTextField("Nombre", mascota.nombre) { onValueChange(mascota.copy(nombre = it)) }
        EditableTextField("Raza", mascota.raza) { onValueChange(mascota.copy(raza = it)) }
        EditableTextField("Peso", mascota.peso) { onValueChange(mascota.copy(peso = it)) }
        EditableTextField("Dueño", mascota.duenio) { onValueChange(mascota.copy(duenio = it)) }
        EditableTextField("Descripción", mascota.descripcion) { onValueChange(mascota.copy(descripcion = it)) }
    }
}

@Composable
fun MostrarCampos(mascota: MascotaPerfil) {
    Column {
        InfoText("Nombre", mascota.nombre)
        InfoText("Raza", mascota.raza)
        InfoText("Peso", mascota.peso)
        InfoText("Dueño", mascota.duenio)
        InfoText("Descripción", mascota.descripcion)
    }
}

@Composable
fun EditableTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        )
    }
}

@Composable
fun InfoText(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value.ifEmpty { "No especificado" }, color = Color.Gray)
    }
}


