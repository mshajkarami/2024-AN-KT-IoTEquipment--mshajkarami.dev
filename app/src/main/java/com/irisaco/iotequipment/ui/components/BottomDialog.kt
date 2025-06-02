package com.irisaco.iotequipment.ui.components

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.MainActivity

class BottomDialog : BottomSheetDialogFragment() {
    private lateinit var barcodeId : TextView
    private lateinit var btnVisit : Button
    private lateinit var btnCancel : Button

    private var equipmentId: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.button_dialog_barcode,container,false)

        barcodeId = view.findViewById(R.id.txt_barcode)
        btnVisit = view.findViewById(R.id.btn_visit)
        btnCancel = view.findViewById(R.id.btn_cansel)

        barcodeId.text = equipmentId

        btnVisit.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("EQUIPMENT_ID",equipmentId)
            startActivity(intent)
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        return view
    }
    fun setEquipmentId(id: String) {
        equipmentId = id
    }
}