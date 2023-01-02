package pe.com.gianbravo.blockedcontacts

import android.content.Context
import android.widget.Toast

/**
 * @author Giancarlo Bravo Anlas
 *
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}