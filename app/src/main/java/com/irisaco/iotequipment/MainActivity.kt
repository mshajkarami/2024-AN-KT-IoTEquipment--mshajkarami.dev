package com.irisaco.iotequipment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import com.irisaco.iotequipment.ui.auth.AuthViewModelFactory
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.di.MyApplication
import com.irisaco.iotequipment.ui.home.HomeFragmentDirections
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var authViewModel: AuthViewModel

    var equipmentIdUnique: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val authDao = MyApplication.database.authTokenDao()
        val authRepository = AuthRepository(ApiClient.authService, authDao)

        val factory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        // Login

        authViewModel.login("saleh", "123456")

        authViewModel.token.observe(this) { token ->
            if (token != null) {
                Log.d("Auth", "Access Token: ${token.accessToken}")
                lifecycleScope.launch {
                    authRepository.saveToken(token)
                }
            }
        }
        authViewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                Log.e("Auth", "Error is : $error")
            }
        }

        setEquipmentId()

        setFragmentManagerAndToolbar()


    }

    private fun setEquipmentId() {
        equipmentIdUnique = intent.getStringExtra("EQUIPMENT_ID") ?: ""
    }


    private fun setFragmentManagerAndToolbar() {
        toolbar = findViewById(R.id.materialToolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(toolbar, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
                    toolbar.setNavigationOnClickListener {
                    }
                }

                else -> {
                    toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back)
                    toolbar.setNavigationOnClickListener {
                        navController.navigateUp()
                    }
                }
            }

        }
    }
}