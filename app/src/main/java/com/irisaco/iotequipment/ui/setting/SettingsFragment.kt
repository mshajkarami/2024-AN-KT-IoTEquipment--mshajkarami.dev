package com.irisaco.iotequipment.ui.setting

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        val languageLayout = binding.includePreferences.languageLayout
        val languageText = binding.includePreferences.currentLanguageText
        val accountUserNameLayout = binding.includeProfile.txtUserName
        val accountNameLayout = binding.itemAccount.txtDisplayName
        val accountEmailLayout = binding.itemAccount.txtEmailName
        val btnLogout = binding.includePreferences.logoutLayout

        viewLifecycleOwner.lifecycleScope.launch {
            val userName = authViewModel.getUserName()
            val displayName = authViewModel.getDisplayName()
            val emailName = authViewModel.getEmailName()

            setTextOrAnonymous(accountUserNameLayout, userName, R.string.msg_user_anonymous)
            setTextOrFormatted(accountNameLayout, displayName, R.string.setting_display_name, R.string.msg_user_anonymous)
            setTextOrFormatted(accountEmailLayout, emailName, R.string.setting_email, R.string.msg_email_anonymous)
        }

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

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTextOrAnonymous(textView: TextView, value: String?, anonymousResId: Int) {
        textView.text = if (!value.isNullOrEmpty()) value else getString(anonymousResId)
    }

    private fun setTextOrFormatted(textView: TextView, value: String?, formattedResId: Int, anonymousResId: Int) {
        textView.text = if (!value.isNullOrEmpty()) getString(formattedResId, value) else getString(anonymousResId)
    }

    private fun applyLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit { putString("language", lang) }
    }

    private fun showLogoutConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
}