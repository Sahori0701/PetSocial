package com.example.proyectopoli.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectopoli.navigation.ContentNavigation
import com.example.proyectopoli.screens.fragments.content.menu.MenuFragment
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedOption by remember { mutableStateOf("inicio") }

    // 🎨 Degradado azul oscuro SOLO para el TopAppBar
    val topBarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF536DFE)) // Azul oscuro a azul intermedio
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MenuFragment(
                    selectedOption = selectedOption,
                    onOptionSelected = { option ->
                        selectedOption = option
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PetSocial",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge ) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White // Icono en blanco para mejor visibilidad
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick  = {}) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Configuración",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Lo hacemos transparente
                        titleContentColor = Color.White
                    ),
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier
                        .background(topBarGradient) // 🔥 Aplicamos el degradado aquí

                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background // Fondo sin degradado
            ) {
                ContentNavigation(selectedOption = selectedOption)
            }
        }
    }
}



