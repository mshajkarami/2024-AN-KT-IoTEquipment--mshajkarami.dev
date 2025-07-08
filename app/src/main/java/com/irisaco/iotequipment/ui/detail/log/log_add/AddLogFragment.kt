package com.irisaco.iotequipment.ui.detail.log.log_add

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.model.Records

import com.irisaco.iotequipment.databinding.FragmentAddLogBinding
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import com.irisaco.iotequipment.ui.detail.log.log_detail.LogDetailFragmentDirections
import java.io.File
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import kotlin.getValue

class AddLogFragment : Fragment() {

    private var _binding: FragmentAddLogBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private val viewModel: AddLogViewModel by viewModels()
    private val sharedViewModel: EquipmentDetailSharedViewModel by activityViewModels()

    private lateinit var pickFileLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerFilePicker()
        setupToolbar()
        setupDatePicker()
        setupCountdown()
        setupFormMode()
        setupObservers()

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun registerFilePicker() {
        pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val file = uri?.let { uriToFile(requireContext(), it) }
            if (file == null) {
                showToast("هیچ فایلی انتخاب نکردین!")
                return@registerForActivityResult
            }

            val equipmentId = sharedViewModel.equipmentId.value
            val recordId = sharedViewModel.recordEquipmentId.value

            if (equipmentId != null) {
                viewModel.uploadDocument(equipmentId, recordId, file)
            } else {
                showToast("شناسه تجهیز یافت نشد")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupFormMode() {
        val recordToEdit = sharedViewModel.recordToEdit.value
        val equipmentId = sharedViewModel.equipmentId.value

        if (equipmentId == null) {
            showToast("شناسه تجهیز نامعتبر است")
            findNavController().navigateUp()
            return
        }

        recordToEdit?.let {
            binding.etNewLogDes.setText(it.description)
            binding.etNewLogDate.setText(it.creationTime?.substring(0, 10))
            binding.btnAddRecorde.text = "ویرایش لاگ"
            binding.btnAddRecorde.setOnClickListener {
                val updatedRecord = createRecord().copy(id = recordToEdit.id ?: return@setOnClickListener)
                viewModel.updateRecord(equipmentId, recordToEdit.id!!, updatedRecord)
            }
        } ?: run {
            binding.btnAddRecorde.setOnClickListener {
                viewModel.addRecord(equipmentId, createRecord())
            }
        }

        binding.btnAddAttachment.setOnClickListener {
            pickFileLauncher.launch("*/*")
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile("upload_", null, context.cacheDir)
            file.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            file
        } catch (e: Exception) {
            Log.e("URI_TO_FILE", "Error converting Uri to File", e)
            null
        }
    }

    private fun setupObservers() {
        viewModel.addRecordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showToast("لاگ با موفقیت افزوده شد")
                findNavController().navigateUp()
            }.onFailure {
                showToast("خطا در افزودن لاگ: ${it.message}")
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                sharedViewModel.recordToEdit.value = null
                binding.etNewLogDes.text?.clear()
                binding.etNewLogDate.text?.clear()
                showToast("ویرایش با موفقیت انجام شد")
                findNavController().navigateUp()
            }.onFailure {
                showToast("خطا در ویرایش: ${it.message}")
            }
        }

        viewModel.uploadState.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showToast("فایل با موفقیت آپلود شد")
            }.onFailure {
                showToast("آپلود فایل ناموفق بود: ${it.message}")
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.setToolbarTitle(getString(R.string.toolbar_add_log_text))
        setupToolbarMenu()
    }

    private fun setupToolbarMenu() {
        (requireActivity() as? MainActivity)?.setToolbarMenu(
            iconRes = R.drawable.ic_kebab,
            menuRes = R.menu.menu_toolbar_popup_add_log
        ) { item ->
            when (item.itemId) {
                R.id.item_add_attachment -> pickFileLauncher.launch("*/*")
            }
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

    private fun getCurrentIsoDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToIso8601(date: String): String {
        val localDate = LocalDate.parse(date)
        val startOfDay = localDate.atStartOfDay(ZoneOffset.UTC)
        return startOfDay.toInstant().toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.recordToEdit.value = null
        _binding = null
    }
}
