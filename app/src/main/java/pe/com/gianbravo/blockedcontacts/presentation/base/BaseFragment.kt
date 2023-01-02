package pe.com.gianbravo.blockedcontacts.presentation.base

import androidx.fragment.app.Fragment
import pe.com.gianbravo.blockedcontacts.presentation.base.BaseActivity

/**
 * @author Giancarlo Bravo Anlas
 *
 */

abstract class BaseFragment: Fragment() {

    fun showFullScreenLoader(showTitle: Boolean = true) {
        val activity = requireActivity()
        if (isAdded && activity is BaseActivity) {
            activity.showFullScreenLoader(showTitle)
        }
    }

    fun dismissFullScreenLoader() {
        val activity = requireActivity()
        if (activity is BaseActivity) {
            activity.dismissFullScreenLoader()
        }
    }

    fun isExists():Boolean = (isAdded && activity!=null)
}