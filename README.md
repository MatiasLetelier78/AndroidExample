# Gestión de Arriendos - Junta de Vecinos Población Zaror

App móvil Android para gestionar el arriendo de la sede comunitaria y
transparentar los ingresos y gastos de la organización.
Proyecto académico — DSY1105 Desarrollo de Aplicaciones Móviles (Evaluación 2).

>  Todos los datos (usuarios, montos, reservas) son **ficticios**.
> No hay pasarelas de pago ni conexión con sistemas externos.

##  Integrante
- Matias Letelier

##  Funcionalidades
**Todos los perfiles**
- Registro e inicio de sesión (clave cifrada con hash + salt, datos locales con Room)
- Transparencia financiera: saldo, ingresos, gastos, pestañas y buscador dinámico

**Vecino**
- Solicitud de reserva (fecha, hora, personas, motivo, uso de cocina y recordatorio)
- Consulta del estado de sus reservas por pestañas

**Secretaría**
- Aprobación o rechazo de reservas con confirmación, sin permitir dos reservas aprobadas en la misma fecha
- Notificación local al aprobar una reserva

**Tesorería**
- Registro de ingresos y gastos con foto de la boleta (cámara)

## Arquitectura
MVVM + Repository + Room, organizada por paquetes:

```
data        → entidades, DAOs, AppDatabase y repositorios
ui          → components, screens, navigation y theme (Jetpack Compose)
viewmodel   → lógica y estado de cada formulario
util        → validadores, hash de clave, sesión, cámara y notificaciones
```

## Recursos nativos
- Cámara (comprobantes, guardados con FileProvider)
- Notificaciones locales (con permiso en tiempo de ejecución)

## Extras
Filtro dinámico de movimientos, notificaciones locales y soporte de rotación.

## Tecnologías
Kotlin · Jetpack Compose · Material 3 · Navigation Compose · MVVM · Room (KSP)

## Cómo ejecutar
1. Clonar el repositorio: `git clone <URL del repo>`
2. Abrir la carpeta en Android Studio
3. Esperar el *Gradle Sync*
4. Ejecutar en un emulador o celular Android (minSdk 26)

## Usuarios de prueba
Registrar un usuario por cada perfil (Vecino, Secretaría y Tesorería).
