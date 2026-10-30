package com.example.llantasplus

import android.content.Context
import org.json.JSONArray

object DatosTienda {
    fun productos(context: Context): List<Producto> {
        val datos = JSONArray(context.assets.open("catalogo.json").bufferedReader().use { it.readText() })
        return (0 until datos.length()).map { i ->
            val p = datos.getJSONObject(i)
            Producto(p.getInt("id"), p.getString("marca"), p.getString("modelo"),
                p.getString("medida"), p.getString("categoria"), p.getLong("precioCentavos"),
                p.getInt("descuento"), context.resources.getIdentifier(p.getString("imagen"), "drawable", context.packageName))
        }
    }
}
