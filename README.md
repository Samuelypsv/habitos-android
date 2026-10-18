# ✅ Hábitos (Android)

App Android para seguir hábitos diarios: marca lo que has hecho cada día, mantén tu racha y mira tu progreso de la semana y del mes. Hecha con **Kotlin** y **Jetpack Compose** como proyecto de 2º de DAM (Programación Multimedia y Dispositivos Móviles).

## Tecnologías

- Kotlin 2.0 y corrutinas / Flow
- Jetpack Compose con Material 3 (y colores dinámicos de Material You en Android 12+)
- Room (SQLite) con KSP
- Arquitectura MVVM: `ViewModel` + `StateFlow`
- JUnit 4 y kotlinx-coroutines-test
- GitHub Actions: tests y APK de debug en cada push

## Funcionalidades

- Crear hábitos con nombre y emoji, y borrarlos
- Marcar el hábito de hoy con un toque, o corregir cualquier día de la última semana
- Racha actual 🔥, mejor racha histórica y porcentaje de cumplimiento de los últimos 30 días
- Barra de progreso con los hábitos hechos hoy
- Tema claro y oscuro

## Arquitectura

```
ui/       HabitosScreen (Compose)  ←  HabitosViewModel (StateFlow<HabitosEstado>)
data/     HabitoRepository  →  HabitoDao (Room)  →  habitos.db
domain/   Rachas: cálculo de rachas y porcentajes, Kotlin puro y sin Android
```

- La lógica de rachas está separada de Android para poder probarla con tests normales de JVM.
- El repositorio es una interfaz: en la app usa Room y en los tests un repositorio falso en memoria.
- Las dependencias se crean en `HabitosApp` sin framework de inyección, para que se vea claro de dónde sale cada cosa.
- La tabla `registros` usa clave primaria compuesta `(habitoId, epochDay)` y borrado en cascada.

## Cómo ejecutarlo

1. Abre la carpeta con **Android Studio** (Ladybug o posterior).
2. Espera a que sincronice Gradle.
3. Ejecuta la configuración `app` en un emulador o móvil con Android 8.0 o superior.

Desde terminal:

```bash
./gradlew assembleDebug        # genera app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # tests unitarios
```

También puedes descargar el APK generado por la CI en la pestaña **Actions** del repositorio.

## Autor

Samuel ([@Samuelypsv](https://github.com/Samuelypsv)), estudiante de Desarrollo de Aplicaciones Multiplataforma.
