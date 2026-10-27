package com.example.androidexample.ui.ui.navigations

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidexample.ui.ui.Screebs.GestionReservasScreen
import com.example.androidexample.ui.ui.Screebs.HomeScreen
import com.example.androidexample.ui.ui.Screebs.LoginScreen
import com.example.androidexample.ui.ui.Screebs.NuevoMovimientoScreen
import com.example.androidexample.ui.ui.Screebs.ProximamenteScreen
import com.example.androidexample.ui.ui.Screebs.RegistroScreen
import com.example.androidexample.ui.ui.Screebs.TransparenciaScreen
import com.example.androidexample.util.Sesion

@Composable
fun NavGraph() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Rutas.LOGIN,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it / 3 } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    Sesion.usuario = usuario
                    nav.navigate(Rutas.HOME) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                },
                onIrRegistro = { nav.navigate(Rutas.REGISTRO) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrado = { nav.popBackStack() },
                onIrLogin = { nav.popBackStack() }
            )
        }
        composable(Rutas.HOME) {
            HomeScreen(
                onNavegar = { ruta -> nav.navigate(ruta) },
                onCerrarSesion = {
                    Sesion.cerrar()
                    nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.HOME) { inclusive = true } }
                }
            )
        }
        composable(Rutas.NUEVA_RESERVA) { ProximamenteScreen("Nueva Reserva", onVolver = { nav.popBackStack() }) }
        composable(Rutas.MIS_RESERVAS) { ProximamenteScreen("Mis Reservas", onVolver = { nav.popBackStack() }) }
        composable(Rutas.GESTION_RESERVAS) { GestionReservasScreen(onVolver = { nav.popBackStack() }) }
        composable(Rutas.NUEVO_MOVIMIENTO) { NuevoMovimientoScreen(onVolver = { nav.popBackStack() }) }
        composable(Rutas.TRANSPARENCIA) { TransparenciaScreen(onVolver = { nav.popBackStack() }) }
    }
}
