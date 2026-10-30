package com.example.llantasplus

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.llantasplus.databinding.ActivityPerfilBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilActivity : PantallaActivity() {
    private lateinit var b: ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); if(!verificarSesion()) return
        iniciarComponentes(); eventosClick(); navegacion()
    }
    fun iniciarComponentes() {
        b=ActivityPerfilBinding.inflate(layoutInflater); iniciar(b.root)
        b.lblUsuario.text="Hola, ${sesion.usuario()}"
        b.txtNombre.setText(sesion.texto("nombre",UsuariosBD(this).use { it.nombre(sesion.usuario()) }))
        b.txtTelefono.setText(sesion.texto("telefono")); b.txtDomicilio.setText(sesion.texto("domicilio"))
    }
    fun eventosClick() {
        b.btnVolver.setOnClickListener { finish() }
        b.btnGuardar.setOnClickListener {
            if(b.txtNombre.text.toString().trim().length<2) { b.txtNombre.error="Escribe tu nombre"; return@setOnClickListener }
            val telefono=b.txtTelefono.text.toString()
            if(telefono.isNotBlank() && telefono.filter { it.isDigit() }.length !in 8..15) { b.txtTelefono.error="Teléfono inválido"; return@setOnClickListener }
            sesion.guardar("nombre",b.txtNombre.text.toString().trim()); sesion.guardar("telefono",telefono.trim()); sesion.guardar("domicilio",b.txtDomicilio.text.toString().trim())
            aviso("Datos guardados")
        }
        b.btnFavoritos.setOnClickListener { startActivity(Intent(this,CatalogoActivity::class.java).putExtra("favoritos",true)) }
        b.btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this).setTitle("¿Cerrar sesión?").setMessage("Tus favoritos y carrito quedarán guardados para esta cuenta.")
                .setPositiveButton("Cerrar sesión") { _,_->
                    sesion.cerrar(); startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish()
                }.setNegativeButton("Cancelar",null).show()
        }
    }
    private fun pedidos() {
        b.contenedorPedidos.removeAllViews()
        val datos=sesion.pedidos()
        if(datos.length()==0) b.contenedorPedidos.addView(TextView(this).apply { text="Aún no has preparado solicitudes."; setTextColor(getColor(R.color.lp_gris)); setPadding(0,20,0,20) })
        for (i in 0 until datos.length()) {
            val pedido=datos.getJSONObject(i)
            val fecha=SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("es","MX")).format(Date(pedido.getLong("fecha")))
            b.contenedorPedidos.addView(AppCompatButton(this).apply {
                text="${pedido.getString("folio")}\n$fecha · envío sin verificar"; isAllCaps=false
                setOnClickListener {
                    AlertDialog.Builder(this@PerfilActivity).setTitle("Solicitud preparada")
                        .setMessage("La app no puede verificar si enviaste el mensaje ni si la tienda lo recibió.\n\n"+pedido.getString("mensaje"))
                        .setPositiveButton("Copiar") { _,_->
                            (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("Solicitud",pedido.getString("mensaje"))); aviso("Resumen copiado")
                        }.setNegativeButton("Cerrar",null).show()
                }
            })
        }
    }
    override fun onResume() { super.onResume(); if(::b.isInitialized) { pedidos(); actualizarCarrito() } }
}
