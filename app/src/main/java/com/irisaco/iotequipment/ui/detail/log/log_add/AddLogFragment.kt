package com.irisaco.iotequipment.ui.detail.log.log_add

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Equipment
import com.irisaco.iotequipment.data.model.Records

import com.irisaco.iotequipment.databinding.FragmentAddLogBinding
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import java.text.ParseException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.getValue

class AddLogFragment : Fragment() {

    private var _binding: FragmentAddLogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddLogViewModel by viewModels()
    private val sharedViewModel: EquipmentDetailSharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = (requireActivity() as MainActivity)

        activity.setToolbarTitle(getString(R.string.toolbar_add_log_text))

        setupToolbarMenu()
        setupDatePicker()
        setupCountdown()
        setupFormMode()
        setupObservers()

        binding.btnCansel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupFormMode() {
        val recordToEdit = sharedViewModel.recordToEdit.value
        val equipmentId = sharedViewModel.equipmentId.value

        if (recordToEdit != null && equipmentId != null) {
            binding.etNewLogDes.setText(recordToEdit.description)
            binding.etNewLogDate.setText(recordToEdit.creationTime?.substring(0, 10))
            binding.btnAddRecorde.text = "ویرایش لاگ"

            binding.btnAddRecorde.setOnClickListener {
                val updatedRecord = createRecord().copy(id = recordToEdit.id)
                viewModel.updateRecord(equipmentId, recordToEdit.id!!, updatedRecord)
            }

        }else if (equipmentId != null){
            binding.btnAddRecorde.setOnClickListener {
                val record = createRecord()
                viewModel.addRecord(equipmentId, record)
            }
        }
        else {
            Toast.makeText(requireContext(), "شناسه تجهیز نامعتبر است", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.btnAddAttachment.setOnClickListener {
            // TODO: پیوست
        }
    }

    private fun setupObservers() {
        viewModel.addRecordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "لاگ با موفقیت افزوده شد", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.onFailure {
                Toast.makeText(requireContext(), "خطا در افزودن لاگ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                sharedViewModel.recordToEdit.value = null
                binding.etNewLogDes.text?.clear()
                binding.etNewLogDate.text?.clear()
                Toast.makeText(requireContext(), "ویرایش با موفقیت انجام شد", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.onFailure {
                Toast.makeText(requireContext(), "خطا در ویرایش: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbarMenu() {
        (requireActivity() as MainActivity).setToolbarMenu(R.drawable.ic_kebab) {
            Toast.makeText(requireContext(), "کلیک شد!", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupCountdown() {
        binding.countdownLogView.start(5 * 60 * 1000)
    }

    private fun setupDatePicker() {
        binding.etNewLogDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("تاریخ را انتخاب کنید")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.show(parentFragmentManager, "datePicker")

            picker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etNewLogDate.setText(sdf.format(date))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRecord(): Records {
        val description = binding.etNewLogDes.text.toString().trim()
        val date = binding.etNewLogDate.text.toString().trim()
        val creationTimeIso = convertToIso8601(date)
        val currentTime = getCurrentIsoDateTime()

        return Records(
            description = description,
            creationTime = creationTimeIso,
            lastModifyTime = currentTime,
            documents = arrayListOf()
        )
    }


    fun getCurrentIsoDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToIso8601(date: String): String {
        val localDate = LocalDate.parse(date)
        val startOfDay = localDate.atStartOfDay(ZoneOffset.UTC)
        return startOfDay.toInstant().toString()
    }


    override fun onResume() {
        super.onResume()
        val activity = (requireActivity() as MainActivity)

        activity.setToolbarMenu(
            R.drawable.ic_kebab
        ) {
            Toast.makeText(activity, "کلیک شد!", Toast.LENGTH_SHORT).show()
            true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.recordToEdit.value = null
        _binding = null
    }

}