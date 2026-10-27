package com.example.androidexample.util


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ActivityNotFoundException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

// La foto se guarda en el almacenamiento privado de la app (acceso seguro)
fun crearUriComprobante(context: Context): Uri {
    val carpeta = File(context.filesDir, "comprobantes").apply { mkdirs() }
    val archivo = File.createTempFile("boleta_", ".jpg", carpeta)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}


@Composable
fun rememberLanzadorCamara(onFoto: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

    val tomarFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) uriPendiente?.let { onFoto(it.toString()) }
    }

    fun abrirCamara() {
        try {
            val uri = crearUriComprobante(context)
            uriPendiente = uri
            tomarFoto.launch(uri)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No hay una app de cámara disponible", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la cámara: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(
            context, "Sin permiso de cámara no se puede adjuntar la boleta", Toast.LENGTH_LONG
        ).show()
    }

    return {
        val conPermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (conPermiso) abrirCamara() else pedirPermiso.launch(Manifest.permission.CAMERA)
    }
}