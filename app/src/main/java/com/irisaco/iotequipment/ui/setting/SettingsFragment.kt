package com.irisaco.iotequipment.ui.setting

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.dataStore.LanguageDataStore
import com.irisaco.iotequipment.databinding.FragmentSettingsBinding
import com.irisaco.iotequipment.ui.components.LanguageBottomSheet
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.content.edit

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageLayout = binding.includePreferences.languageLayout
        val languageText = binding.includePreferences.currentLanguageText


        languageLayout.setOnClickListener {
            val bottomSheet = LanguageBottomSheet()
            bottomSheet.onLanguageSelected = { selectedLang ->
                applyLanguage(selectedLang)
                activity?.recreate()
            }
            bottomSheet.show(parentFragmentManager, "LanguageBottomSheet")

        }


        lifecycleScope.launch {
            LanguageDataStore.getLanguageFlow(requireContext()).collect { lang ->
                languageText.text = when (lang) {
                    "en" -> getString(R.string.settings_language_en)
                    "fa" -> getString(R.string.settings_language_fa)
                    else -> getString(R.string.settings_language_fa)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit { putString("language", lang) }

        requireActivity().recreate()
    }

}
