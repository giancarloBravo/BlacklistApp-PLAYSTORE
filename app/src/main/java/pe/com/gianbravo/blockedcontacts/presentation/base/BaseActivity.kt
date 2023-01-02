package pe.com.gianbravo.blockedcontacts.presentation.base

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import pe.com.gianbravo.blockedcontacts.R
import pe.com.gianbravo.blockedcontacts.domain.User
import pe.com.gianbravo.blockedcontacts.data.source.UserRepository


abstract class BaseActivity : AppCompatActivity() {
    private val userRepository: UserRepository by inject()
    private var user: User? = userRepository.userSession
    private var loader: Dialog? = null

    fun showFullScreenLoader(showTitle: Boolean = true) {
        if (loader == null) {
            loader = Dialog(this)
        }
        if (loader?.isShowing == false) {
            loader?.setCancelable(false)
            loader?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loader?.setContentView(R.layout.dialog_full_screen_loading_indicator)
            loader?.show()
        }
    }

    fun dismissFullScreenLoader() {
        if (loader?.isShowing == true) {
            loader?.dismiss()
        }
    }
}