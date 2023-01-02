package pe.com.gianbravo.blockedcontacts.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import pe.com.gianbravo.blockedcontacts.R

/**
 * @author Giancarlo Bravo Anlas
 *
 */

class DialogUtil {
    interface OnEventDialog {
        fun onClickSend()
        fun onCancel()
    }

    companion object {

        fun setupDialog(ctx: Context?, res: Int): Dialog {
            val dialog = Dialog(ctx!!)
            dialog.setCancelable(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(res)
            return dialog
        }


        fun showDialogListener(
            context: Context?,
            message: String?,
            isCloseView: Boolean,
            closeEnabled: Boolean,
            sendEnabled: Boolean,
            onEventDialog: OnEventDialog?
        ): Dialog? {
            val dialog: Dialog =
                setupDialog(context, R.layout.dialog_listener)
            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val mTxtMessage = dialog.findViewById<View>(R.id.message_dialog) as TextView

            mTxtMessage.text = message
            val mBtnClose = dialog.findViewById<View>(R.id.close_dialog_liq) as Button
            mBtnClose.isEnabled = closeEnabled
            val mBtnSend = dialog.findViewById<View>(R.id.send_dialog) as Button
            mBtnSend.isEnabled = sendEnabled
            if (isCloseView) {
                context?.let {
                    mBtnClose.text = it.getString(R.string.text_no)
                    mBtnSend.text = it.getString(R.string.text_yes)

                }
            }
            mBtnClose.setOnClickListener { view: View? ->
                if (onEventDialog != null) onEventDialog.onCancel()
                dialog.dismiss()
            }
            mBtnSend.setOnClickListener { view: View? ->
                if (onEventDialog != null) onEventDialog.onClickSend()
                dialog.dismiss()
            }
            if (!(context as Activity).isFinishing) dialog.show()
            return dialog
        }

    }
}
