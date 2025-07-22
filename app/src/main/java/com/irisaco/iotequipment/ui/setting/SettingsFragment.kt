package com.irisaco.iotequipment.ui.setting

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
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
            val bottomSheet = LanguageBottomSheet { selectedLang ->
                applyLanguage(selectedLang)
                activity?.recreate()
            }
            bottomSheet.show(parentFragmentManager, "LanguageBottomSheet")
        }

        lifecycleScope.launch {
            LanguageDataStore.getLanguageFlow(requireContext()).collect { lang ->
                languageText.text = when (lang) {
                    "en" -> "English"
                    "fa" -> "فارسی"
                    else -> "فارسی"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyLanguage(lang: String) {
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", lang).apply()

        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
    }
}
