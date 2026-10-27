package com.example.androidexample.ui.ui.Screebs

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidexample.data.Model.Reserva
import com.example.androidexample.util.Notificaciones
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.ReservaViewModel

private enum class FiltroReserva(val titulo: String, val estado: String) {
    PENDIENTES("Pendientes", "PENDIENTE"),
    APROBADAS("Aprobadas", "APROBADA"),
    RECHAZADAS("Rechazadas", "RECHAZADA")
}

// Guarda qué reserva se quiere aprobar o rechazar mientras se muestra el diálogo
private data class AccionPendiente(val reserva: Reserva, val aprobar: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionReservasScreen(
    onVolver: () -> Unit,
    vm: ReservaViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val reservas by vm.todas.collectAsState()

    val filtros = FiltroReserva.entries
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var accion by remember { mutableStateOf<AccionPendiente?>(null) }

    // Permiso de notificaciones (Android 13+)
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (!concedido) {
            Toast.makeText(
                context, "Sin permiso no recibirás avisos de reservas", Toast.LENGTH_LONG
            ).show()
        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 && !Notificaciones.tienePermiso(context)) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Ejecuta la acción confirmada en el diálogo
    fun confirmar(a: AccionPendiente) {
        if (a.aprobar) {
            vm.aprobar(a.reserva) { aprobada ->
                if (aprobada) {
                    Notificaciones.mostrar(
                        context,
                        "Reserva aprobada ✅",
                        "La reserva del ${a.reserva.fecha} a las ${a.reserva.horaInicio} fue aprobada"
                    )
                    Toast.makeText(context, "Reserva aprobada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context, "Esa fecha ya tiene una reserva aprobada", Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            vm.rechazar(a.reserva)
            Toast.makeText(context, "Reserva rechazada", Toast.LENGTH_SHORT).show()
        }
    }

    // Diálogo de confirmación
    accion?.let { a ->
        AlertDialog(
            onDismissRequest = { accion = null },
            icon = {
                Icon(
                    if (a.aprobar) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = null
                )
            },
            title = { Text(if (a.aprobar) "¿Aprobar reserva?" else "¿Rechazar reserva?") },
            text = {
                Text("${a.reserva.fecha} a las ${a.reserva.horaInicio} · ${a.reserva.cantidadPersonas} personas")
            },
            confirmButton = {
                TextButton(onClick = { confirmar(a); accion = null }) {
                    Text(if (a.aprobar) "Aprobar" else "Rechazar")
                }
            },
            dismissButton = { TextButton(onClick = { accion = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Gestionar reservas") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            // Pestañas con contador por estado
            PrimaryTabRow(selectedTabIndex = tab) {
                filtros.forEachIndexed { i, f ->
                    val cantidad = reservas.count { it.estado == f.estado }
                    Tab(
                        selected = tab == i,
                        onClick = { tab = i },
                        text = { Text("${f.titulo} ($cantidad)") }
                    )
                }
            }

            // El contenido cambia con animación al cambiar de pestaña
            AnimatedContent(targetState = tab, label = "pestanas") { t ->
                val lista = reservas.filter { it.estado == filtros[t].estado }

                if (lista.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                "No hay reservas ${filtros[t].titulo.lowercase()}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista, key = { it.id }) { r ->
                            TarjetaGestion(
                                reserva = r,
                                onAprobar = { accion = AccionPendiente(r, true) },
                                onRechazar = { accion = AccionPendiente(r, false) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaGestion(
    reserva: Reserva,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icono, tinte) = when (reserva.estado) {
        "APROBADA" -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.primary
        "RECHAZADA" -> Icons.Filled.Cancel to MaterialTheme.colorScheme.error
        else -> Icons.Filled.HourglassEmpty to MaterialTheme.colorScheme.secondary
    }

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, contentDescription = reserva.estado, tint = tinte)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(reserva.fecha, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${reserva.horaInicio} · ${reserva.cantidadPersonas} personas",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(reserva.motivo, style = MaterialTheme.typography.bodyMedium)

            if (reserva.usaCocina || reserva.requireRecordatorio) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reserva.usaCocina) AssistChip(onClick = {}, label = { Text("Cocina") })
                    if (reserva.requireRecordatorio) AssistChip(onClick = {}, label = { Text("Recordatorio") })
                }
            }

            Text("Solicitada por vecino #${reserva.usuarioId}", style = MaterialTheme.typography.bodySmall)

            // Solo las pendientes muestran botones
            if (reserva.estado == "PENDIENTE") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onRechazar, modifier = Modifier.weight(1f)) { Text("Rechazar") }
                    Button(onClick = onAprobar, modifier = Modifier.weight(1f)) { Text("Aprobar") }
                }
            }
        }
    }
}