
package com.example.proyectopoli.screens.fragments.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*
import androidx.compose.foundation.lazy.items


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalPermissionsApi::class)

@Composable
fun PhotosScreen() {
    val context = LocalContext.current
    val imageUris = remember { mutableStateListOf<Uri>() }

    // Crear archivo temporal donde guardar la imagen
    val photoFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "foto_mascota.jpg"
    )
    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            imageUris.add(photoUri)
            Toast.makeText(context, "Foto tomada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Galería de Mascotas", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { launcher.launch(photoUri) }) {
            Text("Abrir cámara")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(imageUris) { uri ->
                Card(
                    modifier = Modifier
                        .size(130.dp)
                        .padding(end = 8.dp)
                ) {
                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Foto mascota",
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { imageUris.remove(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar imagen",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

