package com.irisaco.iotequipment.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.dataStore.LanguageDataStore
import kotlinx.coroutines.launch

class LanguageBottomSheet : BottomSheetDialogFragment() {

    var onLanguageSelected: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_language, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val radioGroup = view.findViewById<RadioGroup>(R.id.languageRadioGroup)

        viewLifecycleOwner.lifecycleScope.launch {
            val currentLang = LanguageDataStore.getLanguage(requireContext())
            when (currentLang) {
                "fa" -> radioGroup.check(R.id.radio_fa)
                "en" -> radioGroup.check(R.id.radio_en)
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedLang = when (checkedId) {
                R.id.radio_fa -> "fa"
                R.id.radio_en -> "en"
                else -> return@setOnCheckedChangeListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                LanguageDataStore.saveLanguage(requireContext(), selectedLang)
                onLanguageSelected?.invoke(selectedLang)
                dismiss()
            }
        }
    }
}