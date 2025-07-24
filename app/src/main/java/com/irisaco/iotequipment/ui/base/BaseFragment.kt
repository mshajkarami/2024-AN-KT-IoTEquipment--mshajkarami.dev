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

    // Views
    protected lateinit var swipeRefreshLayout: SwipeRefreshLayout
    protected lateinit var baseContent: FrameLayout
    private lateinit var noInternetLayout: View
    private lateinit var actualContentLayout: View
    private lateinit var emptyDataLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val baseView = inflater.inflate(R.layout.fragment_base, container, false)

        // Init views
        swipeRefreshLayout = baseView.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.isEnabled = isSwipeRefreshEnabled()

        baseContent = baseView.findViewById(R.id.baseContent)
        noInternetLayout = baseView.findViewById(R.id.noInternetLayout)
        actualContentLayout = baseView.findViewById(R.id.actualContentLayout)
        emptyDataLayout = baseView.findViewById(R.id.emptyDataLayout)

        // Inflate content layout
        val contentLayout = inflater.inflate(getLayoutId(), baseContent, false)
        baseContent.addView(contentLayout)

        return baseView
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.let { mainActivity ->
            onNetworkChanged(mainActivity.isConnectedToInternet)
        }
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
            noInternetLayout.visibility = View.GONE
            showEmptyState(false)
            onInternetRestored()
        } else {
            noInternetLayout.visibility = View.VISIBLE
            actualContentLayout.visibility = View.GONE
            emptyDataLayout.visibility = View.GONE
        }
    }

    protected fun showEmptyState(isEmpty: Boolean) {
        emptyDataLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        actualContentLayout.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    protected abstract fun getLayoutId(): Int

    protected open fun onInternetRestored() {}
    protected open fun isSwipeRefreshEnabled(): Boolean = true

}
