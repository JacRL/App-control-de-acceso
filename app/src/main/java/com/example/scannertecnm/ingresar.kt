package com.example.scannertecnm

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant

class ingresar : AppCompatActivity() {
    var editControl:EditText?=null
    val db = FirebaseFirestore.getInstance()
    val alumnosRef = db.collection("alumnos")
    val invitadosRef = db.collection("invitados")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresar)
        editControl=findViewById<EditText>(R.id.editControl)
    }
    fun regresar(view: View){
        val intent= Intent(this,MainActivity::class.java).apply { }
        finish()
        startActivity(intent)
    }
    fun  insertar(view:View){
        var ncontrol=editControl?.text.toString()
        buscarDocumentoPorId(ncontrol)
    }
    fun buscarDocumentoPorId(id: String) {
        var tip_nom=""
        alumnosRef.document(id).get().addOnSuccessListener { alumno ->
            if (alumno != null && alumno.exists()) {
                tip_nom="alumno"
                // El alumno está registrado en la base de datos
                //Toast.makeText(this, document.toString(),Toast.LENGTH_LONG).show()
                val nombre = alumno.getString("Nombre(s)")
                val apellido = alumno.getString("Apellidos")
                Log.d(ContentValues.TAG, "El alumno $nombre $apellido está registrado en la base de datos")
                // Crea una referencia a la colección "RegistrosAcceso"
                val registrosRef = FirebaseFirestore.getInstance().collection("RegistrosAcceso")

                // En el método buscarAlumnoPorId, después de comprobar que el alumno está registrado en la base de datos
                // Crea un objeto RegistroAcceso para representar el registro de acceso del alumno
                val registroAcceso = hashMapOf(
                    "NoID" to id,
                    "fecha" to Timestamp.now(),
                    "tipo" to tip_nom,
                    "Nombre(s)" to nombre,
                    "Apellidos" to apellido
                    // Agrega cualquier otra información relevante al objeto de registro de acceso
                )

                // Guarda el objeto RegistroAcceso en Firestore utilizando el ID del alumno como clave del documento
                registrosRef.document()
                    .set(registroAcceso)
                    .addOnSuccessListener { documentReference ->
                        // Maneja el éxito de la operación
                        Log.d(ContentValues.TAG, "Registro de acceso guardado en Firestore")
                        Toast.makeText(this, "El alumno $nombre $apellido se a registrado correctamente", Toast.LENGTH_LONG).show()
                        editControl?.setText("")
                    }
                    .addOnFailureListener { exception ->
                        // Maneja el fracaso de la operación
                        Log.w(ContentValues.TAG, "Error al guardar el registro del acceso en Firestore", exception)
                        Toast.makeText(this, "Error al guardar el registro del acceso en Firestore", Toast.LENGTH_LONG).show()
                        editControl?.setText("")
                    }
            } else {
                // El documento no existe en la colección de alumnos
                // Verificar si existe en la colección de invitados
                invitadosRef.document(id).get().addOnSuccessListener { invitado ->
                    if (invitado != null && invitado.exists()) {
                        tip_nom="invitado"
                        // El invitado está registrado en la base de datos
                        //Toast.makeText(this, document.toString(),Toast.LENGTH_LONG).show()
                        val nombre = invitado.getString("Nombre(s)")
                        val apellido = invitado.getString("Apellidos")
                        Log.d(ContentValues.TAG, "El invitado $nombre $apellido está registrado en la base de datos")
                        // Crea una referencia a la colección "RegistrosAcceso"
                        val registrosRef = FirebaseFirestore.getInstance().collection("RegistrosAcceso")
                        // Crea un objeto RegistroAcceso para representar el registro de acceso del invitado
                        val registroAcceso = hashMapOf(
                            "NoID" to id,
                            "fecha" to Timestamp.now(),
                            "tipo" to tip_nom,
                            "Nombre(s)" to nombre,
                            "Apellidos" to apellido
                            // Agrega cualquier otra información relevante al objeto de registro de acceso
                        )
                        // Guarda el objeto RegistroAcceso en Firestore utilizando el ID del alumno como clave del documento
                        registrosRef.document()
                            .set(registroAcceso)
                            .addOnSuccessListener { documentReference ->
                                // Maneja el éxito de la operación
                                Log.d(ContentValues.TAG, "Registro de acceso guardado en Firestore")
                                Toast.makeText(this, "El alumno $nombre $apellido se a registrado correctamente", Toast.LENGTH_LONG).show()
                                editControl?.setText("")
                            }
                            .addOnFailureListener { exception ->
                                // Maneja el fracaso de la operación
                                Log.w(ContentValues.TAG, "Error al guardar el registro del acceso en Firestore", exception)
                                Toast.makeText(this, "Error al guardar el registro del acceso en Firestore", Toast.LENGTH_LONG).show()
                                editControl?.setText("")
                            }
                    } else {
                        // El documento no existe en ninguna de las colecciones
                        Log.d(ContentValues.TAG, "El ID $id no está registrado en la base de datos")
                        Toast.makeText(this, "El ID $id no está registrado en la base de datos", Toast.LENGTH_LONG).show()
                        editControl?.setText("")
                    }
                }.addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error al buscar el invitado con ID $id", exception)
                    Toast.makeText(this, "Error al buscar el invitado con ID $id", Toast.LENGTH_LONG).show()
                    editControl?.setText("")
                }
            }
        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error al buscar el alumno con ID $id", exception)
            Toast.makeText(this, "Error al buscar el alumno con ID $id", Toast.LENGTH_LONG).show()
            editControl?.setText("")
        }
    }

}