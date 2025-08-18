package com.irisaco.iotequipment.ui.support

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.FragmentSettingsBinding
import com.irisaco.iotequipment.databinding.FragmentSupportBinding

class SupportFragment : Fragment() {

    private var _binding : FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupportBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setQuestionType()
    }

    private fun setQuestionType(){
        binding.autoCompleteQuestionType.setDropDownBackgroundResource(R.color.md_theme_background)
        val questionType = resources.getStringArray(R.array.array_question_type)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,questionType)
        binding.autoCompleteQuestionType.setAdapter(arrayAdapter)


        binding.autoCompleteQuestionType.apply {
            setAdapter(arrayAdapter)

            post { setText(getString(R.string.support_question_type), false) }

            setOnItemClickListener { parent, _, position, _ ->
                val selected = parent.getItemAtPosition(position).toString()
                setText(selected, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}