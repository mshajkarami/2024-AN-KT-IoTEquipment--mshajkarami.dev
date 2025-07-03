package com.irisaco.iotequipment.ui.detail.log.log_add

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import android.os.Bundle
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
        // Inflate the layout for this fragment
        _binding = FragmentAddLogBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etNewLogDate.setOnClickListener {

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("تاریخ را انتخاب کنید")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            if (!picker.isAdded) {
                picker.show(parentFragmentManager, "datePicker")
            }

            picker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etNewLogDate.setText(sdf.format(date))
            }
        }

        binding.btnAddRecorde.setOnClickListener {
            sharedViewModel.equipmentId.observe(viewLifecycleOwner) { equipmentId ->
                if (equipmentId != null) {
                    val record = createRecord()
                    viewModel.addRecord(equipmentId, record)
                } else {
                    Toast.makeText(requireContext(), "شناسه تجهیز معتبر نیست", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.addRecordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "لاگ با موفقیت افزوده شد", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.onFailure {
                Toast.makeText(requireContext(), "خطا در افزودن لاگ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }



        binding.btnAddAttachment.setOnClickListener {

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            documents = arrayListOf(),
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



}