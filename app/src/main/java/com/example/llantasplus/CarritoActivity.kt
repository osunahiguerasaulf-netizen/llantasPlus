package com.example.llantasplus

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.llantasplus.databinding.ActivityCarritoBinding
import com.example.llantasplus.databinding.LpFilaCarritoBinding

class CarritoActivity : PantallaActivity() {
    private lateinit var b: ActivityCarritoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); if(!verificarSesion()) return
        iniciarComponentes(); eventosClick(); navegacion()
    }
    fun iniciarComponentes() { b=ActivityCarritoBinding.inflate(layoutInflater); iniciar(b.root) }
    fun eventosClick() {
        b.btnVolver.setOnClickListener { finish() }; b.btnCatalogo.setOnClickListener { abrir(CatalogoActivity::class.java) }
        b.btnPedir.setOnClickListener { if(sesion.carrito().isNotEmpty()) abrir(PedidoActivity::class.java) }
        b.btnVaciar.setOnClickListener {
            AlertDialog.Builder(this).setTitle("¿Vaciar carrito?").setMessage("Se quitarán todas las llantas de este carrito.")
                .setPositiveButton("Vaciar") { _,_-> sesion.vaciar(); mostrar() }.setNegativeButton("Cancelar",null).show()
        }
    }
    private fun mostrar() {
        b.contenedorCarrito.removeAllViews()
        val carrito=sesion.carrito(); val productos=DatosTienda.productos(this)
        var total=0L
        for ((id,cantidad) in carrito) {
            val p=productos.find { it.id==id } ?: continue
            total+=p.precioFinal()*cantidad
            val fila=LpFilaCarritoBinding.inflate(layoutInflater,b.contenedorCarrito,false)
            fila.imgProducto.setImageResource(p.imagen); fila.imgProducto.contentDescription=p.nombre()
            fila.lblProducto.text="${p.nombre()} · ${p.medida}"
            fila.lblSubtotal.text="${moneda(p.precioFinal())} c/u\nSubtotal: ${moneda(p.precioFinal()*cantidad)}"
            fila.lblCantidad.text=cantidad.toString()
            fila.btnMenos.isEnabled=cantidad>1; fila.btnMas.isEnabled=cantidad<24
            fila.btnMenos.setOnClickListener { sesion.cantidad(id,cantidad-1); mostrar() }
            fila.btnMas.setOnClickListener { sesion.cantidad(id,cantidad+1); mostrar() }
            fila.btnEliminar.setOnClickListener { sesion.cantidad(id,0); mostrar() }
            b.contenedorCarrito.addView(fila.root)
        }
        val vacio=b.contenedorCarrito.childCount==0
        b.lblVacio.visibility=if(vacio) View.VISIBLE else View.GONE
        b.lblTotal.text="Total estimado: ${moneda(total)}"
        b.btnPedir.isEnabled=!vacio; b.btnVaciar.isEnabled=!vacio
        actualizarCarrito()
    }
    override fun onResume() { super.onResume(); if(::b.isInitialized) mostrar() }
}
