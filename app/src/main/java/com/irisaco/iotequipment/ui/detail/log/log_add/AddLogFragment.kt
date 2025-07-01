package com.irisaco.iotequipment.ui.detail.log.log_add

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.irisaco.iotequipment.R
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddLogFragment : Fragment() {
    lateinit var dateEditText : TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateEditText = view.findViewById<TextInputEditText>(R.id.et_date)

        dateEditText.setOnClickListener {

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("تاریخ را انتخاب کنید")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            if (!picker.isAdded) {
                picker.show(parentFragmentManager, "datePicker")
            }

            picker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateEditText.setText(sdf.format(date))
            }
        }
    }
}