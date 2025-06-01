package com.irisaco.iotequipment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        toolbar = findViewById(R.id.materialToolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as androidx.navigation.fragment.NavHostFragment
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