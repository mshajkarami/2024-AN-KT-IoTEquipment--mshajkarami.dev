package com.irisaco.iotequipment.ui.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnboardingViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.language.observe(viewLifecycleOwner) { lang ->
            when (lang) {
                "en" -> binding.chipGroupLang.check(binding.chipEn.id)
                "fa" -> binding.chipGroupLang.check(binding.chipFa.id)
            }
        }

        binding.chipGroupLang.setOnCheckedChangeListener { _, checkedId ->
            val selectedLang = when (checkedId) {
                binding.chipEn.id -> "en"
                binding.chipFa.id -> "fa"
                else -> null
            }
            selectedLang?.let {
                if (it != viewModel.language.value) {
                    viewModel.setLanguage(it)

                    requireActivity().recreate()

                }
            }
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeThemeFragment_to_featuresThemeFragment)
        }

        binding.tvSkip.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeThemeFragment_to_getStartedFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}