package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import kotlinx.android.synthetic.main.fragment_route_photos.*
import java.io.File
import java.lang.IllegalStateException
import javax.inject.Inject

class RoutePhotosFragment : BaseFragment(), View.OnClickListener, RoutePhotosView {

    interface Listener {

        var photosAdapter: RecyclerView.Adapter<*>

        fun onNewPhotoTaken(file: File)
    }

    var isTakingNewPhotoEnabled = true
        set(value) {
            field = value
            takePhotoButton?.isGone = !value
        }

    var photosListener: Listener? = null

    @Inject
    lateinit var presenter: RoutePhotosPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is Listener && isTakingNewPhotoEnabled) {

           photosListener = context

        } else {

            throw IllegalStateException("Activity should implement RoutePhotosPresenter.Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_route_photos, container, false)
    }

    override fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {

        takePhotoButton.setOnClickListener(this)
        photosRecyclerView.adapter = photosListener!!.photosAdapter
    }

    override fun onDetach() {
        super.onDetach()
        photosListener = null
    }

    override fun onClick(v: View?) {

        when (view?.id) {
            R.id.takePhotoButton -> {

                presenter.onClickTakePhotoButton()

            }
        }
    }

    override fun notifyPhotoTaken(file: File) {

        photosListener?.onNewPhotoTaken(file)
    }
}
