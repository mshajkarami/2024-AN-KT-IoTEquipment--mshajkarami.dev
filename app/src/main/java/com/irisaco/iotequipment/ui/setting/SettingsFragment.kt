package com.irisaco.iotequipment.ui.setting

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.dataStore.LanguageDataStore
import com.irisaco.iotequipment.ui.components.LanguageBottomSheet
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import com.irisaco.iotequipment.ui.auth.AuthActivity
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import com.irisaco.iotequipment.ui.base.BaseFragment

class SettingsFragment : BaseFragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun getLayoutId(): Int = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeNetworkStatus(view)
        loadUserData(view)
        observeLanguage(view)
        setupListeners(view)
    }

    override fun isSwipeRefreshEnabled(): Boolean = false

    private fun setupViews() {
        // Optional UI styling logic here
    }

    private fun observeNetworkStatus(view: View) {
        val accountStateText = view.findViewById<TextView>(R.id.txtUserState)

        authViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
            accountStateText.text = if (isConnected) {
                getString(R.string.setting_profile_online)
            } else {
                getString(R.string.setting_profile_offline)
            }
        }
    }

    private fun observeLanguage(view: View) {
        val languageText = view.findViewById<TextView>(R.id.currentLanguageText)

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

    private fun loadUserData(view: View) {
        showEmptyState(true)

        val settingsRoot = view.findViewById<View>(R.id.settingsRoot)
        settingsRoot.visibility = View.GONE

        val userNameText = view.findViewById<TextView>(R.id.txtUserName)
        val displayNameText = view.findViewById<TextView>(R.id.txtDisplayName)
        val emailText = view.findViewById<TextView>(R.id.txtEmailName)

        viewLifecycleOwner.lifecycleScope.launch {
            val userName = authViewModel.getUserName()
            val displayName = authViewModel.getDisplayName()
            val emailName = authViewModel.getEmailName()

            setTextOrAnonymous(userNameText, userName, R.string.msg_user_anonymous)
            setTextOrFormatted(displayNameText, displayName, R.string.setting_display_name, R.string.msg_user_anonymous)
            setTextOrFormatted(emailText, emailName, R.string.setting_email, R.string.msg_email_anonymous)

            settingsRoot.visibility = View.VISIBLE
            showEmptyState(false)
        }
    }


    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.languageLayout).setOnClickListener {
            val bottomSheet = LanguageBottomSheet()
            bottomSheet.onLanguageSelected = { selectedLang ->
                applyLanguage(selectedLang)
                activity?.recreate()
            }
            bottomSheet.show(parentFragmentManager, "LanguageBottomSheet")
        }

        view.findViewById<View>(R.id.logoutLayout).setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val noMvpClickListener = View.OnClickListener {
            Toast.makeText(requireActivity(), getString(R.string.no_mvp), Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.themeLayout).setOnClickListener(noMvpClickListener)
        view.findViewById<View>(R.id.termsLayout).setOnClickListener(noMvpClickListener)
        view.findViewById<View>(R.id.supportLayout).setOnClickListener(noMvpClickListener)
        view.findViewById<View>(R.id.passwordLayout).setOnClickListener(noMvpClickListener)
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
}
