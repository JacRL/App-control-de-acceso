package com.example.scannertecnm

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mostar_lista.*
import java.text.SimpleDateFormat
import java.util.*


class mostar_lista : AppCompatActivity() {
    var tl_regsitros:TableLayout?=null
    var rd_todos: RadioButton?=null
    var rd_alum: RadioButton?=null
    var rd_ext: RadioButton?=null
    var radioG: RadioGroup?=null
    var tipo: String? =null
    var fecha_calen: Date? = null
    val firestore = FirebaseFirestore.getInstance()
    var fecha_calen2: Date? = null


    val fila=null
    lateinit var binding: mostar_lista
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostar_lista)
        tl_regsitros=findViewById(R.id.tl_registros)
        radioG=findViewById(R.id.rd_group)
        rd_todos=findViewById(R.id.rd_todos)
        rd_alum=findViewById(R.id.rd_alumnos)
        rd_ext=findViewById(R.id.rd_exter)
        llernar_tabla2()
        edT_fecha.setOnClickListener { showDatePickFragment() }
    }

    fun llernar_tabla2 (){
        var tbRow=TableRow(this)
        tbRow=tab_row
        tl_regsitros?.removeAllViews()
        tl_regsitros?.addView(tbRow)
        val registrosRef = firestore.collection("RegistrosAcceso")
        // Obtiene los registros de hoy
        val hoy = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val inicioHoy = hoy.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time
        val finHoy = hoy.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.time
        val query = registrosRef.whereGreaterThan("fecha", inicioHoy).whereLessThan("fecha", finHoy)
        // Realiza la consulta
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Crea una nueva fila
                    //val tableRow = TableRow(this)
                    val registro=LayoutInflater.from(this).inflate(R.layout.item_layout_regis,null,false)
                    val tv_id=registro.findViewById<View>(R.id.tv_control2) as TextView
                    val tv_nombre=registro.findViewById<View>(R.id.tv_name2)as TextView
                    val tv_apellido=registro.findViewById<View>(R.id.tv_apellido2)as TextView
                    val tv_dia=registro.findViewById<View>(R.id.tv_dia2)as TextView
                    // Crea una nueva celda para el número de control
                    tv_id.setText(document.getString("NoID"))
                    // Crea una nueva celda para el nombre del alumno
                    tv_nombre.setText(document.getString("Nombre(s)"))
                    tv_apellido.setText(document.getString("Apellidos"))
                    val fecha = document.getTimestamp("fecha")?.toDate()
                    tv_dia.setText(SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fecha))
                    tl_regsitros?.addView(registro)
                }

                firestore.clearPersistence()
            }
            .addOnFailureListener { exception ->
                // Maneja el fracaso de la operación
                Log.w(TAG, "Error al obtener los registros del día de hoy", exception)
                Toast.makeText(this, "Error al obtener los registros del día de hoy", Toast.LENGTH_LONG).show()
            }
    }
    fun regre(view: View){
        val intent= Intent(this,MainActivity::class.java).apply { }
        finish()
        startActivity(intent)
    }
    private fun showDatePickFragment(){
        val datePicker= DatePickFragment{day,mouth,year -> onDateSelected(day,mouth,year)}
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDateSelected(day:Int, mouth:Int,year:Int) {
        var mes=String.format("%02d", mouth+1)
        var fecha=day.toString()+"/"+mes+"/"+year
        edT_fecha.setText(fecha)
        //fecha_calen=fecha
        val fechaEspecifica1 = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, mouth)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fecha_calen = fechaEspecifica1.time
        val fechaEspecifica2 = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, mouth)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 59)
        }

        fecha_calen2 = fechaEspecifica2.time
    }
    fun buscar(view: View) {
        var tbRow=TableRow(this)
        tbRow=tab_row
        tl_regsitros?.removeAllViews()
        tl_regsitros?.addView(tbRow)
        var dia1 = fecha_calen
        var dia2= fecha_calen2
        if (edT_fecha?.text.toString().isEmpty()){
            val registrosRef = firestore.collection("RegistrosAcceso")
            val hoy = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
            }
            val iHoy = hoy.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time
            val fHoy = hoy.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.time

            // Realiza la consulta
            val query = registrosRef
                .whereGreaterThan("fecha", iHoy).whereLessThan("fecha", fHoy)
                //.whereEqualTo("tipo", tipo)
            query.get()
                .addOnSuccessListener { documentos ->
                    for (documento in documentos) {
                        // Crea una nueva fila
                        if(documento.getString("tipo")==tipo || tipo==null){
                            val registro=LayoutInflater.from(this).inflate(R.layout.item_layout_regis,null,false)
                            val tv_id=registro.findViewById<View>(R.id.tv_control2) as TextView
                            val tv_nombre=registro.findViewById<View>(R.id.tv_name2)as TextView
                            val tv_apellido=registro.findViewById<View>(R.id.tv_apellido2)as TextView
                            val tv_dia=registro.findViewById<View>(R.id.tv_dia2)as TextView
                            // Crea una nueva celda para el número de control
                            tv_id.setText(documento.getString("NoID"))
                            // Crea una nueva celda para el nombre del alumno
                            tv_nombre.setText(documento.getString("Nombre(s)"))
                            tv_apellido.setText(documento.getString("Apellidos"))
                            val fecha = documento.getTimestamp("fecha")?.toDate()
                            tv_dia.setText(SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fecha))
                            tl_regsitros?.addView(registro)
                        }
                    }

                }
                .addOnFailureListener { exception ->
                    // Maneja el fracaso de la operación
                    Log.w(TAG, "Error al obtener los registros del día de hoy", exception)
                    Toast.makeText(this, "Error al obtener los registros en el dia: "+fecha_calen, Toast.LENGTH_LONG).show()

                }
        }else{
            if (tipo==null) {
                val registrosRef = firestore.collection("RegistrosAcceso")
                val inicioHoy = dia1?.let { Timestamp(it) }
                val finHoy = dia2?.let { Timestamp(it) }
                val query = registrosRef.whereGreaterThan("fecha", inicioHoy!!).whereLessThan("fecha",
                    finHoy!!
                )
                query.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Crea una nueva fila
                            //val tableRow = TableRow(this)
                            val registro=LayoutInflater.from(this).inflate(R.layout.item_layout_regis,null,false)
                            val tv_id=registro.findViewById<View>(R.id.tv_control2) as TextView
                            val tv_nombre=registro.findViewById<View>(R.id.tv_name2)as TextView
                            val tv_apellido=registro.findViewById<View>(R.id.tv_apellido2)as TextView
                            val tv_dia=registro.findViewById<View>(R.id.tv_dia2)as TextView
                            // Crea una nueva celda para el número de control
                            tv_id.setText(document.getString("NoID"))
                            // Crea una nueva celda para el nombre del alumno
                            tv_nombre.setText(document.getString("Nombre(s)"))
                            tv_apellido.setText(document.getString("Apellidos"))
                            val fecha = document.getTimestamp("fecha")?.toDate()
                            tv_dia.setText(SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fecha))

                            tl_regsitros?.addView(registro)
                        }

                    }
                    .addOnFailureListener { exception ->
                        // Maneja el fracaso de la operación
                        Log.w(TAG, "Error al obtener los registros del día de hoy", exception)
                        Toast.makeText(this, "Error al obtener los registros en el dia: "+fecha_calen.toString(), Toast.LENGTH_LONG).show()

                    }
            } else {
                val registrosRef = firestore.collection("RegistrosAcceso")
                val inicioHoy = dia1?.let { Timestamp(it) }
                val finHoy = dia2?.let { Timestamp(it) }
                //Toast.makeText(this, tipo, Toast.LENGTH_LONG).show()

                val query = registrosRef
                    .whereGreaterThan("fecha", inicioHoy!!).whereLessThan("fecha",
                        finHoy!!
                    )
                    //.whereEqualTo("tipo", tipo)

                query.get()
                    .addOnSuccessListener { documentos ->
                        for (documento in documentos) {
                            // Crea una nueva fila
                            //val tableRow = TableRow(this)
                            if(documento.getString("tipo")==tipo){
                                val registro=LayoutInflater.from(this).inflate(R.layout.item_layout_regis,null,false)
                                val tv_id=registro.findViewById<View>(R.id.tv_control2) as TextView
                                val tv_nombre=registro.findViewById<View>(R.id.tv_name2)as TextView
                                val tv_apellido=registro.findViewById<View>(R.id.tv_apellido2)as TextView
                                val tv_dia=registro.findViewById<View>(R.id.tv_dia2)as TextView
                                // Crea una nueva celda para el número de control
                                tv_id.setText(documento.getString("NoID"))
                                // Crea una nueva celda para el nombre del alumno
                                tv_nombre.setText(documento.getString("Nombre(s)"))
                                tv_apellido.setText(documento.getString("Apellidos"))
                                val fecha = documento.getTimestamp("fecha")?.toDate()
                                tv_dia.setText(SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fecha))
                                tl_regsitros?.addView(registro)
                            }

                        }

                    }
                    .addOnFailureListener { exception ->
                        // Maneja el fracaso de la operación
                        Log.w(TAG, "Error al obtener los registros del día de hoy", exception)
                        Toast.makeText(this, "Error al obtener los registros en el dia: "+fecha_calen, Toast.LENGTH_LONG).show()

                    }
            }
        }
    }
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            when (view.getId()) {
                R.id.rd_todos ->
                    if (checked) {
                        tipo=null
                    }
                R.id.rd_alumnos ->
                    if (checked) {
                        tipo="alumno"
                    }
                R.id.rd_exter ->
                    if (checked) {
                        tipo="invitado"
                    }
            }
        }
    }
}