package com.example.llantasplus

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Locale

class UsuariosBD(context: Context) : SQLiteOpenHelper(context, "usuarios.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios(usuario TEXT PRIMARY KEY, correo TEXT UNIQUE NOT NULL, nombre TEXT NOT NULL, sal TEXT NOT NULL, hash TEXT NOT NULL)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }
    private fun hash(password: String, sal: ByteArray): ByteArray {
        val clave = PBEKeySpec(password.toCharArray(), sal, 120000, 256)
        return try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(clave).encoded }
        finally { clave.clearPassword() }
    }
    fun registrar(nombre: String, usuario: String, correo: String, password: String): Boolean {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val datos = ContentValues().apply {
            put("usuario", usuario.lowercase(Locale.ROOT)); put("correo", correo.lowercase(Locale.ROOT))
            put("nombre", nombre); put("sal", Base64.encodeToString(sal, Base64.NO_WRAP))
            put("hash", Base64.encodeToString(hash(password, sal), Base64.NO_WRAP))
        }
        return writableDatabase.insertWithOnConflict("usuarios", null, datos, SQLiteDatabase.CONFLICT_IGNORE) != -1L
    }
    fun ingresar(identificador: String, password: String): String? {
        val valor = identificador.trim().lowercase(Locale.ROOT)
        readableDatabase.rawQuery("SELECT usuario, sal, hash FROM usuarios WHERE usuario=? OR correo=?", arrayOf(valor, valor)).use { c ->
            if (!c.moveToFirst()) return null
            val valido = MessageDigest.isEqual(hash(password, Base64.decode(c.getString(1), Base64.NO_WRAP)), Base64.decode(c.getString(2), Base64.NO_WRAP))
            return if (valido) c.getString(0) else null
        }
    }
    fun nombre(usuario: String): String {
        readableDatabase.rawQuery("SELECT nombre FROM usuarios WHERE usuario=?", arrayOf(usuario)).use { c ->
            return if(c.moveToFirst()) c.getString(0) else ""
        }
    }
}
