package com.irisaco.iotequipment.ui.onboarding

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.irisaco.iotequipment.ui.base.BaseActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class OnboardingActivity : BaseActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)
        enableFullScreenMode()
    }
    private fun enableFullScreenMode() {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}