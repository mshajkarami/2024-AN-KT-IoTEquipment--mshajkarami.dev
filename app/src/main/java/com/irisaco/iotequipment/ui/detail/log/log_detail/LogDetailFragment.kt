package com.irisaco.iotequipment.ui.detail.log.log_detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.base.BaseFragment
import com.irisaco.iotequipment.ui.detail.base_view_model.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class LogDetailFragment : BaseFragment() {
    @Inject
    lateinit var assistedFactory: LogDetailViewModelAssistedFactory
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var viewModel: LogDetailViewModel

    private var recordId: Int = -1

    override fun getLayoutId(): Int = R.layout.fragment_detail_log

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val equipmentId = sharedViewModel.equipmentId.value ?: -1
        viewModel = ViewModelProvider(
            this,
            LogDetailViewModelFactory(assistedFactory, equipmentId)
        )[LogDetailViewModel::class.java]


        val tvDate = view.findViewById<TextView>(R.id.logDate)
        val tvDetail = view.findViewById<TextView>(R.id.logDetail)
        val btnAttachment = view.findViewById<MaterialButton>(R.id.btn_logAttachment)

        val activity = requireActivity() as MainActivity
        activity.setToolbarTitle(getString(R.string.log_toolbar_detail))

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
                    Toast.makeText(requireContext(), getString(R.string.no_mvp), Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.item_share -> {
                    viewModel.getRecordById(recordId).observe(viewLifecycleOwner) { record ->
                        record?.let {
                            val formattedDate = formatIsoDateToReadable(it.creationTime.toString())
                            val content =
                                "${getString(R.string.log_hint_date)} $formattedDate\n${getString(R.string.log_hint_description)} ${it.description}"
                            shareLog(content)
                        }
                    }
                }
            }
            true
        }

        sharedViewModel.recordEquipmentId.value?.let { id ->
            recordId = id
            viewModel.getRecordById(recordId).observe(viewLifecycleOwner) { record ->
                record?.let {
                    val formattedDate = formatIsoDateToReadable(it.creationTime.toString())
                    tvDate.text = getString(R.string.equipment_label_date, formattedDate)
                    val description = it.description
                    tvDetail.text =
                        this.getString(R.string.equipment_label_description, description)
                }
            }
        }

        btnAttachment.setOnClickListener {
            findNavController().navigate(LogDetailFragmentDirections.actionLogDetailFragmentToAttachmentsFragment())
        }
    }

    override fun isSwipeRefreshEnabled(): Boolean = false

    fun formatIsoDateToReadable(isoDate: String): String {
        return try {
            val cleanedIso = isoDate.replace(Regex("\\.\\d{6}"), "")

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(cleanedIso) ?: return isoDate

            return if (Locale.getDefault().language == "fa") {
                val persianDate = PersianDate(date.time)
                val persianFormat = PersianDateFormat("Y/m/d")
                persianFormat.format(persianDate)
            } else {
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                outputFormat.format(date)
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    private fun shareLog(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.log_toolbar_detail))
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_with)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val activity = requireActivity() as MainActivity
        activity.setDefaultToolbarIcon()
    }

}