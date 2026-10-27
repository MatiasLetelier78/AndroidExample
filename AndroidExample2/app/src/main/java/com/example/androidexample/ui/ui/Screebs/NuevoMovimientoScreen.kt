package com.example.androidexample.ui.ui.Screebs


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidexample.ui.ui.components.CampoFecha
import com.example.androidexample.ui.ui.components.CampoTexto
import com.example.androidexample.ui.ui.components.ImagenUri
import com.example.androidexample.util.rememberLanzadorCamara
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.FinanzasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoScreen(
    onVolver: () -> Unit,
    vm: FinanzasViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val s by vm.form.collectAsState()
    val context = LocalContext.current

    // Recurso nativo: cámara
    val abrirCamara = rememberLanzadorCamara { uri -> vm.onComprobante(uri) }

    LaunchedEffect(s.guardado) {
        if (s.guardado) {
            Toast.makeText(context, "Movimiento registrado ✅", Toast.LENGTH_SHORT).show()
            vm.limpiar()
            onVolver()
        }
    }

    val tipos = listOf("INGRESO" to "Ingreso", "GASTO" to "Gasto")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Registrar movimiento") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Seccion("Tipo de movimiento") {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    tipos.forEachIndexed { index, (valor, texto) ->
                        SegmentedButton(
                            selected = s.tipo == valor,
                            onClick = { vm.onTipo(valor) },
                            shape = SegmentedButtonDefaults.itemShape(index, tipos.size),
                            label = { Text(texto) }
                        )
                    }
                }
            }

            Seccion("Datos") {
                CampoTexto(s.descripcion, vm::onDescripcion, "Descripción", s.errorDescripcion)
                CampoTexto(
                    s.monto, vm::onMonto, "Monto (CLP, ficticio)", s.errorMonto,
                    teclado = KeyboardType.Number
                )
                CampoFecha(s.fecha, "Fecha del movimiento", s.errorFecha, vm::onFecha)
            }

            Seccion("Comprobante (opcional)") {
                OutlinedButton(onClick = abrirCamara, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (s.comprobanteUri == null) "Tomar foto de la boleta" else "Tomar otra foto")
                }

                AnimatedVisibility(visible = s.comprobanteUri != null) {
                    Column {
                        ImagenUri(
                            uri = s.comprobanteUri ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        TextButton(onClick = { vm.onComprobante(null) }) { Text("Quitar foto") }
                    }
                }
            }

            Button(
                onClick = vm::guardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar movimiento")
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            contenido()
        }
    }
}