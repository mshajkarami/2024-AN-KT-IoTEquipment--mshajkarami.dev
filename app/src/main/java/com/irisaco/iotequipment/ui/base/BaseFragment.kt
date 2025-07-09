package com.irisaco.iotequipment.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.irisaco.iotequipment.R

abstract class BaseFragment : Fragment() {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var baseContent: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val baseView = inflater.inflate(R.layout.fragment_base, container, false)
        swipeRefreshLayout = baseView.findViewById(R.id.swipeRefreshLayout)
        baseContent = baseView.findViewById(R.id.baseContent)

        val contentLayout = inflater.inflate(getLayoutId(), baseContent, false)
        baseContent.addView(contentLayout)

        return baseView
    }

    abstract fun getLayoutId(): Int
}