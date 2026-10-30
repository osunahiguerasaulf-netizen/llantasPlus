package com.example.llantasplus

import android.os.Bundle
import com.example.llantasplus.databinding.ActivityDetalleBinding

class DetalleActivity : PantallaActivity() {
    private lateinit var b: ActivityDetalleBinding
    private lateinit var producto: Producto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); if(!verificarSesion()) return
        val encontrado=DatosTienda.productos(this).find { it.id==intent.getIntExtra("producto",-1) }
        if(encontrado==null) { aviso("Producto no disponible"); finish(); return }
        producto=encontrado; iniciarComponentes(); eventosClick()
    }
    fun iniciarComponentes() {
        b=ActivityDetalleBinding.inflate(layoutInflater); iniciar(b.root)
        b.imgProducto.setImageResource(producto.imagen); b.imgProducto.contentDescription=producto.nombre()
        b.lblNombre.text=producto.nombre(); b.lblMedida.text="${producto.medida} · ${producto.categoria}"
        b.lblPrecio.text=moneda(producto.precioFinal())
        b.lblOferta.text=if(producto.descuento>0) "${producto.descuento}% de descuento · antes ${moneda(producto.precioCentavos)}" else "Precio por unidad"
        b.lblDescripcion.text="Llanta para ${producto.categoria.lowercase()}. Consulta compatibilidad, índice de carga y velocidad con la tienda antes de comprar.\n\nCatálogo y fotografías ilustrativos. Precios de demostración; disponibilidad, envío e instalación sujetos a confirmación por WhatsApp."
        favorito()
    }
    fun eventosClick() {
        b.btnVolver.setOnClickListener { finish() }
        b.btnFavorito.setOnClickListener { sesion.cambiarFavorito(producto.id); favorito() }
        b.btnAgregar.setOnClickListener { aviso(if(sesion.agregar(producto.id)) "Agregado al carrito" else "Máximo 24 unidades por producto") }
        b.btnCarrito.setOnClickListener { abrir(CarritoActivity::class.java) }
    }
    private fun favorito() { b.btnFavorito.text=if(sesion.favorito(producto.id)) "Quitar de favoritos" else "Agregar a favoritos" }
}
