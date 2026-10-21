package com.example.androidexample.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidexample.data.Local.AppDataBase
import com.example.androidexample.data.Repository.MovimientoRepo
import com.example.androidexample.data.Repository.ReservaRepository
import com.example.androidexample.data.Repository.UsuariosRepository

class AppViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val db = AppDataBase.get(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(UsuariosRepository(db.usuariosDao()))
        modelClass.isAssignableFrom(ReservaViewModel::class.java) ->
            ReservaViewModel(ReservaRepository(db.reservaDao()))
        modelClass.isAssignableFrom(FinanzasViewModel::class.java) ->
            FinanzasViewModel(MovimientoRepo(db.movimientoDao()))
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}