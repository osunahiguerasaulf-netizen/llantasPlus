package com.example.llantasplus

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PedidoResumen {
    fun folio() = "LP-" + SimpleDateFormat("yyyyMMdd",Locale.ROOT).format(Date()) + "-" + UUID.randomUUID().toString().take(8).uppercase(Locale.ROOT)
    fun crear(folio: String, productos: List<Producto>, carrito: Map<Int,Int>, nombre: String,
        telefono: String, entrega: String, domicilio: String, notas: String): String {
        var total=0L
        val lineas=productos.filter { carrito.containsKey(it.id) }.map { p ->
            val cantidad=carrito.getValue(p.id); val subtotal=p.precioFinal()*cantidad; total+=subtotal
            "• ${p.nombre()} | ${p.medida}\n  $cantidad x ${moneda(p.precioFinal())} = ${moneda(subtotal)}"
        }
        return "Hola, LlantasPlus. Quiero solicitar estas llantas.\nSolicitud: $folio\n\n" +
            "Cliente: $nombre\nContacto: $telefono\nEntrega: $entrega\n" +
            (if(entrega=="Solicitar envío") "Domicilio: $domicilio\n" else "") +
            (if(notas.isNotBlank()) "Notas: $notas\n" else "") + "\n" + lineas.joinToString("\n\n") +
            "\n\nTotal estimado: ${moneda(total)}\n\nCatálogo de demostración. Favor de confirmar precio final, disponibilidad, compatibilidad, costo de envío e instalación. Esta solicitud no acredita pago ni reserva de inventario."
    }
}
