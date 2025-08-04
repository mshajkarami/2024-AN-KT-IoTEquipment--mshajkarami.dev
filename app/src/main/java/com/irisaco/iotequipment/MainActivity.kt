package com.irisaco.iotequipment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.irisaco.iotequipment.databinding.ActivityMainBinding
import com.irisaco.iotequipment.ui.auth.AuthViewModel
import com.irisaco.iotequipment.ui.components.NoInternetBottomSheet
import com.irisaco.iotequipment.ui.detail.base_view_model.SharedViewModel
import com.irisaco.iotequipment.utils.LocaleHelper
import com.irisaco.iotequipment.utils.NetworkConnectionLiveData
import com.irisaco.iotequipment.utils.NetworkStatusListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var networkConnection: NetworkConnectionLiveData
    private lateinit var navController: NavController
    private var noInternetDialog: NoInternetBottomSheet? = null
    internal var equipmentIdUnique: String? = null
    private lateinit var binding: ActivityMainBinding

    private val shareViewModel: SharedViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()
    internal var isConnectedToInternet: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()

        setupNavigation()

        observeNetwork()

        readIntentExtras()

        setupNavigationHeader()

    }

    private fun setupNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
//        val imageView = headerView.findViewById<ImageView>(R.id.profileImageView)
        val btnChangeThem = headerView.findViewById<ImageButton>(R.id.btnChangeThem)
        val userName = headerView.findViewById<TextView>(R.id.nav_header_name)
        val userState = headerView.findViewById<TextView>(R.id.nav_header_state)

        lifecycleScope.launch {
            userName.text = authViewModel.getUserName()
        }

        btnChangeThem.setOnClickListener {
            Toast.makeText(this, getString(R.string.no_mvp), Toast.LENGTH_SHORT).show()
        }

        authViewModel.networkStatus.observe(this) { isConnected ->
            userState.text = if (isConnected) {
                getString(R.string.setting_profile_online)
            } else {
                getString(R.string.setting_profile_offline)
            }
        }

    }

    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getString("language", "fa") ?: "fa"
        val contextWithLocale = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(contextWithLocale)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.materialToolbar,
            R.string.nav_open,
            R.string.nav_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        NavigationUI.setupWithNavController(binding.materialToolbar, navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen = destination.id == R.id.authFragment
            updateUIForDestination(isAuthScreen)

            when (destination.id) {
                R.id.homeFragment -> configureHomeToolbar()
                R.id.searchFragment -> configureSearchToolbar()
                R.id.settingsFragment -> configureSettingsToolbar()
                R.id.attachmentsFragment -> configureAttachmentsToolbar()
                R.id.addLogFragment -> configureAddLogToolbar()
                R.id.equipmentDetailFragment -> configureEquipmentDetailToolbar()
                R.id.logDetailFragment -> configureLogDetailToolbar()
                else -> configureDefaultToolbar()
            }
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
            val currentFragment =
                navHostFragment?.childFragmentManager?.fragments?.firstOrNull { it.isVisible }

            if (currentFragment is NetworkStatusListener) {
                currentFragment.onNetworkChanged(isConnectedToInternet)
            }

        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_exit -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.menu_exit))
                        .setMessage(getString(R.string.msg_confirm_exit))
                        .setPositiveButton(getString(R.string.yes)) { _, _ -> finishAffinity() }
                        .setNegativeButton(getString(R.string.no), null)
                        .show()
                    true
                }

                R.id.nav_web_view -> {
                    val url = "https://www.irisaco.com/"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    startActivity(intent)
                    true
                }

                R.id.nav_help -> {
                    Toast.makeText(this,
                        getString(R.string.no_mvp),
                        Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_about -> {
                    Toast.makeText(this,
                        getString(R.string.no_mvp),
                        Toast.LENGTH_SHORT).show()
                    true
                }


                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) binding.drawerLayout.closeDrawer(Gravity.LEFT)
                    handled
                }
            }
        }
    }

    private fun updateUIForDestination(isAuthScreen: Boolean) {
        with(binding) {
            if (isAuthScreen) {
                materialToolbar.visibility = View.GONE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                navView.visibility = View.GONE
                guidelineToolbarBottom.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    guideBegin = 0
                }
            } else {
                materialToolbar.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                navView.visibility = View.VISIBLE
                guidelineToolbarBottom.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    guideBegin = 65.dpToPx()
                }
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun configureHomeToolbar() {
        setupToolbarNavigation(R.drawable.ic_menu) {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        setDefaultToolbarIcon()
        setToolbarTitle(getString(R.string.toolbar_default_text))
    }

    private fun configureSearchToolbar() {
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setDefaultToolbarIcon()
        setToolbarTitle(getString(R.string.toolbar_default_text))
    }

    private fun configureSettingsToolbar() {
        setToolbarTitle(getString(R.string.toolbar_setting_text))
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setToolbarMenu(R.drawable.ic_help_question)
    }

    private fun configureAttachmentsToolbar() {
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setDefaultToolbarIcon()
        setupAttachmentMenu()
    }

    private fun configureAddLogToolbar() {
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setDefaultToolbarIcon()
        setToolbarTitle(getString(R.string.log_toolbar_add))
        setToolbarMenu(
            iconRes = R.drawable.ic_kebab,
            menuRes = R.menu.menu_toolbar_popup_add_log
        ) { item ->
            when (item.itemId) {
                R.id.item_add_attachment -> {}
            }
            true
        }
    }

    private fun configureEquipmentDetailToolbar() {
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setDefaultToolbarIcon()
        setToolbarTitle(getString(R.string.toolbar_default_text))
    }

    private fun configureLogDetailToolbar() {
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setDefaultToolbarIcon()
    }

    private fun configureDefaultToolbar() {
        setupToolbarNavigation(R.drawable.ic_arrow_left) { navController.navigateUp() }
        setDefaultToolbarIcon()
    }

    private fun setupToolbarNavigation(iconRes: Int, onClick: () -> Unit) {
        binding.materialToolbar.navigationIcon = ContextCompat.getDrawable(this, iconRes)
        binding.materialToolbar.setNavigationOnClickListener { onClick() }
    }

    private fun setupAttachmentMenu() {
        setToolbarTitle(getString(R.string.toolbar_title_attachments))
        setToolbarMenu(
            R.drawable.ic_kebab,
            menuRes = R.menu.menu_toolbar_popup_attachment
        ) { item ->
            when (item.itemId) {
                R.id.item_download_all -> {
                    shareViewModel.triggerDownloadAll()
                }

                R.id.item_refresh -> {
                    shareViewModel.triggerRefresh()

                }

                R.id.item_share_all -> {
                    shareViewModel.triggerShareAll()
                }
            }
            true
        }
    }

    internal fun setToolbarTitle(title: String) {
        binding.txtToolbarName.text = title
    }

    internal fun setToolbarMenu(
        iconRes: Int,
        menuRes: Int? = null,
        onMenuItemClick: ((MenuItem) -> Boolean)? = null
    ) {
        val toolbar = binding.materialToolbar
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.menu.findItem(R.id.toolbar_icon_right)?.setIcon(iconRes)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.toolbar_icon_right) {
                val view = toolbar.findViewById<View>(item.itemId)
                if (menuRes != null) {
                    showPopupMenu(this, view, menuRes) { clickedItem ->
                        onMenuItemClick?.invoke(clickedItem)
                    }
                } else {
                    onMenuItemClick?.invoke(item)
                }
                true
            } else false
        }
    }

    internal fun setDefaultToolbarIcon() {
        setToolbarMenu(R.drawable.ic_profile) {
            navController.navigate(R.id.action_global_settingsFragment)
            true
        }
    }

    private fun showPopupMenu(
        context: Context,
        anchor: View,
        menuRes: Int,
        onItemClick: (MenuItem) -> Unit
    ) {
        val themedContext = ContextThemeWrapper(context, R.style.CustomPopupMenuStyle)
        PopupMenu(themedContext, anchor).apply {
            menuInflater.inflate(menuRes, menu)
            setOnMenuItemClickListener {
                onItemClick(it)
                true
            }
            show()
        }
    }

    private fun observeNetwork() {
        networkConnection = NetworkConnectionLiveData(this)

        networkConnection.observe(this) { isConnected ->
            isConnectedToInternet = isConnected

            authViewModel.updateNetworkStatus(isConnected)

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment

            val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull {
                it.isVisible
            }

            if (currentFragment is NetworkStatusListener) {
                currentFragment.onNetworkChanged(isConnected)
            }

            if (!isConnected && noInternetDialog == null) {
                noInternetDialog = NoInternetBottomSheet()
                noInternetDialog?.show(supportFragmentManager, "NoInternet")
            } else if (isConnected) {
                noInternetDialog?.dismiss()
                noInternetDialog = null
            }
        }
    }

    private fun readIntentExtras() {
        equipmentIdUnique = intent.getStringExtra("EQUIPMENT_ID")
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
