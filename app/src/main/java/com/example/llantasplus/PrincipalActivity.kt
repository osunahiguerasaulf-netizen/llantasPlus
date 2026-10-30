package com.example.llantasplus

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.example.llantasplus.databinding.ActivityPrincipalBinding

class PrincipalActivity : PantallaActivity() {
    private lateinit var b: ActivityPrincipalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); if(!verificarSesion()) return
        iniciarComponentes(); eventosClick(); navegacion()
    }
    fun iniciarComponentes() {
        b=ActivityPrincipalBinding.inflate(layoutInflater); iniciar(b.root)
        b.contenedorDestacados.orientation=android.widget.LinearLayout.VERTICAL
    }
    fun eventosClick() {
        b.categoriaTodas.setOnClickListener { catalogo() }
        b.categoriaAuto.setOnClickListener { catalogo("Automóvil") }
        b.categoriaSuv.setOnClickListener { catalogo("SUV") }
        b.categoriaCamioneta.setOnClickListener { catalogo("Camioneta") }
        b.btnVerTodos.setOnClickListener { catalogo() }
        b.bannerPrincipal.btnVerProductos.setOnClickListener { catalogo() }
        b.bannerOfertas.btnVerOfertas.setOnClickListener { catalogo(ofertas=true) }
        b.btnVerTodasOfertas.setOnClickListener { catalogo(ofertas=true) }
        b.txtBuscar.setOnEditorActionListener { _,action,_ ->
            if(action==EditorInfo.IME_ACTION_SEARCH) { catalogo(busqueda=b.txtBuscar.text.toString()); true } else false
        }
        b.btnNotificaciones.setOnClickListener { aviso("No tienes notificaciones. Consulta tus solicitudes en Perfil.") }
    }
    private fun catalogo(categoria: String="Todas", ofertas: Boolean=false, busqueda: String="") {
        startActivity(Intent(this,CatalogoActivity::class.java).putExtra("categoria",categoria).putExtra("ofertas",ofertas).putExtra("busqueda",busqueda))
    }
    override fun onResume() {
        super.onResume(); if(!::b.isInitialized) return
        Tarjetas.mostrar(this,b.contenedorDestacados,DatosTienda.productos(this).take(2),sesion) { actualizarCarrito() }
        actualizarCarrito()
    }
}
