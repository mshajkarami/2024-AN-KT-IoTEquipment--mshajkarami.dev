package com.irisaco.iotequipment.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentFeaturesThemeBinding
import com.irisaco.iotequipment.databinding.FragmentGetStartedBinding
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.core.content.edit
import com.irisaco.iotequipment.utils.TemporarySettings.KEY_ONBOARDING
import com.irisaco.iotequipment.utils.TemporarySettings.PREFS_NAME

class GetStartedFragment : Fragment() {

    private var _binding: FragmentGetStartedBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetStartedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.btnStart.setOnClickListener {
            markOnboardingFinished()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


        viewLifecycleOwner.lifecycleScope.launch {
            val userName = authViewModel.getUserName()
            binding.tvSubtitle.text = getString(R.string.onboarding_description_text,userName)
        }

    }

    private fun markOnboardingFinished() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit {
            putBoolean(KEY_ONBOARDING, true)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}