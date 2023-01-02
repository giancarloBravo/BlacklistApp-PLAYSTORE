package pe.com.gianbravo.blockedcontacts.presentation.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment


/**
 * @author Giancarlo Bravo Anlas
 *
 */
abstract class BaseDialogFragment : DialogFragment() {
    protected var CONTEXT: Context? = null
    lateinit var customView: View

    //private Tracker mTracker;
    override fun onAttach(context: Context) {
        CONTEXT = context
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setupReceiver()
        val builder =
            AlertDialog.Builder(CONTEXT!!)
        val customView =
            requireActivity().layoutInflater.inflate(dialogView, null)
        //     ButterKnife.bind(this, view)
        setupView()
        builder.setView(customView)
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(dialogView, null, false)
        //bind = ButterKnife.bind(this, view)
        //initViewPager()
        return view
    }

    protected abstract val dialogView: Int

    protected abstract fun setupReceiver()
    protected abstract fun setupView()
    fun startLoading() {
        //   (CONTEXT as BaseActivity?).startLoading()
    }

    fun stopLoading() {
        //   (CONTEXT as BaseActivity?).startLoading()
    }

    fun onCloseSession() {
        (CONTEXT as AppCompatActivity?)!!.finish()
    }
}
