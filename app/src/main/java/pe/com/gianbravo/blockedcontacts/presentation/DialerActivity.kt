package pe.com.gianbravo.blockedcontacts.presentation

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
import android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject
import pe.com.gianbravo.blockedcontacts.R
import pe.com.gianbravo.blockedcontacts.data.source.UserRepository
import pe.com.gianbravo.blockedcontacts.presentation.base.BaseActivity
import pe.com.gianbravo.blockedcontacts.presentation.dialog.HowToUseDialogFragment
import pe.com.gianbravo.blockedcontacts.utils.DialogUtil
import pe.com.gianbravo.blockedcontacts.utils.Utils


class DialerActivity : BaseActivity() {
    private val userRepository: UserRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (userRepository.isFirstTime == true || userRepository.isFirstTime == null) {
            showHowToDialog()
            userRepository.isFirstTime = false
        } else
            offerReplacingDefaultDialer()
    }

    private val defaultCallerRegisterForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            if (uri != null) {
                // Get the data
                when (uri.resultCode) {
                    RESULT_OK -> setupPermissionsAndSettings()
                    RESULT_CANCELED -> finish()
                    else -> Log.d("tag", "else")
                }
            }
        }

    private val settingsRegisterForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            if (uri != null) {
                // Get the data
                when (uri.resultCode) {
                    RESULT_OK -> {}
                    RESULT_CANCELED -> {}
                    else -> Log.d("tag", "else")
                }
            }
        }

    private fun offerReplacingDefaultDialer() {
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
            showChangeDefaultDialerSelector(false)
        } else {
            setupPermissionsAndSettings()
        }
    }

    private fun setupPermissionsAndSettings() {
        this.let {
            if (Utils.hasStoragePermission(this)) {
                // Already have permission, do the thing
                initViews()
            } else {
                // Do not have permissions, request them now
                Utils.requestStoragePermission(
                    this,
                    REQUEST_PERMISSION_MEDIA
                )
            }
        }
    }

    private fun initViews() {
        replaceFragment(
            BlockedNumbersFragment(),
            R.id.catalog
        )
    }

    override fun onRequestPermissionsResult(
        code: Int,
        permission: Array<out String>,
        res: IntArray
    ) {
        super.onRequestPermissionsResult(code, permission, res)
        when (code) {
            REQUEST_PERMISSION_MEDIA -> {
                when {
                    res.isEmpty() -> {
                        //Do nothing, app is resuming
                    }
                    res[0] == PackageManager.PERMISSION_GRANTED -> {
                        initViews()
                    }
                    else -> {
                        finish()
                    }
                }
            }
        }
    }

    private fun replaceFragment(
        fragment: Fragment,
        containerViewId: Int
    ) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showHowToDialog() {
        val dialogModifyEntriesFragment =
            HowToUseDialogFragment()
        dialogModifyEntriesFragment.dialogListener(object : HowToUseDialogFragment.DialogListener {
            override fun onDismiss() {
                userRepository.isFirstTime = false
                offerReplacingDefaultDialer()
            }

            override fun onCancel() {
            }
        })
        val bundle = Bundle()
        dialogModifyEntriesFragment.arguments = bundle

        supportFragmentManager.let {
            dialogModifyEntriesFragment.show(it, "aea")
        }
    }

    override fun onBackPressed() {
        DialogUtil.showDialogListener(this,
            getString(R.string.exit_app),
            true,
            closeEnabled = true,
            sendEnabled = true,
            onEventDialog = object : DialogUtil.OnEventDialog {
                override fun onClickSend() {
                    finish()
                }

                override fun onCancel() {
                }

            })
    }

    fun showChangeDefaultDialerSelector(toSettings: Boolean) {
        if (!toSettings)
            if (SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager =
                    getSystemService(Context.ROLE_SERVICE) as RoleManager
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                defaultCallerRegisterForResult.launch(intent)

            } else {
                Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                    .let(::startActivity)
            }
        else {
            val intent = if (SDK_INT >= Build.VERSION_CODES.N) {
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            } else {
                Intent(Settings.ACTION_SETTINGS)
            }
            settingsRegisterForResult.launch(intent)
        }
    }

    companion object {
        const val REQUEST_PERMISSION_MEDIA = 101
    }
}
