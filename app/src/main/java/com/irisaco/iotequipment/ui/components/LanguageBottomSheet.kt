package com.irisaco.iotequipment.ui.components

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.content.edit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_LANGUAGE
import com.irisaco.iotequipment.utils.TemporarySettings.PREFS_NAME

class LanguageBottomSheet : BottomSheetDialogFragment() {

    var onLanguageSelected: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_language, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val radioGroup = view.findViewById<RadioGroup>(R.id.languageRadioGroup)
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val currentLang = prefs.getString(KEY_LANGUAGE, "fa") ?: "fa"
        when (currentLang) {
            "fa" -> radioGroup.check(R.id.radio_fa)
            "en" -> radioGroup.check(R.id.radio_en)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedLang = when (checkedId) {
                R.id.radio_fa -> "fa"
                R.id.radio_en -> "en"
                else -> return@setOnCheckedChangeListener
            }

            prefs.edit { putString(KEY_LANGUAGE, selectedLang) }
            onLanguageSelected?.invoke(selectedLang)
            dismiss()
        }
    }
}