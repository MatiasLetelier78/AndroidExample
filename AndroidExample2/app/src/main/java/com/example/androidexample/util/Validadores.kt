package com.example.androidexample.util

import android.util.Patterns

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

}