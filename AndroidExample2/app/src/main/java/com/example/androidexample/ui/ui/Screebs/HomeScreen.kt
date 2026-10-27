package com.example.androidexample.ui.ui.Screebs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.androidexample.ui.ui.navigations.Rutas
import com.example.androidexample.util.Sesion

private data class OpcionHome(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val ruta: String
)

private fun opcionesPara(rol: String?): List<OpcionHome> = when (rol) {
    "SECRETARIA" -> listOf(
        OpcionHome("Gestionar reservas", "Aprueba o rechaza solicitudes", Icons.Filled.EventAvailable, Rutas.GESTION_RESERVAS),
        OpcionHome("Transparencia", "Ingresos, gastos y saldo", Icons.Filled.PieChart, Rutas.TRANSPARENCIA)
    )
    "TESORERIA" -> listOf(
        OpcionHome("Registrar movimiento", "Ingresos y gastos con boleta", Icons.Filled.AttachMoney, Rutas.NUEVO_MOVIMIENTO),
        OpcionHome("Transparencia", "Ingresos, gastos y saldo", Icons.Filled.PieChart, Rutas.TRANSPARENCIA)
    )
    else -> listOf(
        OpcionHome("Nueva reserva", "Solicita el uso de la sede", Icons.Filled.AddCircle, Rutas.NUEVA_RESERVA),
        OpcionHome("Mis reservas", "Revisa el estado de tus solicitudes", Icons.Filled.CalendarMonth, Rutas.MIS_RESERVAS),
        OpcionHome("Transparencia", "Ingresos, gastos y saldo", Icons.Filled.PieChart, Rutas.TRANSPARENCIA)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = Sesion.usuario
    val opciones = opcionesPara(usuario?.rol)

    // Dispara la animación de entrada de las tarjetas
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Junta de Vecinos Zaror") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Hola, ${usuario?.nombre ?: ""} 👋", style = MaterialTheme.typography.headlineSmall)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    "Perfil: ${usuario?.rol?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(4.dp))
            Text("¿Qué quieres hacer?", style = MaterialTheme.typography.titleMedium)

            opciones.forEachIndexed { index, op ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400, delayMillis = index * 120)) +
                            slideInVertically(tween(400, delayMillis = index * 120)) { it / 2 }
                ) {
                    Card(
                        onClick = { onNavegar(op.ruta) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                op.icono,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(op.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(op.descripcion, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}