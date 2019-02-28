package ga.lupuss.anotherbikeapp.base

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import kotlinx.android.synthetic.main.fragment_labeled.*

abstract class LabeledFragment : BaseFragment(), LabeledView {


    override var isRefreshButtonVisible: Boolean = true
        set(value) {

            refreshButton?.isVisible = value
            field = value
        }

    override var isRefreshProgressBarVisible: Boolean = false
        set(value) {

            refreshProgressBar?.isVisible = value
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_labeled, container, false)
    }

    override fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedPostVerification(view, savedInstanceState)

        refreshButton.setOnClickListener {
            onClickRefreshButton()
        }
    }

    open fun onClickRefreshButton() {}

    fun setLabel(id: Int) {

        label.text = getString(id)
    }

    fun attachChildLayoutToParent(child: View, parent: ConstraintLayout) {

        parent.addView(child)
        val set = ConstraintSet()
        set.clone(parent)
        set.connect(child.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(child.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(child.id, ConstraintSet.TOP, R.id.label, ConstraintSet.BOTTOM)
        set.applyTo(parent)
    }
}
