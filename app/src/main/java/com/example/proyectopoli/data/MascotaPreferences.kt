package com.example.proyectopoli.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proyectopoli.model.MascotaPerfil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore("mascota_prefs")

class MascotaPreferences(private val context: Context) {

    companion object {
        private val KEY_NOMBRE = stringPreferencesKey("nombre")
        private val KEY_RAZA = stringPreferencesKey("raza")
        private val KEY_PESO = stringPreferencesKey("peso")
        private val KEY_DUENIO = stringPreferencesKey("duenio")
        private val KEY_DESCRIPCION = stringPreferencesKey("descripcion")
        private val KEY_FOTO = stringPreferencesKey("foto_uri")
    }

    val mascotaFlow: Flow<MascotaPerfil> = context.dataStore.data.map { preferences ->
        MascotaPerfil(
            nombre = preferences[KEY_NOMBRE] ?: "",
            raza = preferences[KEY_RAZA] ?: "",
            peso = preferences[KEY_PESO] ?: "",
            duenio = preferences[KEY_DUENIO] ?: "",
            descripcion = preferences[KEY_DESCRIPCION] ?: "",
            fotoUri = preferences[KEY_FOTO]
        )
    }

    suspend fun guardarMascota(mascota: MascotaPerfil) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOMBRE] = mascota.nombre
            preferences[KEY_RAZA] = mascota.raza
            preferences[KEY_PESO] = mascota.peso
            preferences[KEY_DUENIO] = mascota.duenio
            preferences[KEY_DESCRIPCION] = mascota.descripcion
            mascota.fotoUri?.let { preferences[KEY_FOTO] = it }
        }
    }
}

