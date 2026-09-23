package com.irisaco.iotequipment.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.onboarding.OnboardingActivity
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_ONBOARDING
import com.irisaco.iotequipment.utils.TemporarySettings.PREFS_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var btnAuth: MaterialButton
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLoginLauncher()
    }

    override fun onResume() {
        super.onResume()
        enableFullScreenMode()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        btnAuth = view.findViewById(R.id.btn_auth)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupAuthButton()

        authViewModel.checkLoginState()
    }

    override fun onPause() {
        super.onPause()
        disableFullScreenMode()
    }

    private fun setupObservers(){
        authViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            loggedIn ->
            if (loggedIn)
                handleOnboarding()
        }

        authViewModel.authResult.observe(viewLifecycleOwner){
            result ->
            result.onSuccess {
                showToast(getString(R.string.auth_user_welcome))
                authViewModel.checkLoginState()
            }

            result.onFailure { e ->
                showToast("${getString(R.string.auth_login_fail)} ${e.message}")
                Log.e("AuthError", e.message ?: "Unknown error")
            }
        }
    }
    private fun setupAuthButton() {
        btnAuth.setOnClickListener {
            authViewModel.loginWithMockUser()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initLoginLauncher() {
        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data ?: return@registerForActivityResult
            authViewModel.handleTokenExchange(data)
        }
    }
    private fun handleOnboarding() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val onboardingDone = prefs.getBoolean(KEY_ONBOARDING, false)
        if (onboardingDone) {
            navigateToMain()
        } else {
            navigateToOnboarding()
        }
    }
    private fun enableFullScreenMode() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    private fun disableFullScreenMode() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
    private fun navigateToOnboarding() {
        val intent = Intent(requireContext(), OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}