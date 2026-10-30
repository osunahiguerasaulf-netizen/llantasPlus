package com.example.llantasplus

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class PantallaActivity : AppCompatActivity() {
    protected lateinit var sesion: Sesion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle=SystemBarStyle.light(Color.TRANSPARENT,Color.BLACK),
            navigationBarStyle=SystemBarStyle.light(Color.WHITE,Color.BLACK)
        )
        sesion = Sesion(this)
    }
    protected fun iniciar(root: View) {
        setContentView(root)
        val l=root.paddingLeft; val t=root.paddingTop; val r=root.paddingRight; val b=root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val espacio = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(l+espacio.left, t+espacio.top, r+espacio.right, b+espacio.bottom)
            insets
        }
    }
    protected fun verificarSesion(): Boolean {
        if(sesion.usuario().isNotEmpty()) return true
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish(); return false
    }
    protected fun abrir(destino: Class<*>) {
        if (javaClass == destino) return
        startActivity(Intent(this, destino).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
    }
    protected fun navegacion() {
        findViewById<View>(R.id.navInicio)?.setOnClickListener { abrir(PrincipalActivity::class.java) }
        findViewById<View>(R.id.navCatalogo)?.setOnClickListener { abrir(CatalogoActivity::class.java) }
        findViewById<View>(R.id.navCarrito)?.setOnClickListener { abrir(CarritoActivity::class.java) }
        findViewById<View>(R.id.navPerfil)?.setOnClickListener { abrir(PerfilActivity::class.java) }
    }
    protected fun actualizarCarrito() {
        findViewById<TextView>(R.id.navCarrito)?.text = "Carrito (${sesion.carrito().values.sum()})"
    }
    protected fun aviso(texto: String) = Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
}
