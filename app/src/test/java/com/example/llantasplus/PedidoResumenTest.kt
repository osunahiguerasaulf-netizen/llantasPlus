package com.example.llantasplus

import org.junit.Assert.*
import org.junit.Test

class PedidoResumenTest {
    private val a=Producto(1,"Michelin","Primacy 4","205/55 R16","Automóvil",245000,10,0)
    private val b=Producto(2,"BFGoodrich","All-Terrain","265/70 R17","Camioneta",420000,30,0)
    @Test fun descuentoSeAplicaEnCentavos() {
        assertEquals(220500L,a.precioFinal())
        assertEquals(294000L,b.precioFinal())
    }
    @Test fun resumenIncluyeSoloProductosDelCarritoYTotalDescontado() {
        val mensaje=PedidoResumen.crear("LP-TEST",listOf(a,b),mapOf(1 to 4),"Saúl & Ana","6691234567","Recoger en tienda","Domicilio privado","Consultar instalación")
        assertTrue(mensaje.contains("LP-TEST"))
        assertTrue(mensaje.contains("Saúl & Ana"))
        assertTrue(mensaje.contains("4 x ${moneda(220500L)}"))
        assertTrue(mensaje.contains("Total estimado: ${moneda(882000L)}"))
        assertFalse(mensaje.contains("BFGoodrich"))
        assertFalse(mensaje.contains("Domicilio privado"))
        assertTrue(mensaje.contains("no acredita pago"))
    }
    @Test fun envioIncluyeDomicilioYTotalDeVariasLineas() {
        val mensaje=PedidoResumen.crear("LP-ENVIO",listOf(a,b),mapOf(1 to 2,2 to 1),"Cliente","6691234567","Solicitar envío","Calle 1, número 2","")
        assertTrue(mensaje.contains("Domicilio: Calle 1, número 2"))
        assertTrue(mensaje.contains("Total estimado: ${moneda(735000L)}"))
        assertFalse(mensaje.contains("Notas:"))
    }
    @Test fun foliosSonUnicos() { assertNotEquals(PedidoResumen.folio(),PedidoResumen.folio()) }
}
