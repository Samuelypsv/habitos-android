package com.samuelypsv.habitos.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class RachasTest {

    private val hoy = LocalDate.of(2026, 3, 15)
    private fun hace(n: Long) = hoy.minusDays(n)

    @Test
    fun `sin registros no hay racha`() {
        assertEquals(0, Rachas.actual(emptySet(), hoy))
        assertEquals(0, Rachas.mejor(emptySet()))
    }

    @Test
    fun `cuenta los dias seguidos hasta hoy`() {
        assertEquals(3, Rachas.actual(setOf(hoy, hace(1), hace(2), hace(4)), hoy))
    }

    @Test
    fun `si hoy no esta marcado la racha de ayer sigue viva`() {
        assertEquals(2, Rachas.actual(setOf(hace(1), hace(2)), hoy))
    }

    @Test
    fun `fallar ayer rompe la racha`() {
        assertEquals(0, Rachas.actual(setOf(hace(2), hace(3)), hoy))
    }

    @Test
    fun `la mejor racha busca el tramo mas largo`() {
        val dias = setOf(hace(10), hace(9), hace(8), hace(7), hace(3), hace(2), hoy)
        assertEquals(4, Rachas.mejor(dias))
    }

    @Test
    fun `porcentaje del mes ignora dias fuera de rango`() {
        val dias = (0L until 15L).map(::hace).toSet() + hace(40) + hoy.plusDays(1)
        assertEquals(50, Rachas.porcentaje(dias, hoy, 30))
    }

    @Test
    fun `ultimos dias van del mas antiguo a hoy`() {
        val semana = Rachas.ultimosDias(setOf(hoy, hace(6)), hoy, 7)
        assertEquals(7, semana.size)
        assertEquals(hace(6) to true, semana.first())
        assertEquals(hoy to true, semana.last())
        assertEquals(2, semana.count { it.second })
    }
}
