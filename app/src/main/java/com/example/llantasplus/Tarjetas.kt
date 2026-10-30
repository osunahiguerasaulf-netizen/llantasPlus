package com.example.llantasplus

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.llantasplus.databinding.LpTarjetaProductoBinding

object Tarjetas {
    fun mostrar(activity: Activity, contenedor: LinearLayout, productos: List<Producto>, sesion: Sesion, cambio: () -> Unit) {
        contenedor.removeAllViews()
        for (grupo in productos.chunked(2)) {
            val fila=LinearLayout(activity).apply { orientation=LinearLayout.HORIZONTAL; isBaselineAligned=false }
            contenedor.addView(fila, LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=(12*activity.resources.displayMetrics.density).toInt() })
            for (p in grupo) {
                val b=LpTarjetaProductoBinding.inflate(activity.layoutInflater, fila, false)
                fila.addView(b.root, LinearLayout.LayoutParams(0,-2,1f).apply { setMargins(4,0,4,0) })
                b.imgProducto.setImageResource(p.imagen); b.imgProducto.contentDescription=p.nombre()
                b.lblNombreProducto.text="${p.marca}\n${p.modelo}"
                b.lblMedidaProducto.text=p.medida
                b.lblPrecioProducto.text=moneda(p.precioFinal())
                b.lblOferta.visibility=if(p.descuento>0) View.VISIBLE else View.GONE
                b.lblOferta.text="-${p.descuento}% · antes ${moneda(p.precioCentavos)}"
                fun favorito() {
                    val activo=sesion.favorito(p.id)
                    b.btnFavorito.imageTintList=ColorStateList.valueOf(activity.getColor(if(activo) R.color.lp_rojo else R.color.lp_gris))
                    b.btnFavorito.contentDescription=if(activo) "Quitar de favoritos" else "Agregar a favoritos"
                }
                favorito()
                b.btnFavorito.setOnClickListener { sesion.cambiarFavorito(p.id); favorito(); cambio() }
                b.btnAgregarCarrito.setOnClickListener {
                    val agregado=sesion.agregar(p.id)
                    Toast.makeText(activity,if(agregado) "Agregado al carrito" else "Máximo 24 unidades por producto",Toast.LENGTH_SHORT).show(); cambio()
                }
                val detalle=View.OnClickListener { activity.startActivity(Intent(activity, DetalleActivity::class.java).putExtra("producto",p.id)) }
                b.imgProducto.setOnClickListener(detalle); b.lblNombreProducto.setOnClickListener(detalle)
            }
            if(grupo.size==1) fila.addView(View(activity),LinearLayout.LayoutParams(0,1,1f))
        }
    }
}
