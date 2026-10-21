package com.example.androidexample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidexample.data.Model.Usuarios
import com.example.androidexample.data.Repository.UsuariosRepository
import com.example.androidexample.util.PasswordHasher
import com.example.androidexample.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UsuariosRepository) : ViewModel() {

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro = _registro.asStateFlow()

    private val _login = MutableStateFlow(LoginUiState())
    val login = _login.asStateFlow()

    // ---------- REGISTRO ----------
    fun onNombre(v: String) = _registro.update {
        it.copy(nombre = v, errorNombre = Validadores.nombre(v))
    }
    fun onEmailReg(v: String) = _registro.update {
        it.copy(email = v, errorEmail = Validadores.email(v))
    }
    fun onClaveReg(v: String) = _registro.update {
        it.copy(clave = v, errorClave = Validadores.clave(v), errorConfirmar = errorConfirmar(v, it.confirmar))
    }
    fun onConfirmar(v: String) = _registro.update {
        it.copy(confirmar = v, errorConfirmar = errorConfirmar(it.clave, v))
    }
    fun onRolReg(v: String) = _registro.update {
        it.copy(rol = v)
    }

    private fun errorConfirmar(clave: String, confirmar: String): String? {
        return when {
            confirmar.isBlank() -> "Debes confirmar la contraseña"
            clave != confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    fun registrar() {
        val s = _registro.value
        val eNombre = Validadores.nombre(s.nombre)
        val eEmail = Validadores.email(s.email)
        val eClave = Validadores.clave(s.clave)
        val eConf = errorConfirmar(s.clave, s.confirmar)

        if (listOf(eNombre, eEmail, eClave, eConf).any { it != null }) {
            _registro.update {
                it.copy(
                    errorNombre = eNombre,
                    errorEmail = eEmail,
                    errorClave = eClave,
                    errorConfirmar = eConf
                )
            }
            return
        }

        viewModelScope.launch {
            _registro.update {
                it.copy(cargando = true, errorGeneral = null)
            }
            val nuevo = Usuarios(
                nombre = s.nombre.trim(),
                email = s.email.trim().lowercase(),
                passwordHash = PasswordHasher.hashPassword(s.clave),
                rol = s.rol
            )
            val exito = repository.registrar(nuevo)
            _registro.update {
                if (exito) it.copy(cargando = false, registroExitoso = true)
                else it.copy(cargando = false, errorGeneral = "Ese correo ya está registrado")
            }
        }
    }

    fun limpiarRegistro() {
        _registro.value = RegistroUiState()
    }

    // ---------- LOGIN ----------
    fun onEmailLogin(v: String) = _login.update {
        it.copy(email = v, errorEmail = Validadores.email(v))
    }
    fun onClaveLogin(v: String) = _login.update {
        it.copy(clave = v, errorClave = if (v.isBlank()) "La clave es obligatoria" else null)
    }

    fun iniciarSesion() {
        val s = _login.value
        val eEmail = Validadores.email(s.email)
        val eClave = if (s.clave.isBlank()) "La clave es obligatoria" else null

        if (eEmail != null || eClave != null) {
            _login.update { it.copy(errorEmail = eEmail, errorClave = eClave) }
            return
        }

        viewModelScope.launch {
            _login.update { it.copy(cargando = true, errorGeneral = null) }
            val usr = repository.buscarPorEmail(s.email.trim().lowercase())
            val valido = usr != null && PasswordHasher.verify(s.clave, usr.passwordHash)
            _login.update {
                if (valido) it.copy(cargando = false, usuario = usr)
                else it.copy(cargando = false, errorGeneral = "Correo o clave incorrectos")
            }
        }
    }

    fun limpiarLogin() {
        _login.value = LoginUiState()
    }
}
