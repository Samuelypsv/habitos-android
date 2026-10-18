package com.samuelypsv.habitos.ui

import com.samuelypsv.habitos.data.Habito
import com.samuelypsv.habitos.data.HabitoConDias
import com.samuelypsv.habitos.data.HabitoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/** Repositorio en memoria para probar el ViewModel sin base de datos. */
private class RepositorioFalso : HabitoRepository {
    val datos = MutableStateFlow<List<HabitoConDias>>(emptyList())
    private var siguienteId = 1L

    override fun observar(): Flow<List<HabitoConDias>> = datos

    override suspend fun crear(nombre: String, emoji: String, hoy: LocalDate) {
        datos.update { it + HabitoConDias(Habito(siguienteId++, nombre, emoji, hoy.toEpochDay()), emptySet()) }
    }

    override suspend fun borrar(habito: Habito) {
        datos.update { lista -> lista.filterNot { it.habito.id == habito.id } }
    }

    override suspend fun alternar(habito: Habito, dia: LocalDate, cumplido: Boolean) {
        datos.update { lista ->
            lista.map {
                if (it.habito.id != habito.id) it
                else it.copy(dias = if (cumplido) it.dias + dia else it.dias - dia)
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HabitosViewModelTest {

    private val hoy = LocalDate.of(2026, 3, 15)
    private val repo = RepositorioFalso()
    private lateinit var vm: HabitosViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vm = HabitosViewModel(repo) { hoy }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `crear un habito lo muestra sin hacer`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.estado.collect {} }

        vm.crear("  Leer  ", "📚")

        val estado = vm.estado.first { it.habitos.isNotEmpty() }
        assertEquals("Leer", estado.habitos.single().habito.nombre)
        assertEquals(0, estado.hechosHoy)
    }

    @Test
    fun `no crea habitos sin nombre`() = runTest {
        vm.crear("   ", "📚")
        assertTrue(repo.datos.value.isEmpty())
    }

    @Test
    fun `marcar hoy actualiza racha y resumen`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.estado.collect {} }
        vm.crear("Correr", "🏃")
        val habito = repo.datos.value.single().habito

        vm.alternar(habito, hoy.minusDays(1), true)
        vm.alternar(habito, hoy, true)

        val ui = vm.estado.value.habitos.single()
        assertTrue(ui.hechoHoy)
        assertEquals(2, ui.racha)
        assertEquals(1, vm.estado.value.hechosHoy)
    }

    @Test
    fun `no se pueden marcar dias futuros`() = runTest {
        vm.crear("Correr", "🏃")
        val habito = repo.datos.value.single().habito

        vm.alternar(habito, hoy.plusDays(1), true)

        assertTrue(repo.datos.value.single().dias.isEmpty())
    }
}
