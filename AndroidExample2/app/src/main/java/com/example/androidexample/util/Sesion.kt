package com.example.androidexample.util

import com.example.androidexample.data.Model.Usuarios

object Sesion {
    var usuario: Usuarios? = null
    fun cerrar() {
        usuario = null
    }
}