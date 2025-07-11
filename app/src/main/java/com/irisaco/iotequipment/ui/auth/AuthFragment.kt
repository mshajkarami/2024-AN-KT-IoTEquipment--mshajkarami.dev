package com.irisaco.iotequipment.ui.auth

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.irisaco.iotequipment.MainActivity
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {

    private lateinit var btnAuth: MaterialButton
    private lateinit var authViewModel: AuthViewModel
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
                        Log.d("AuthFragment", "ورود با کاربر: ${account?.username}")
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

        val authRepository = AuthRepository(requireContext())
        val factory = AuthViewModelFactory(authRepository, requireActivity().application)


        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        btnAuth.setOnClickListener {
            val loginIntent = authViewModel.startLoginIntent()
            loginLauncher.launch(loginIntent)
        }
    }
}


