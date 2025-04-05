import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.videoDataStore: DataStore<Preferences> by preferencesDataStore(name = "video_prefs")

class VideoPreferences(private val context: Context) {

    private val VIDEO_URIS_KEY = stringSetPreferencesKey("video_uris")

    val videoUrisFlow: Flow<List<String>> = context.videoDataStore.data.map { preferences ->
        preferences[VIDEO_URIS_KEY]?.toList() ?: emptyList()
    }

    suspend fun saveVideoUri(videoUri: String) {
        context.videoDataStore.edit { preferences ->
            val updatedUris = preferences[VIDEO_URIS_KEY]?.toMutableSet() ?: mutableSetOf()
            updatedUris.add(videoUri)
            preferences[VIDEO_URIS_KEY] = updatedUris
        }
    }

    suspend fun removeVideoUri(videoUri: String) {
        context.videoDataStore.edit { preferences ->
            val updatedUris = preferences[VIDEO_URIS_KEY]?.toMutableSet() ?: mutableSetOf()
            updatedUris.remove(videoUri)
            preferences[VIDEO_URIS_KEY] = updatedUris
        }
    }
}

