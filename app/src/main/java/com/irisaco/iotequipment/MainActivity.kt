package com.irisaco.iotequipment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
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
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navView: NavigationView
    private lateinit var toolbarTitleTextView: TextView
    private var noInternetDialog: NoInternetBottomSheet? = null

    private lateinit var authViewModel: AuthViewModel
    private lateinit var networkConnection: NetworkConnectionLiveData

    var equipmentIdUnique: String? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupInsets()
        setupViews()
        observeNetwork()
        setupObservers()
        setupNavigation()
        readIntentExtras()
    }

    private fun setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.materialToolbar)
        toolbarTitleTextView = findViewById(R.id.txt_toolbar_name)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        navController = (supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

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
        NavigationUI.setupWithNavController(navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    setupToolbarNavigation(R.drawable.ic_menu) {
                        drawerLayout.openDrawer(
                            GravityCompat.START
                        )
                    }
                    setDefaultToolbarIcon()
                    setToolbarTitle(getString(R.string.toolbar_default_text))
                }

                R.id.searchFragment -> {
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setDefaultToolbarIcon()
                    setToolbarTitle(getString(R.string.toolbar_default_text))
                }

                R.id.settingsFragment -> {
                    setToolbarTitle(getString(R.string.toolbar_setting_text))
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setToolbarMenu(R.drawable.ic_help_question)
                }

                R.id.attachmentsFragment -> {
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setDefaultToolbarIcon()
                    setupAttachmentMenu()
                }

                R.id.addLogFragment -> {
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setDefaultToolbarIcon()

                    setToolbarTitle(getString(R.string.toolbar_add_log_text))
                    setToolbarMenu(
                        iconRes = R.drawable.ic_kebab,
                        menuRes = R.menu.menu_toolbar_popup_add_log
                    ) { item ->
                        when (item.itemId) {
                            R.id.item_add_attachment -> {
                            }
                        }
                        true
                    }
                }

                R.id.equipmentDetailFragment -> {
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setDefaultToolbarIcon()
                    setToolbarTitle(getString(R.string.toolbar_default_text))
                    setDefaultToolbarIcon()
                }

                R.id.logDetailFragment -> {
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setDefaultToolbarIcon()
                }

                else -> {
                    setupToolbarNavigation(R.drawable.ic_back) { navController.navigateUp() }
                    setDefaultToolbarIcon()
                }
            }
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_exit -> {
                    AlertDialog.Builder(this)
                        .setTitle("خروج")
                        .setMessage("آیا مطمئن هستید که می‌خواهید از برنامه خارج شوید؟")
                        .setPositiveButton("بله") { _, _ -> finishAffinity() }
                        .setNegativeButton("خیر", null)
                        .show()
                    true
                }

                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(item, navController)
                    if (handled) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    handled
                }
            }
        }
    }

    private fun setupToolbarNavigation(iconRes: Int, onClick: () -> Unit) {
        toolbar.navigationIcon = ContextCompat.getDrawable(this, iconRes)
        toolbar.setNavigationOnClickListener { onClick() }
    }

    private fun setupAttachmentMenu() {

        setToolbarTitle(getString(R.string.toolbar_attachment_text))
        setToolbarMenu(
            R.drawable.ic_kebab,
            menuRes = R.menu.menu_toolbar_popup_attachment
        ) { item ->
            when (item.itemId) {
                R.id.item_download_all -> Toast.makeText(
                    this,
                    "دانلود همه داده ها ",
                    Toast.LENGTH_SHORT
                ).show()

                R.id.item_refresh -> Toast.makeText(this, "بار گذاری مجدد ", Toast.LENGTH_SHORT)
                    .show()

                R.id.item_share_all -> Toast.makeText(
                    this,
                    "اشتراک گذاری همه داده ها ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }
    }

    private fun setupObservers() {
        val authDao = MyApplication.database.authTokenDao()
        val authRepository = AuthRepository(ApiClient.authService, authDao)

        val factory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        authViewModel.login("saleh", "123456")

        authViewModel.token.observe(this) { token ->
            token?.let {
                Log.d("Auth", "Access Token: ${it.accessToken}")
                lifecycleScope.launch {
                    authRepository.saveToken(it)
                }
            }
        }
        authViewModel.error.observe(this) { error ->
            error?.let {
                Log.e("Auth", "Error is : $it")
            }
        }
    }

    private fun observeNetwork() {
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
    }

    fun setToolbarTitle(title: String) {
        toolbarTitleTextView.text = title
    }

    fun setToolbarMenu(
        iconRes: Int,
        menuRes: Int? = null,
        onMenuItemClick: ((MenuItem) -> Boolean)? = null
    ) {
        toolbar.menu.clear()

        toolbar.inflateMenu(R.menu.toolbar_menu)

        val iconItem = toolbar.menu.findItem(R.id.toolbar_icon_right)
        iconItem?.setIcon(iconRes)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.toolbar_icon_right) {
                val view = toolbar.findViewById<View>(item.itemId)
                if (menuRes != null) {
                    showPopupMenu(toolbar.context, view, menuRes) { clickedItem ->
                        onMenuItemClick?.invoke(clickedItem)
                    }
                } else {
                    onMenuItemClick?.invoke(item)
                }
                true
            } else false
        }
    }

    fun setDefaultToolbarIcon() {
        setToolbarMenu(R.drawable.ic_profile) {
            navController.navigate(R.id.action_global_settingsFragment)
            true
        }
    }

    fun showPopupMenu(
        context: Context,
        anchor: View,
        menuRes: Int,
        onItemClick: (MenuItem) -> Unit
    ) {
        val themedContext = ContextThemeWrapper(context, R.style.CustomPopupMenuStyle)
        val popup = PopupMenu(themedContext, anchor)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            onItemClick(it)
            true
        }

        popup.show()
    }

    private fun readIntentExtras() {
        equipmentIdUnique = intent.getStringExtra("EQUIPMENT_ID")
    }
}
