package com.example.androidexample.ui.ui.components


import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImagenUri(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            try {
                val opciones = BitmapFactory.Options().apply { inSampleSize = 4 } // liviana
                context.contentResolver.openInputStream(uri.toUri())?.use { entrada ->
                    BitmapFactory.decodeStream(entrada, null, opciones)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    val imagen = bitmap
    if (imagen != null) {
        Image(
            bitmap = imagen,
            contentDescription = "Comprobante",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier, contentAlignment = Alignment.Center) { Text("Cargando…") }
    }
}