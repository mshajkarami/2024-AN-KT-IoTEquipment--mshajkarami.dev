package com.irisaco.iotequipment.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var btnAuth: MaterialButton
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data ?: return@registerForActivityResult

            authViewModel.handleTokenExchange(data) { success, error ->
                if (success) {
                    Toast.makeText(requireContext(), "خوش آمدید", Toast.LENGTH_SHORT).show()

                    viewLifecycleOwner.lifecycleScope.launch {
                        val account = authViewModel.getActiveAccount()
                        Log.d("AuthFragment", "login with user name : ${account?.username}")
                    }

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {
                    Toast.makeText(requireContext(), "ورود ناموفق بود: $error", Toast.LENGTH_SHORT).show()
                    Log.e("AuthError", error ?: "Unknown error")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        btnAuth = view.findViewById(R.id.btn_auth)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.loadActiveAccountFromDb()
            val isLoggedIn = authViewModel.isLoggedIn()
            if (isLoggedIn) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        btnAuth.setOnClickListener {
            val loginIntent = authViewModel.startLoginIntent()
            loginLauncher.launch(loginIntent)
        }
    }

}


