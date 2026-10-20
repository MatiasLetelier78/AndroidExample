package com.example.androidexample.viewmodel

import com.example.androidexample.data.Model.Usuarios

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val clave: String = "",
    val confirmar: String = "",
    val rol: String = "VECINO",
    val errorNombre: String? = null,
    val errorEmail: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val registroExitoso: Boolean = false
)

data class LoginUiState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val usuario: Usuarios? = null
)