package com.example.androidexample.util

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Validadores {

    fun nombre(v: String) = when {
        v.isBlank() -> "El nombre no puede estar vacío"
        v.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
        else -> null
    }

    fun email(v: String) = when {
        v.isBlank() -> "El email no puede estar vacío"
        !Patterns.EMAIL_ADDRESS.matcher(v).matches() -> "El email no es válido"
        else -> null
    }

    fun clave(v: String) = when {
        v.isBlank() -> "La contraseña no puede estar vacía"
        v.trim().length < 8 -> "La contraseña debe tener al menos 8 caracteres"
        else -> null
    }

    fun confirmarClave(clave: String, confirmar: String): String? = when {
        confirmar.isBlank() -> "Debes confirmar la contraseña"
        clave != confirmar -> "Las contraseñas no coinciden"
        else -> null
    }

    fun telefono(v: String): String? = when {
        v.isBlank() -> "El teléfono es obligatorio"
        !Regex("^(\\+?56)?\\s?9\\d{8}$").matches(v.trim()) -> "Ingresa un teléfono válido (ej: 912345678)"
        else -> null
    }

    fun rut(v: String): String? {
        val limpio = v.replace(".", "").replace("-", "").trim().uppercase()
        if (limpio.isBlank()) return "El RUT es obligatorio"
        if (!Regex("^\\d{7,8}[0-9K]$").matches(limpio)) return "Formato de RUT inválido"

        val cuerpo = limpio.dropLast(1)
        val dvIngresado = limpio.last()

        var suma = 0
        var multiplicador = 2
        for (i in cuerpo.length - 1 downTo 0) {
            suma += (cuerpo[i] - '0') * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = 11 - (suma % 11)
        val dvEsperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> ('0'.code + resto).toChar()
        }

        return if (dvIngresado == dvEsperado) null else "El RUT ingresado no es válido"
    }

    fun cantidadPersonas(v: String): String? {
        val n = v.toIntOrNull() ?: return "Ingresa un número"
        return when {
            n <= 0 -> "Debe ser mayor a 0"
            n > 100 -> "Máximo 100 personas"
            else -> null
        }
    }

    fun monto(v: String): String? {
        val n = v.toIntOrNull() ?: return "Ingresa un monto válido"
        return if (n <= 0) "Debe ser mayor a 0" else null
    }

    fun tipoMovimiento(v: String): String? = when {
        v.isBlank() -> "Selecciona un tipo de movimiento"
        v.uppercase() !in listOf("INGRESO", "GASTO", "EGRESO") -> "Tipo de movimiento inválido"
        else -> null
    }

    // Para reservas: no puede ser una fecha pasada
    fun fecha(v: String): String? {
        if (v.isBlank()) return "Selecciona una fecha"
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }
            val fechaIngresada = sdf.parse(v) ?: return "Formato inválido (dd/MM/yyyy)"

            val hoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            if (fechaIngresada.before(hoy)) "La fecha no puede ser pasada" else null
        } catch (e: Exception) {
            "Formato inválido (dd/MM/yyyy)"
        }
    }

    // Para movimientos: puede ser pasada, solo se revisa el formato
    fun fechaMovimiento(v: String): String? {
        if (v.isBlank()) return "Selecciona una fecha"
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }
            sdf.parse(v)
            null
        } catch (e: Exception) {
            "Formato inválido (dd/MM/yyyy)"
        }
    }

    fun hora(v: String): String? {
        if (v.isBlank()) return "Ingresa la hora"
        return if (Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(v)) null
        else "Formato inválido (HH:mm)"
    }

    fun motivo(v: String) =
        if (v.isBlank()) "Cuéntanos el motivo del arriendo" else null

    fun descripcion(v: String) = when {
        v.isBlank() -> "La descripción es obligatoria"
        v.trim().length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }

}
