package com.irisaco.iotequipment.ui.setting

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.dataStore.LanguageDataStore
import com.irisaco.iotequipment.databinding.FragmentSettingsBinding
import com.irisaco.iotequipment.ui.components.LanguageBottomSheet
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import com.irisaco.iotequipment.ui.auth.AuthActivity
import com.irisaco.iotequipment.ui.auth.AuthViewModel

class SettingsFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

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

        setupClickListeners()
        observeLanguage()
        loadUserInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() = with(binding) {
        includePreferences.languageLayout.setOnClickListener {
            showLanguageBottomSheet()
        }

        includePreferences.logoutLayout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun observeLanguage() {
        viewLifecycleOwner.lifecycleScope.launch {
            LanguageDataStore.getLanguageFlow(requireContext()).collect { lang ->
                val languageText = when (lang) {
                    "en" -> getString(R.string.settings_language_en)
                    "fa" -> getString(R.string.settings_language_fa)
                    else -> getString(R.string.settings_language_fa)
                }
                binding.includePreferences.currentLanguageText.text = languageText
            }
        }
    }

    private fun loadUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userName = authViewModel.getUserName().orAnonymous(R.string.msg_user_anonymous)
            val displayName = authViewModel.getDisplayName()?.let {
                getString(R.string.setting_display_name, it)
            }.orAnonymous(R.string.msg_user_anonymous)
            val email = authViewModel.getEmailName()?.let {
                getString(R.string.setting_email, it)
            }.orAnonymous(R.string.msg_email_anonymous)

            binding.includeProfile.txtUserName.text = userName
            binding.itemAccount.txtDisplayName.text = displayName
            binding.itemAccount.txtEmailName.text = email
        }
    }

    private fun showLanguageBottomSheet() {
        val bottomSheet = LanguageBottomSheet().apply {
            onLanguageSelected = { selectedLang ->
                applyLanguage(selectedLang)
            }
        }
        bottomSheet.show(parentFragmentManager, "LanguageBottomSheet")
    }

    private fun applyLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration().apply {
            setLocale(locale)
        }

        requireContext()
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .edit { putString("language", lang) }

        requireActivity().recreate()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_dialog_title)
            .setMessage(R.string.logout_dialog_message)
            .setPositiveButton(R.string.logout_dialog_confirm) { _, _ ->
                authViewModel.logout {
                    val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            }
            .setNegativeButton(R.string.logout_dialog_cancel, null)
            .show()
    }

    private fun String?.orAnonymous(resId: Int): String {
        return if (!isNullOrEmpty()) this else getString(resId)
    }

    private fun CharSequence?.orAnonymous(resId: Int): String {
        return if (!isNullOrEmpty()) this.toString() else getString(resId)
    }
}
