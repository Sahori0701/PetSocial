package com.example.proyectopoli.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun guardarImagenPermanente(context: Context, imageUri: Uri): String? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val file = File(context.filesDir, "perfil_mascota.jpg") // Nombre fijo para la imagen
    val outputStream = FileOutputStream(file)

    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()

    return file.absolutePath // Retorna la ruta absoluta de la imagen guardada
}
