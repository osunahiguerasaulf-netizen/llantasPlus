package com.example.llantasplus

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.llantasplus.databinding.ActivityCatalogoBinding
import java.text.Normalizer
import java.util.Locale

class CatalogoActivity : PantallaActivity() {
    private lateinit var b: ActivityCatalogoBinding
    private lateinit var productos: List<Producto>
    private var categoria="Todas"; private var marca="Todas"; private var medida="Todas"
    private var precio=0; private var descendente=false; private var ofertas=false; private var favoritos=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); if(!verificarSesion()) return
        iniciarComponentes()
        categoria=savedInstanceState?.getString("categoria") ?: intent.getStringExtra("categoria") ?: "Todas"
        marca=savedInstanceState?.getString("marca") ?: "Todas"; medida=savedInstanceState?.getString("medida") ?: "Todas"
        precio=savedInstanceState?.getInt("precio") ?: 0; descendente=savedInstanceState?.getBoolean("descendente") ?: false
        ofertas=savedInstanceState?.getBoolean("ofertas") ?: intent.getBooleanExtra("ofertas",false)
        favoritos=savedInstanceState?.getBoolean("favoritos") ?: intent.getBooleanExtra("favoritos",false)
        b.chkOfertas.isChecked=ofertas; b.chkFavoritos.isChecked=favoritos
        b.txtBuscarCatalogo.setText(savedInstanceState?.getString("busqueda") ?: intent.getStringExtra("busqueda") ?: "")
        eventosClick(); navegacion()
    }
    fun iniciarComponentes() { b=ActivityCatalogoBinding.inflate(layoutInflater); iniciar(b.root); productos=DatosTienda.productos(this) }
    fun eventosClick() {
        b.btnVolver.setOnClickListener { finish() }; b.btnCarrito.setOnClickListener { abrir(CarritoActivity::class.java) }
        val botones=listOf(b.btnTodas,b.btnAuto,b.btnSuv,b.btnCamioneta)
        val categorias=listOf("Todas","Automóvil","SUV","Camioneta")
        botones.forEachIndexed { i, boton -> boton.setOnClickListener { categoria=categorias[i]; mostrar() } }
        b.txtBuscarCatalogo.doAfterTextChanged { mostrar() }
        b.btnMarca.setOnClickListener { elegir("Marca",listOf("Todas")+productos.map { it.marca }.distinct(),marca) { marca=it; mostrar() } }
        b.btnMedida.setOnClickListener { elegir("Medida",listOf("Todas")+productos.map { it.medida }.distinct(),medida) { medida=it; mostrar() } }
        b.btnPrecio.setOnClickListener {
            val opciones=listOf("Todos los precios","Hasta $2,000","Hasta $2,500","Hasta $3,000")
            AlertDialog.Builder(this).setTitle("Precio por llanta").setSingleChoiceItems(opciones.toTypedArray(),precio) { dialog,i -> precio=i; dialog.dismiss(); mostrar() }.setNegativeButton("Cancelar",null).show()
        }
        b.btnOrdenar.setOnClickListener { descendente=!descendente; mostrar() }
        b.chkOfertas.setOnCheckedChangeListener { _,valor -> ofertas=valor; mostrar() }
        b.chkFavoritos.setOnCheckedChangeListener { _,valor -> favoritos=valor; mostrar() }
        b.btnLimpiarFiltros.setOnClickListener {
            categoria="Todas"; marca="Todas"; medida="Todas"; precio=0; descendente=false; ofertas=false; favoritos=false
            b.chkOfertas.isChecked=false; b.chkFavoritos.isChecked=false; b.txtBuscarCatalogo.setText(""); mostrar()
        }
    }
    private fun elegir(titulo: String, opciones: List<String>, actual: String, seleccionar: (String)->Unit) {
        AlertDialog.Builder(this).setTitle(titulo).setSingleChoiceItems(opciones.toTypedArray(),opciones.indexOf(actual)) { dialog,i -> seleccionar(opciones[i]); dialog.dismiss() }.setNegativeButton("Cancelar",null).show()
    }
    private fun normalizar(texto: String) = Normalizer.normalize(texto,Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"").lowercase(Locale.ROOT)
    private fun mostrar() {
        val texto=normalizar(b.txtBuscarCatalogo.text.toString().trim())
        val limite=listOf(Long.MAX_VALUE,200000L,250000L,300000L)[precio]
        val filtrados=productos.filter { p ->
            (categoria=="Todas" || p.categoria==categoria) && (marca=="Todas" || p.marca==marca) &&
            (medida=="Todas" || p.medida==medida) && p.precioFinal()<=limite && (!ofertas || p.descuento>0) &&
            (!favoritos || sesion.favorito(p.id)) && normalizar("${p.nombre()} ${p.medida} ${p.categoria}").contains(texto)
        }.let { if(descendente) it.sortedByDescending { p -> p.precioFinal() } else it.sortedBy { p -> p.precioFinal() } }
        b.lblSinResultados.visibility=if(filtrados.isEmpty()) View.VISIBLE else View.GONE
        b.lblResultados.text="${filtrados.size} productos · precios de demostración"
        b.btnMarca.text=if(marca=="Todas") "Marca ⌄" else marca
        b.btnMedida.text=if(medida=="Todas") "Medida ⌄" else medida
        b.btnPrecio.text=listOf("Precio ⌄","≤ $2,000","≤ $2,500","≤ $3,000")[precio]
        b.btnOrdenar.contentDescription=if(descendente) "Precio de mayor a menor" else "Precio de menor a mayor"
        b.btnOrdenar.rotation=if(descendente) 180f else 0f
        listOf(b.btnTodas,b.btnAuto,b.btnSuv,b.btnCamioneta).forEachIndexed { i, boton ->
            val activo=listOf("Todas","Automóvil","SUV","Camioneta")[i]==categoria
            boton.setBackgroundResource(if(activo) R.drawable.lp_bg_rojo else R.drawable.lp_bg_categoria)
            boton.setTextColor(getColor(if(activo) R.color.lp_blanco else R.color.lp_texto))
        }
        Tarjetas.mostrar(this,b.contenedorProductos,filtrados,sesion) { if(favoritos) mostrar() else actualizarCarrito() }
        actualizarCarrito()
    }
    override fun onResume() { super.onResume(); if(::b.isInitialized) mostrar() }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("categoria",categoria); outState.putString("marca",marca); outState.putString("medida",medida)
        outState.putInt("precio",precio); outState.putBoolean("descendente",descendente)
        outState.putBoolean("ofertas",ofertas); outState.putBoolean("favoritos",favoritos)
        outState.putString("busqueda",b.txtBuscarCatalogo.text.toString()); super.onSaveInstanceState(outState)
    }
}
