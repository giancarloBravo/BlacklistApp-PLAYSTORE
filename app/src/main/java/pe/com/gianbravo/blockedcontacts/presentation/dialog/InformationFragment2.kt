package pe.com.gianbravo.blockedcontacts.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.sonu.libraries.materialstepper.StepFragment
import pe.com.gianbravo.blockedcontacts.R

/**
 * @author Giancarlo Bravo Anlas
 *
 */

class InformationFragment2(var title:String) : StepFragment() {

    override fun getStepTitle(): String {
        return title
    }

    override fun canGoBack(): Boolean {
        return true
    }

    override fun canSkip(): Boolean {
        return false
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_step_2, container, false)
    }
}