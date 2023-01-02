package pe.com.gianbravo.blockedcontacts.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * @author Giancarlo Bravo Anlas
 *
 */
object Utils {
    private const val TAG = "Utils"

    fun requestStoragePermission(fragment: Activity, requestCode: Int) {
        fragment.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            requestCode
        )
    }

    fun hasStoragePermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}