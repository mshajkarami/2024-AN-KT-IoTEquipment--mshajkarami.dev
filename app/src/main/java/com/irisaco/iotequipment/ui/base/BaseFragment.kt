package com.irisaco.iotequipment.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.utils.NetworkStatusListener

abstract class BaseFragment : Fragment(), NetworkStatusListener {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var baseContent: FrameLayout
    private lateinit var noInternetLayout: View
    private lateinit var actualContentLayout: View

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

        noInternetLayout = baseView.findViewById(R.id.noInternetLayout)
        actualContentLayout = baseView.findViewById(R.id.actualContentLayout)

        return baseView
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as? MainActivity
        activity?.let {
            onNetworkChanged(it.isConnectedToInternet)
        }
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
            noInternetLayout.visibility = View.GONE
            actualContentLayout.visibility = View.VISIBLE

            onInternetRestored()

        } else {
            noInternetLayout.visibility = View.VISIBLE
            actualContentLayout.visibility = View.GONE
        }
    }

    abstract fun getLayoutId(): Int

    protected open fun onInternetRestored() {
    }
}

