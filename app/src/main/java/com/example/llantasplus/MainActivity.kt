package com.example.llantasplus

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AlertDialog
import com.example.llantasplus.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : PantallaActivity() {
    private lateinit var b: ActivityMainBinding
    private var visible = false
    private val tareas = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(sesion.usuario().isNotEmpty()) { entrar(); return }
        iniciarComponentes(); eventosClick()
    }
    fun iniciarComponentes() {
        b=ActivityMainBinding.inflate(layoutInflater); iniciar(b.root)
    }
    fun eventosClick() {
        b.btnIngresar.setOnClickListener {
            val usuario=b.txtUsuario.text.toString().trim(); val password=b.txtPassword.text.toString()
            if(usuario.isBlank()) { b.txtUsuario.error="Escribe tu usuario o correo"; return@setOnClickListener }
            if(password.isEmpty()) { b.txtPassword.error="Escribe tu contraseña"; return@setOnClickListener }
            b.btnIngresar.isEnabled=false
            tareas.execute {
                val resultado = runCatching { UsuariosBD(this).use { it.ingresar(usuario,password) } }
                runOnUiThread {
                    if(isFinishing || isDestroyed) return@runOnUiThread
                    b.btnIngresar.isEnabled=true
                    val id=resultado.getOrNull()
                    if(id!=null) { sesion.iniciar(id); entrar() }
                    else aviso(if(resultado.isFailure) "No se pudo abrir la cuenta. Intenta de nuevo." else "Usuario o contraseña incorrectos")
                }
            }
        }
        b.btnRegistrar.setOnClickListener { startActivity(Intent(this, RegistroActivity::class.java)) }
        b.btnSalir.setOnClickListener { finishAffinity() }
        b.btnVerPassword.setOnClickListener {
            visible=!visible
            b.txtPassword.transformationMethod=if(visible) null else PasswordTransformationMethod.getInstance()
            b.txtPassword.setSelection(b.txtPassword.text.length)
            b.btnVerPassword.contentDescription=if(visible) "Ocultar contraseña" else "Mostrar contraseña"
        }
        b.lblRecuperar.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Cuentas en este dispositivo")
                .setMessage("Las cuentas se guardan únicamente en este teléfono. No hay recuperación por correo ni sincronización. Si olvidaste tu contraseña puedes registrar otra cuenta con un usuario y correo diferentes.")
                .setPositiveButton("Crear otra cuenta") { _,_-> startActivity(Intent(this, RegistroActivity::class.java)) }
                .setNegativeButton("Cerrar",null).show()
        }
    }
    private fun entrar() { startActivity(Intent(this, PrincipalActivity::class.java)); finish() }
    override fun onDestroy() { tareas.shutdown(); super.onDestroy() }
}
