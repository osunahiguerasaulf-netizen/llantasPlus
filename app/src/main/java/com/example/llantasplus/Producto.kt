package com.example.llantasplus

import java.text.NumberFormat
import java.util.Locale

data class Producto(val id: Int, val marca: String, val modelo: String, val medida: String,
    val categoria: String, val precioCentavos: Long, val descuento: Int, val imagen: Int) {
    fun nombre() = "$marca $modelo"
    fun precioFinal() = precioCentavos * (100 - descuento) / 100
}
fun moneda(centavos: Long): String = NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(centavos / 100.0) + " MXN"
