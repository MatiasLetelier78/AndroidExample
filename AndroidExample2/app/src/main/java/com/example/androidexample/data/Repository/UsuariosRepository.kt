package com.example.androidexample.data.Repository

import android.database.sqlite.SQLiteConstraintException
import com.example.androidexample.data.Local.UsuariosDao
import com.example.androidexample.data.Model.Usuarios

class UsuariosRepository(private val dao: UsuariosDao) {
    suspend fun registrar(usuario: Usuarios): Boolean =
        try {
            dao.insertar(usuario)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    suspend fun buscarPorEmail(email: String): Usuarios? =dao.buscarPorEmail(email)
}