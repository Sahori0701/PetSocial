package com.example.proyectopoli.screens.fragments.content.menu


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectopoli.R
import com.example.proyectopoli.model.MenuItem
import com.example.proyectopoli.ui.theme.components.DrawerItem

@Composable

fun Menu(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val menuItems = listOf(
        MenuItem(id = "inicio", title = "Inicio", icon = Icons.Default.Home),
        MenuItem(id = "perfil", title = "Perfil", icon = Icons.Default.AccountCircle),
        MenuItem(id = "fotos", title = "Fotos", icon = Icons.Default.Image),
        MenuItem(id = "videos", title = "Videos", icon = Icons.Default.Videocam),
        MenuItem(id = "web", title = "Web", icon = Icons.Default.Language),

    )

    Column(
        modifier = Modifier

            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,          // Blanco arriba
                        Color(0xFFFEFEFF),    // Azul claro en el centro
                        Color(0xFFFDFEFF)     // Azul muy claro casi blanco abajo
                    )
                )
            )


    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(0.5f)){
            Column (modifier = Modifier
                .weight(1f)
                .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally){
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
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Column (modifier = Modifier
                .weight(0.7f)
                ){
                Image(
                    painter = painterResource(id = R.drawable.mascotas),
                    contentDescription = "Mascotas",
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()

                )
            }
        }
        Column(modifier = Modifier.weight(1f)){
            //Divider()

            LazyColumn {
                items(menuItems) { item ->
                    DrawerItem(
                        item = item,
                        selected = selectedOption == item.id,
                        onItemClick = { onOptionSelected(item.id) }
                    )
                }
            }
        }
        Divider()
    }
}