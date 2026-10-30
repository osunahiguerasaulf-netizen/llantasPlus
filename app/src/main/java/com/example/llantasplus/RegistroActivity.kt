package com.example.llantasplus

import android.os.Bundle
import android.util.Patterns
import com.example.llantasplus.databinding.ActivityRegistroBinding
import java.util.concurrent.Executors

class RegistroActivity : PantallaActivity() {
    private lateinit var b: ActivityRegistroBinding
    private val tareas=Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); iniciarComponentes(); eventosClick()
    }
    fun iniciarComponentes() { b=ActivityRegistroBinding.inflate(layoutInflater); iniciar(b.root) }
    fun eventosClick() {
        b.btnVolver.setOnClickListener { finish() }
        b.btnGuardar.setOnClickListener {
            val nombre=b.txtNombre.text.toString().trim(); val usuario=b.txtUsuario.text.toString().trim()
            val correo=b.txtCorreo.text.toString().trim(); val pass=b.txtPassword.text.toString()
            if(nombre.length !in 2..80) { b.txtNombre.error="Escribe un nombre de 2 a 80 caracteres"; return@setOnClickListener }
            if(!usuario.matches(Regex("[a-zA-Z0-9_.-]{3,30}"))) { b.txtUsuario.error="Usa entre 3 y 30 letras, números, puntos o guiones"; return@setOnClickListener }
            if(correo.length>120 || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { b.txtCorreo.error="Correo inválido"; return@setOnClickListener }
            if(pass.length !in 8..128) { b.txtPassword.error="Usa de 8 a 128 caracteres"; return@setOnClickListener }
            if(pass!=b.txtConfirmar.text.toString()) { b.txtConfirmar.error="Las contraseñas no coinciden"; return@setOnClickListener }
            b.btnGuardar.isEnabled=false
            tareas.execute {
                val resultado=runCatching { UsuariosBD(this).use { it.registrar(nombre,usuario,correo,pass) } }
                runOnUiThread {
                    if(isFinishing || isDestroyed) return@runOnUiThread
                    b.btnGuardar.isEnabled=true
                    if(resultado.getOrDefault(false)) { aviso("Cuenta creada. Ya puedes iniciar sesión."); finish() }
                    else aviso(if(resultado.isFailure) "No se pudo guardar la cuenta" else "Ese usuario o correo ya está registrado")
                }
            }
        }
    }
    override fun onDestroy() { tareas.shutdown(); super.onDestroy() }
}
