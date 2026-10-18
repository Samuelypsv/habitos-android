package com.samuelypsv.habitos.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samuelypsv.habitos.data.Habito
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val EMOJIS = listOf("💧", "📚", "🏃", "🧘", "🥗", "😴", "💻", "🎸")
private val ES = Locale.forLanguageTag("es-ES")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitosScreen(viewModel: HabitosViewModel) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var creando by rememberSaveable { mutableStateOf(false) }
    var borrando by remember { mutableStateOf<Habito?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis hábitos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { creando = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo hábito")
            }
        },
    ) { padding ->
        when {
            estado.cargando -> Unit
            estado.habitos.isEmpty() -> Vacio(Modifier.padding(padding))
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { ResumenHoy(estado.hechosHoy, estado.habitos.size) }
                items(estado.habitos, key = { it.habito.id }) { ui ->
                    TarjetaHabito(
                        ui = ui,
                        onAlternar = { dia, hecho -> viewModel.alternar(ui.habito, dia, hecho) },
                        onBorrar = { borrando = ui.habito },
                    )
                }
            }
        }
    }

    if (creando) {
        DialogoNuevoHabito(
            onCrear = { nombre, emoji ->
                viewModel.crear(nombre, emoji)
                creando = false
            },
            onCancelar = { creando = false },
        )
    }

    borrando?.let { habito ->
        AlertDialog(
            onDismissRequest = { borrando = null },
            title = { Text("¿Borrar «${habito.nombre}»?") },
            text = { Text("Se perderá todo su historial.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.borrar(habito)
                    borrando = null
                }) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { borrando = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun Vacio(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            "Aún no tienes hábitos.\nPulsa + para crear el primero.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ResumenHoy(hechos: Int, total: Int) {
    Column(Modifier.fillMaxWidth()) {
        Text("Hoy: $hechos de $total", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(8.dp))
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else hechos.toFloat() / total },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TarjetaHabito(
    ui: HabitoUi,
    onAlternar: (LocalDate, Boolean) -> Unit,
    onBorrar: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ui.habito.emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(ui.habito.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "🔥 ${ui.racha} días · récord ${ui.mejorRacha} · ${ui.porcentajeMes}% este mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onBorrar) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Borrar ${ui.habito.nombre}")
                }
                FilledIconToggleButton(
                    checked = ui.hechoHoy,
                    onCheckedChange = { onAlternar(LocalDate.now(), it) },
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Hecho hoy")
                }
            }
            Spacer(Modifier.size(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ui.semana.forEach { (dia, hecho) -> DiaSemana(dia, hecho) { onAlternar(dia, !hecho) } }
            }
        }
    }
}

@Composable
private fun DiaSemana(dia: LocalDate, hecho: Boolean, onClick: () -> Unit) {
    val nombre = dia.dayOfWeek.getDisplayName(TextStyle.NARROW, ES)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(nombre, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.size(4.dp))
        Surface(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .semantics { contentDescription = "$nombre ${dia.dayOfMonth}: ${if (hecho) "hecho" else "sin hacer"}" },
            shape = CircleShape,
            color = if (hecho) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            border = if (hecho) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "${dia.dayOfMonth}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (dia == LocalDate.now()) FontWeight.Bold else FontWeight.Normal,
                    color = if (hecho) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun DialogoNuevoHabito(onCrear: (String, String) -> Unit, onCancelar: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var emoji by rememberSaveable { mutableStateOf(EMOJIS.first()) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nuevo hábito") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.take(40) },
                    label = { Text("Nombre") },
                    placeholder = { Text("Beber 2 L de agua") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    EMOJIS.forEach { e ->
                        Surface(
                            onClick = { emoji = e },
                            shape = CircleShape,
                            color = if (e == emoji) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface,
                        ) {
                            Text(e, Modifier.padding(6.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCrear(nombre, emoji) }, enabled = nombre.isNotBlank()) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}
