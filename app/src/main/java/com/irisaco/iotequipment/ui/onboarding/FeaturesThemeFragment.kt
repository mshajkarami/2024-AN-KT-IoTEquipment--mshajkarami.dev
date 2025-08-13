package com.irisaco.iotequipment.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentFeaturesThemeBinding

class FeaturesThemeFragment : Fragment() {

    private var _binding: FragmentFeaturesThemeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeaturesThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.theme.observe(viewLifecycleOwner) { theme ->
            when (theme) {
                "light" -> binding.chipGroupTheme.check(binding.chipLight.id)
                "dark" -> binding.chipGroupTheme.check(binding.chipDark.id)
            }
        }

        setupViews()

    }

    private fun setupViews() {

        binding.chipGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val selectedTheme = when (checkedId) {
                binding.chipLight.id -> "light"
                binding.chipDark.id -> "dark"
                else -> null
            }
            selectedTheme?.let { viewModel.setTheme(it) }
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_featuresThemeFragment_to_getStartedFragment)
        }
        binding.tvSkip.setOnClickListener {
            findNavController().navigate(R.id.action_featuresThemeFragment_to_getStartedFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}