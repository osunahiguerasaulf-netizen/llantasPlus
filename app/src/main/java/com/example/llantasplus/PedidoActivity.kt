package com.example.llantasplus

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.llantasplus.databinding.ActivityPedidoBinding
import org.json.JSONObject

class PedidoActivity : PantallaActivity() {
    private lateinit var b: ActivityPedidoBinding
    private var folio=""
    private var abriendo=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); if(!verificarSesion()) return
        folio=savedInstanceState?.getString("folio") ?: PedidoResumen.folio()
        iniciarComponentes(); eventosClick()
    }
    fun iniciarComponentes() {
        b=ActivityPedidoBinding.inflate(layoutInflater); iniciar(b.root)
        b.txtNombre.setText(sesion.texto("nombre",UsuariosBD(this).use { it.nombre(sesion.usuario()) }))
        b.txtTelefono.setText(sesion.texto("telefono")); b.txtDomicilio.setText(sesion.texto("domicilio"))
        b.spEntrega.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listOf("Recoger en tienda","Solicitar envío"))
        b.spEntrega.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { b.txtDomicilio.visibility=if(position==1) View.VISIBLE else View.GONE; b.lblResumen.text=resumen() }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
        actualizar()
    }
    fun eventosClick() {
        listOf(b.txtNombre,b.txtTelefono,b.txtDomicilio,b.txtNotas).forEach { campo -> campo.doAfterTextChanged { b.lblResumen.text=resumen() } }
        b.btnVolver.setOnClickListener { finish() }
        b.btnConfigurar.setOnClickListener { configurar() }
        b.btnCopiar.setOnClickListener { if(validar()) copiar(resumen()) }
        b.btnWhatsApp.setOnClickListener {
            if(!validar() || abriendo) return@setOnClickListener
            val numero=sesion.numeroNegocio()
            if(!numero.matches(Regex("[1-9][0-9]{7,14}"))) { configurar(); return@setOnClickListener }
            val mensaje=resumen()
            AlertDialog.Builder(this).setTitle("Revisar solicitud")
                .setMessage("Destino de la tienda: +$numero\n\n$mensaje\n\nSe abrirá WhatsApp. Debes pulsar Enviar ahí. El carrito se conserva.")
                .setPositiveButton("Abrir WhatsApp") { _,_-> abrirWhatsApp(numero,mensaje) }
                .setNegativeButton("Seguir editando",null).show()
        }
    }
    private fun validar(): Boolean {
        if(sesion.carrito().isEmpty()) { aviso("Tu carrito está vacío"); return false }
        if(b.txtNombre.text.toString().trim().length<2) { b.txtNombre.error="Escribe tu nombre"; return false }
        val telefono=b.txtTelefono.text.toString().filter { it.isDigit() }
        if(telefono.length !in 8..15) { b.txtTelefono.error="Escribe un teléfono válido"; return false }
        if(b.spEntrega.selectedItemPosition==1 && b.txtDomicilio.text.toString().trim().length<8) { b.txtDomicilio.error="Escribe un domicilio completo"; return false }
        return true
    }
    private fun resumen(): String = PedidoResumen.crear(folio,DatosTienda.productos(this),sesion.carrito(),
        b.txtNombre.text.toString().trim(),b.txtTelefono.text.toString().trim(),b.spEntrega.selectedItem.toString(),
        b.txtDomicilio.text.toString().trim(),b.txtNotas.text.toString().trim())
    private fun actualizar() {
        val numero=sesion.numeroNegocio()
        b.lblNegocio.text=if(numero.isEmpty()) "WhatsApp de la tienda: falta configurar" else "WhatsApp de la tienda: +$numero"
        b.lblResumen.text=resumen(); b.btnWhatsApp.isEnabled=sesion.carrito().isNotEmpty()
    }
    private fun configurar() {
        val entrada=EditText(this).apply { hint="Código de país y número"; inputType=android.text.InputType.TYPE_CLASS_PHONE; setText(sesion.numeroNegocio()); setPadding(40,20,40,20) }
        val dialogo=AlertDialog.Builder(this).setTitle("WhatsApp de la tienda")
            .setMessage("Ingresa el número real del negocio con código de país. En México: 52 seguido de los 10 dígitos. Se guardará en este dispositivo.")
            .setView(entrada).setPositiveButton("Guardar",null).setNegativeButton("Cancelar",null).create()
        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val numero=entrada.text.toString().replace(Regex("[\\s()+-]"),"")
                if(!numero.matches(Regex("[1-9][0-9]{7,14}"))) entrada.error="Número internacional inválido"
                else { sesion.guardarNumero(numero); actualizar(); dialogo.dismiss() }
            }
        }
        dialogo.show()
    }
    private fun abrirWhatsApp(numero: String, mensaje: String) {
        if(abriendo) return
        abriendo=true
        val uri=Uri.parse("https://wa.me/$numero").buildUpon().appendQueryParameter("text",mensaje).build()
        var abierto=false
        for (paquete in listOf("com.whatsapp","com.whatsapp.w4b",null)) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,uri).apply { if(paquete!=null) setPackage(paquete) })
                abierto=true; break
            } catch (_: ActivityNotFoundException) { } catch (_: SecurityException) { }
        }
        if(abierto) {
            sesion.guardar("nombre",b.txtNombre.text.toString().trim()); sesion.guardar("telefono",b.txtTelefono.text.toString().trim())
            sesion.guardar("domicilio",b.txtDomicilio.text.toString().trim())
            // Mantener una sola revisión por folio, sin afirmar que fue enviada.
            sesion.guardarPedido(JSONObject().put("folio",folio).put("fecha",System.currentTimeMillis()).put("mensaje",mensaje).put("numero",numero).put("estado","Preparada · envío sin verificar"))
            aviso("Solicitud preparada. Confirma el envío dentro de WhatsApp.")
        } else {
            AlertDialog.Builder(this).setTitle("No se pudo abrir WhatsApp")
                .setMessage("Instala WhatsApp o abre WhatsApp Web en un navegador. Puedes copiar el resumen para enviarlo manualmente.")
                .setPositiveButton("Copiar resumen") { _,_-> copiar(mensaje) }.setNegativeButton("Cerrar",null).show()
        }
        abriendo=false
    }
    private fun copiar(mensaje: String) {
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("Solicitud LlantasPlus",mensaje))
        aviso("Resumen copiado")
    }
    override fun onResume() { super.onResume(); if(::b.isInitialized) actualizar() }
    override fun onSaveInstanceState(outState: Bundle) { outState.putString("folio",folio); super.onSaveInstanceState(outState) }
}
