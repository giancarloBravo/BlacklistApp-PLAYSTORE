package pe.com.gianbravo.blockedcontacts.presentation.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.sonu.libraries.materialstepper.OnLastStepNextListener
import kotlinx.android.synthetic.main.dialog_how_to_use.*
import pe.com.gianbravo.blockedcontacts.R

/**
 * @author Giancarlo Bravo Anlas
 *
 */
class HowToUseDialogFragment : DialogFragment() {
    private var listener: DialogListener? = null

    interface DialogListener {
        fun onDismiss()
        fun onCancel()
    }

    fun dialogListener(listener: DialogListener) {
        this.listener = listener
    }

    override fun dismiss() {
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onCancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_how_to_use, null, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        setStyle(
            STYLE_NO_TITLE,
            R.style.Custom_Dialog
        )
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }


    override fun onStart() {
        super.onStart()
        setup()
    }

    private fun setup() {
        //adding fragment manager for ViewPager Adapter
        materialStepper?.let {
            it.fragmentManager = childFragmentManager
            //adding steps
            it.addStep(InformationFragment0(getString(R.string.title_information_0)))
            it.addStep(InformationFragment1(getString(R.string.title_information_1)))
            it.addStep(InformationFragment2(getString(R.string.title_information_2)))
            it.addStep(InformationFragment3(getString(R.string.title_information_3)))
            it.addStep(InformationFragment4(getString(R.string.title_information_4)))

            //adding functionality when NEXT button is clicked on last step
            it.onLastStepNextListener = OnLastStepNextListener {
                //some action
                dialog?.dismiss()
            }

        }
    }
}