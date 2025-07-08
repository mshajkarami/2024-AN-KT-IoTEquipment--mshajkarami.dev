package com.irisaco.iotequipment.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R

class SettingsFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = (requireActivity() as MainActivity)

        activity.setToolbarTitle("")
        activity.setToolbarMenu(R.drawable.ic_help_question)

    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).setDefaultToolbarIcon()
    }
}