package com.example.llantasplus

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class Sesion(private val context: Context) {
    private val preferencias = context.getSharedPreferences("llantasplus", Context.MODE_PRIVATE)
    fun usuario() = preferencias.getString("usuario_actual", "") ?: ""
    fun iniciar(usuario: String) { preferencias.edit().putString("usuario_actual", usuario).apply() }
    fun cerrar() { preferencias.edit().remove("usuario_actual").apply() }
    private fun clave(nombre: String) = "${usuario()}_$nombre"
    fun texto(nombre: String, defecto: String = "") = preferencias.getString(clave(nombre), defecto) ?: defecto
    fun guardar(nombre: String, valor: String) { preferencias.edit().putString(clave(nombre), valor).apply() }
    fun carrito(): Map<Int, Int> {
        val datos = JSONObject(texto("carrito", "{}"))
        return datos.keys().asSequence().mapNotNull { key ->
            val id = key.toIntOrNull()
            val cantidad = datos.optInt(key)
            if (id != null && cantidad in 1..24) id to cantidad else null
        }.toMap()
    }
    fun cantidad(id: Int, cantidad: Int) {
        val datos = JSONObject(texto("carrito", "{}"))
        if (cantidad <= 0) datos.remove(id.toString()) else datos.put(id.toString(), cantidad.coerceAtMost(24))
        guardar("carrito", datos.toString())
    }
    fun agregar(id: Int): Boolean {
        val actual = carrito()[id] ?: 0
        if (actual >= 24) return false
        cantidad(id, actual + 1)
        return true
    }
    fun vaciar() = guardar("carrito", "{}")
    fun favorito(id: Int) = preferencias.getStringSet(clave("favoritos"), emptySet())!!.contains(id.toString())
    fun cambiarFavorito(id: Int) {
        val valores = preferencias.getStringSet(clave("favoritos"), emptySet())!!.toMutableSet()
        if (!valores.add(id.toString())) valores.remove(id.toString())
        preferencias.edit().putStringSet(clave("favoritos"), valores).apply()
    }
    fun pedidos() = JSONArray(texto("pedidos", "[]"))
    fun guardarPedido(pedido: JSONObject) {
        val anteriores = pedidos()
        val nuevos = JSONArray().put(pedido)
        for (i in 0 until anteriores.length()) {
            val anterior=anteriores.getJSONObject(i)
            if(anterior.optString("folio")!=pedido.optString("folio") && nuevos.length()<50) nuevos.put(anterior)
        }
        guardar("pedidos", nuevos.toString())
    }
    fun numeroNegocio(): String = preferencias.getString("whatsapp_negocio", null) ?: context.getString(R.string.lp_whatsapp_negocio)
    fun guardarNumero(numero: String) { preferencias.edit().putString("whatsapp_negocio", numero).apply() }
}
