package com.irisaco.iotequipment.ui.detail.log.log_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.iwgang.countdownview.CountdownView
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.detail.DetailViewModel
import com.irisaco.iotequipment.ui.detail.EquipmentDetailSharedViewModel

class LogDetailFragment : Fragment() {

    private val vm: EquipmentDetailSharedViewModel by activityViewModels()

    private val viewModel: DetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = requireActivity().application
                return DetailViewModel(app, equipmentId = vm.equipmentId.value ?: -1) as T
            }
        }
    }

    private lateinit var tvDate: TextView
    private lateinit var tvDetail: TextView
    private lateinit var btnAttachment: Button
    private lateinit var countdownView: CountdownView

    private var recordId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_log, container, false)
        tvDate = view.findViewById(R.id.logDate)
        tvDetail = view.findViewById(R.id.logDetail)
        btnAttachment = view.findViewById(R.id.btn_logAttachment)
        countdownView = view.findViewById(R.id.countdownLogView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.toolbar_edit_log_text))
        vm.recordEquipmentId.value?.let { id ->
            recordId = id
            viewModel.getRecordById(recordId).observe(viewLifecycleOwner) { record ->
                record?.let {
                    val createTime = it.creationTime
                    tvDate.text = this.getString(R.string.label_device_date,createTime)
                    val description = it.description
                    tvDetail.text = this.getString(R.string.label_device_description,description)
                    countdownView.start(5 * 60 * 1000)
                }
            }
        }
    }
}
