package com.samuelypsv.habitos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Verde = Color(0xFF2F6F4F)
private val VerdeClaro = Color(0xFF8FD3AE)

@Composable
fun HabitosTheme(content: @Composable () -> Unit) {
    val oscuro = isSystemInDarkTheme()
    val colores = when {
        // Material You: en Android 12+ usamos los colores del fondo de pantalla
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (oscuro) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        oscuro -> darkColorScheme(primary = VerdeClaro)
        else -> lightColorScheme(primary = Verde)
    }
    MaterialTheme(colorScheme = colores, content = content)
}
