package com.example.proyectopoli.screens.fragments.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil
import kotlinx.coroutines.launch

@Composable
fun PerfilFragment(
    mascotaPreferences: MascotaPreferences,

) {
    val mascotaState = remember { mutableStateOf(MascotaPerfil()) }
    var isEditing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar la imagen desde la galería
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        mascotaState.value = mascotaState.value.copy(fotoUri = uri?.toString())
    }

    // Obtener los datos guardados en DataStore
    LaunchedEffect(Unit) {
        mascotaPreferences.mascotaFlow.collect { mascota ->
            mascotaState.value = mascota
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(mascotaState.value.fotoUri ?: ""),
            contentDescription = "Foto de la mascota",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { launcher.launch("image/*") },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            OutlinedTextField(
                value = mascotaState.value.nombre,
                onValueChange = { mascotaState.value = mascotaState.value.copy(nombre = it) },
                label = { Text("Nombre") }
            )
            OutlinedTextField(
                value = mascotaState.value.raza,
                onValueChange = { mascotaState.value = mascotaState.value.copy(raza = it) },
                label = { Text("Raza") }
            )
            OutlinedTextField(
                value = mascotaState.value.peso,
                onValueChange = { mascotaState.value = mascotaState.value.copy(peso = it) },
                label = { Text("Peso") }
            )
            OutlinedTextField(
                value = mascotaState.value.duenio,
                onValueChange = { mascotaState.value = mascotaState.value.copy(duenio = it) },
                label = { Text("Dueño") }
            )
            OutlinedTextField(
                value = mascotaState.value.descripcion,
                onValueChange = { mascotaState.value = mascotaState.value.copy(descripcion = it) },
                label = { Text("Descripción") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isEditing = false
                    coroutineScope.launch {
                        mascotaPreferences.guardarMascota(mascotaState.value)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Guardar", color = Color.White)
            }
        } else {
            Text("Nombre: ${mascotaState.value.nombre}", style = MaterialTheme.typography.bodyLarge)
            Text("Raza: ${mascotaState.value.raza}", style = MaterialTheme.typography.bodyLarge)
            Text("Peso: ${mascotaState.value.peso}", style = MaterialTheme.typography.bodyLarge)
            Text("Dueño: ${mascotaState.value.duenio}", style = MaterialTheme.typography.bodyLarge)
            Text("Descripción: ${mascotaState.value.descripcion}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { isEditing = true }) {
                Text("Editar")
            }
        }
    }
}

