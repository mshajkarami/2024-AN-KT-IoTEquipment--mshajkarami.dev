package com.irisaco.iotequipment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import com.irisaco.iotequipment.ui.auth.AuthViewModelFactory
import com.irisaco.iotequipment.data.remote.ApiClient
import com.irisaco.iotequipment.data.repository.AuthRepository
import com.irisaco.iotequipment.di.MyApplication
import com.irisaco.iotequipment.ui.components.NoInternetBottomSheet
import com.irisaco.iotequipment.utils.NetworkConnectionLiveData
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var authViewModel: AuthViewModel
    private lateinit var networkConnection: NetworkConnectionLiveData
    private var noInternetDialog: NoInternetBottomSheet? = null

    var equipmentIdUnique: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        networkConnection = NetworkConnectionLiveData(this)

        networkConnection.observe(this) { isConnected ->
            if (!isConnected && noInternetDialog == null) {
                noInternetDialog = NoInternetBottomSheet()
                noInternetDialog?.show(supportFragmentManager, "NoInternet")
            } else if (isConnected) {
                noInternetDialog?.dismiss()
                noInternetDialog = null
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

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
                Log.e("Auth", "Error is : $error")
            }
        }

        setEquipmentId()

        setFragmentManagerAndToolbar()


    }

    private fun setEquipmentId() {
        equipmentIdUnique = intent.getStringExtra("EQUIPMENT_ID")
        Log.d("EQUIPMENT_ID", equipmentIdUnique.toString())
    }


    private fun setFragmentManagerAndToolbar() {
        toolbar = findViewById(R.id.materialToolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        NavigationUI.setupWithNavController(toolbar, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
                    toolbar.setNavigationOnClickListener {
                        drawerLayout.openDrawer(GravityCompat.START)
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

    fun setToolbarTitle(title: String) {
        findViewById<TextView>(R.id.txt_toolbar_name).text = title
    }

    fun setToolbarMenu(
        iconRes: Int,
        menuRes: Int? = null,
        onMenuItemClick: ((MenuItem) -> Boolean)? = null
    ) {
        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.menu.clear()

        toolbar.inflateMenu(R.menu.toolbar_menu) // منویی فقط با یک آیکون

        val iconItem = toolbar.menu.findItem(R.id.toolbar_icon_right)
        iconItem?.setIcon(iconRes)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.toolbar_icon_right && menuRes != null) {
                val view = toolbar.findViewById<View>(item.itemId)
                showPopupMenu(toolbar.context, view, menuRes) { clickedItem ->
                    onMenuItemClick?.invoke(clickedItem)
                }
                true
            } else false
        }
    }



    fun setDefaultToolbarIcon() {
        setToolbarMenu(R.drawable.ic_profile){ item ->
            Toast.makeText(this, "پروفایل کلیک شد", Toast.LENGTH_SHORT).show()
            true
        }
    }
    fun showPopupMenu(context: Context, anchor: View, menuRes: Int, onItemClick: (MenuItem) -> Unit) {
        val themedContext = ContextThemeWrapper(context, R.style.CustomPopupMenuStyle)
        val popup = PopupMenu(themedContext, anchor)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            onItemClick(it)
            true
        }

        popup.show()
    }

}