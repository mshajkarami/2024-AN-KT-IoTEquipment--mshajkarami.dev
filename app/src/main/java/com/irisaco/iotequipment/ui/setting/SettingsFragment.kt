package com.irisaco.iotequipment.ui.setting

import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaFormat.KEY_LANGUAGE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.dataStore.AppSettingsDataStore
import com.irisaco.iotequipment.ui.components.LanguageBottomSheet
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.databinding.FragmentFeaturesThemeBinding
import com.irisaco.iotequipment.databinding.FragmentSettingsBinding
import com.irisaco.iotequipment.ui.auth.AuthActivity
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_THEME
import com.irisaco.iotequipment.utils.TemporarySettings.PREFS_NAME

class SettingsFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNetworkStatus(view)
        loadUserData(view)
        observeLanguage(view)
        setupListeners()
    }



    private fun observeNetworkStatus(view: View) {
        val accountStateText = view.findViewById<TextView>(R.id.txtUserState)

        authViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
            accountStateText.text = if (isConnected) {
                getString(R.string.settings_profile_online)
            } else {
                getString(R.string.settings_profile_offline)
            }
        }
    }

    private fun observeLanguage(view: View) {
        val languageText = view.findViewById<TextView>(R.id.currentLanguageText)

        lifecycleScope.launch {
            AppSettingsDataStore.getLanguageFlow(requireContext()).collect { lang ->
                languageText.text = when (lang) {
                    "en" -> getString(R.string.settings_language_en)
                    "fa" -> getString(R.string.settings_language_fa)
                    else -> getString(R.string.settings_language_fa)
                }
            }
        }
    }

    private fun observeTheme(view: View) {
        val themeText = view.findViewById<TextView>(R.id.currentThemeText)

        lifecycleScope.launch {
            AppSettingsDataStore.getThemeFlow(requireContext()).collect { theme ->
                themeText.text = when (theme) {
                    "light" -> getString(R.string.settings_theme_light)
                    "dark" -> getString(R.string.settings_theme_dark)
                    else -> getString(R.string.settings_theme_light)
                }
            }
        }
    }

    private fun loadUserData(view: View) {

        val settingsRoot = view.findViewById<View>(R.id.settingsRoot)
        settingsRoot.visibility = View.GONE

        val userNameText = view.findViewById<TextView>(R.id.txtUserName)
        val displayNameText = view.findViewById<TextView>(R.id.txtDisplayName)
        val emailText = view.findViewById<TextView>(R.id.txtEmailName)

        viewLifecycleOwner.lifecycleScope.launch {
            val userName = authViewModel.getUserName()
            val displayName = authViewModel.getDisplayName()
            val emailName = authViewModel.getEmailName()

            setTextOrAnonymous(userNameText, userName, R.string.auth_user_anonymous)
            setTextOrFormatted(displayNameText, displayName, R.string.settings_profile_display_name, R.string.auth_user_anonymous)
            setTextOrFormatted(emailText, emailName, R.string.settings_profile_email, R.string.auth_email_anonymous)

            settingsRoot.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.includePreferences.languageLayout.setOnClickListener {
            val bottomSheet = LanguageBottomSheet()
            bottomSheet.onLanguageSelected = { selectedLang ->
                applyLanguage(selectedLang)
                activity?.recreate()
            }
            bottomSheet.show(parentFragmentManager, "LanguageBottomSheet")
        }

        binding.includePreferences.logoutLayout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val noMvpClickListener = View.OnClickListener {
            Toast.makeText(requireActivity(), getString(R.string.msg_feature_not_available), Toast.LENGTH_SHORT).show()
        }

        binding.includePreferences.themeLayout.setOnClickListener(noMvpClickListener)

        binding.includeHelp.termsLayout.setOnClickListener{
            findNavController().navigate(R.id.action_settingsFragment_to_privacyPolicyFragment)
        }

        binding.includeHelp.supportLayout.setOnClickListener(noMvpClickListener)

        binding.includeAccount.passwordLayout.setOnClickListener(noMvpClickListener)
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

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, lang) }
    }



    private fun applyTheme(themeName : String){

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_THEME, themeName) }

        when (themeName) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.auth_logout_dialog_title)
            .setMessage(R.string.auth_logout_dialog_message)
            .setPositiveButton(R.string.auth_logout_dialog_confirm) { _, _ ->
                authViewModel.logout {
                    val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            }
            .setNegativeButton(R.string.auth_logout_dialog_cancel, null)
            .show()
    }
}
