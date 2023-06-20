package com.example.scannertecnm

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickFragment (val listener:(day:Int, month:Int,year:Int)->Unit):DialogFragment(),
    DatePickerDialog.OnDateSetListener{
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayofMonth: Int) {
        listener(dayofMonth,month,year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal=Calendar.getInstance()
        val day=cal.get(Calendar.DAY_OF_MONTH)
        val month=cal.get(Calendar.MONTH)
        val year=cal.get(Calendar.YEAR)
        val picker=DatePickerDialog(activity as Context,this, year,month,day)
        return picker
    }
}