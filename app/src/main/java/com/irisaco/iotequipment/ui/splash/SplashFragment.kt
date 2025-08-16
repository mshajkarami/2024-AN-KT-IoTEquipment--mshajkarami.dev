package com.irisaco.iotequipment.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import com.irisaco.iotequipment.ui.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_ONBOARDING
import com.irisaco.iotequipment.utils.TemporarySettings.PREFS_NAME

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.postDelayed({
            authViewModel.checkLoginState()
        }, 2000)

        authViewModel.isLoggedInLiveData.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn == true) {
                if (shouldShowOnboarding()) {
                    navigateToOnboarding()
                } else {
                    navigateToMain()
                }
                navigateToMain()
            } else {
                navigateToAuth()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToAuth() {
        findNavController().navigate(R.id.action_splashFragment_to_authFragment)
    }

    private fun navigateToOnboarding() {
        val intent = Intent(requireContext(), OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun shouldShowOnboarding(): Boolean {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !sharedPref.getBoolean(KEY_ONBOARDING, false)
    }

}