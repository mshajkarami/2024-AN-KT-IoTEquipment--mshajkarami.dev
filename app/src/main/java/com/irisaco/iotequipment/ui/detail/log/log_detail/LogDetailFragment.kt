package com.irisaco.iotequipment.ui.detail.log.log_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.base.BaseFragment
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Suppress("UNCHECKED_CAST")
class LogDetailFragment : BaseFragment() {
    private val sharedViewModel: EquipmentDetailSharedViewModel by activityViewModels()
    private val viewModel: LogDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = requireActivity().application
                return LogDetailViewModel(
                    app,
                    equipmentId = sharedViewModel.equipmentId.value ?: -1
                ) as T
            }
        }
    }
    private var recordId: Int = -1

    override fun getLayoutId(): Int = R.layout.fragment_detail_log

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDate = view.findViewById<TextView>(R.id.logDate)
        val tvDetail = view.findViewById<TextView>(R.id.logDetail)
        val btnAttachment = view.findViewById<MaterialButton>(R.id.btn_logAttachment)

        val activity = requireActivity() as MainActivity
        activity.setToolbarTitle(getString(R.string.toolbar_detail_log_text))

        observeDeleteResult()

        activity.setToolbarMenu(
            iconRes = R.drawable.ic_kebab,
            menuRes = R.menu.menu_toolbar_popup_detail_log
        ) { item ->
            when (item.itemId) {

                R.id.item_edit -> {
                    viewModel.getRecordById(recordId).observe(viewLifecycleOwner) { record ->
                        record?.let {
                            sharedViewModel.recordToEdit.value = it
                            findNavController().navigate(LogDetailFragmentDirections.actionLogDetailFragmentToAddLogFragment())
                        }
                    }
                }

                R.id.item_print -> {

                }

                R.id.item_share -> {

                }

                R.id.item_delete -> {
                    showDeleteConfirmationDialog()

                }
            }
            true
        }

        sharedViewModel.recordEquipmentId.value?.let { id ->
            recordId = id
            viewModel.getRecordById(recordId).observe(viewLifecycleOwner) { record ->
                record?.let {
                    val formattedDate = formatIsoDateToReadable(it.creationTime.toString())
                    tvDate.text = getString(R.string.label_device_date, formattedDate)
                    val description = it.description
                    tvDetail.text = this.getString(R.string.label_device_description, description)
                }
            }
        }

        btnAttachment.setOnClickListener {
            findNavController().navigate(LogDetailFragmentDirections.actionLogDetailFragmentToAttachmentsFragment())
        }
    }

    private fun observeDeleteResult() {
        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "لاگ با موفقیت حذف شد", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.onFailure {
                Toast.makeText(requireContext(), "خطا در حذف: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun formatIsoDateToReadable(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(isoDate)

            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            isoDate
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف لاگ")
            .setMessage("آیا از حذف این لاگ مطمئن هستید؟ این عملیات غیرقابل بازگشت است.")
            .setPositiveButton("حذف") { _, _ ->
                performDelete()
            }
            .setNegativeButton("انصراف", null)
            .show()
    }

    private fun performDelete() {
        val equipmentId = sharedViewModel.equipmentId.value
        if (equipmentId != null && recordId != -1) {
            viewModel.deleteRecord(equipmentId, recordId)
        } else {
            Toast.makeText(requireContext(), "شناسه‌ها نامعتبر هستند", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val activity = requireActivity() as MainActivity
        activity.setDefaultToolbarIcon()
    }

}




