package com.example.scannertecnm

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.scannertecnm.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var binding: ActivityMainBinding
    val db = FirebaseFirestore.getInstance()
    val alumnosRef = db.collection("alumnos")
    val invitadosRef = db.collection("invitados")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnScanner.setOnClickListener { initScanner() }
    }

    private fun initScanner() {
        var integretor= IntentIntegrator(this)
        integretor.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integretor.setPrompt("Escanee su credencial")
        //integretor.setTorchEnabled(true)
        integretor.setBeepEnabled(true)
        integretor.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                //
                val idAlumno = result.contents.toString() // Aquí debes poner el ID del alumno que deseas buscar
                buscarDocumentoPorId(idAlumno)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    fun ingresarncon(view: View){
        val intent= Intent(this,ingresar::class.java).apply { }
        finish()
        startActivity(intent)
    }
    fun mostar(view: View){
        val intent= Intent(this,mostar_lista::class.java).apply { }
        finish()
        startActivity(intent)
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
                Log.d(TAG, "El alumno $nombre $apellido está registrado en la base de datos")
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
                        Log.d(TAG, "Registro de acceso guardado en Firestore")
                        Toast.makeText(this, "El alumno $nombre $apellido se a registrado correctamente", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        // Maneja el fracaso de la operación
                        Log.w(TAG, "Error al guardar el registro del acceso en Firestore", exception)
                        Toast.makeText(this, "Error al guardar el registro del acceso en Firestore", Toast.LENGTH_LONG).show()
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
                        Log.d(TAG, "El invitado $nombre $apellido está registrado en la base de datos")
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
                                Log.d(TAG, "Registro de acceso guardado en Firestore")
                                Toast.makeText(this, "El alumno $nombre $apellido se a registrado correctamente", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { exception ->
                                // Maneja el fracaso de la operación
                                Log.w(TAG, "Error al guardar el registro del acceso en Firestore", exception)
                                Toast.makeText(this, "Error al guardar el registro del acceso en Firestore", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        // El documento no existe en ninguna de las colecciones
                        Log.d(TAG, "El ID $id no está registrado en la base de datos")
                        Toast.makeText(this, "El ID $id no está registrado en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error al buscar el invitado con ID $id", exception)
                    Toast.makeText(this, "Error al buscar el invitado con ID $id", Toast.LENGTH_LONG).show()

                }
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error al buscar el alumno con ID $id", exception)
            Toast.makeText(this, "Error al buscar el alumno con ID $id", Toast.LENGTH_LONG).show()

        }
    }

    fun buscarAlumnoPorId(id: String) {
        alumnosRef.document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // El alumno está registrado en la base de datos
                    //Toast.makeText(this, document.toString(),Toast.LENGTH_LONG).show()
                    val nombre = document.getString("Nombre(s)")
                    val apellido = document.getString("Apellidos")
                    Log.d(TAG, "El alumno $nombre $apellido está registrado en la base de datos")
                    // Crea una referencia a la colección "RegistrosAcceso"
                    val registrosRef = FirebaseFirestore.getInstance().collection("RegistrosAcceso")

                    // En el método buscarAlumnoPorId, después de comprobar que el alumno está registrado en la base de datos
                    // Crea un objeto RegistroAcceso para representar el registro de acceso del alumno
                    val registroAcceso = hashMapOf(
                        "alumnoId" to id,
                        "fecha" to Timestamp.now(),
                        // Agrega cualquier otra información relevante al objeto de registro de acceso
                    )

                    // Guarda el objeto RegistroAcceso en Firestore utilizando el ID del alumno como clave del documento
                    registrosRef.document()
                        .set(registroAcceso)
                        .addOnSuccessListener { documentReference ->
                            // Maneja el éxito de la operación
                            Log.d(TAG, "Registro de acceso guardado en Firestore")
                            Toast.makeText(this, "El alumno $nombre $apellido se a registrado correctamente", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { exception ->
                            // Maneja el fracaso de la operación
                            Log.w(TAG, "Error al guardar el registro del acceso en Firestore", exception)
                            Toast.makeText(this, "Error al guardar el registro del acceso en Firestore", Toast.LENGTH_LONG).show()
                        }

                } else {
                    // El alumno no está registrado en la base de datos
                    Log.d(TAG, "El alumno con ID $id no está registrado en la base de datos")
                    Toast.makeText(this, "El alumno con ID $id no está registrado en la base de datos", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al buscar el alumno con ID $id", exception)
                Toast.makeText(this, "Error al buscar el alumno con ID $id", Toast.LENGTH_LONG).show()

            }
    }

}