package com.example.proyectopoli.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.proyectopoli.data.MascotaPreferences
import com.example.proyectopoli.model.MascotaPerfil

import com.example.proyectopoli.screens.fragments.content.BotonesFragment
import com.example.proyectopoli.screens.fragments.content.FotosFragment
import com.example.proyectopoli.screens.fragments.content.InicioFragment

import com.example.proyectopoli.screens.fragments.content.VideosFragment
import com.example.proyectopoli.screens.fragments.content.WebFragment
import com.example.proyectopoli.screens.fragments.content.PerfilFragment
import com.example.proyectopoli.screens.fragments.content.PhotosScreen

@Composable
fun ContentNavigation(selectedOption: String,
                      mascotaPreferences: MascotaPreferences
) {
    when (selectedOption) {
        "perfil" -> PerfilFragment(mascotaPreferences = mascotaPreferences)
        "fotos" -> PhotosScreen()
        "videos" -> VideosFragment()
        "web" -> WebFragment()
         else -> InicioFragment()
    }
}