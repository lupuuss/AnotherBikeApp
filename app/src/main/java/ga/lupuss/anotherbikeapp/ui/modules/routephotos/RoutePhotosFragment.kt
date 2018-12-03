package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.models.dataclass.Photo
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import kotlinx.android.synthetic.main.fragment_route_photos.*
import java.io.File
import java.lang.IllegalStateException
import javax.inject.Inject

class RoutePhotosFragment : BaseFragment(), View.OnClickListener, RoutePhotosView {

    interface Listener {

        var photosAdapter: RecyclerView.Adapter<*>

        fun onNewPhotoTaken(photo: Photo)
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
        requiresVerification()
        super.onAttach(context)
    }

    override fun onAttachPostVerification(context: Context?) {

        AnotherBikeApp.get(this.requireActivity().application)
                .routePhotosComponent(this)
                .inject(this)

        super.onAttachPostVerification(context)

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
        super.onViewCreatedPostVerification(view, savedInstanceState)
        takePhotoButton.setOnClickListener(this)
        photosRecyclerView.adapter = photosListener!!.photosAdapter
    }

    override fun onDetach() {
        super.onDetach()
        photosListener = null
    }

    override fun onClick(v: View?) {

        presenter.onClickTakePhotoButton()
    }

    override fun displayNewPhotoDialog(photoPath: File, onYesAction: (String) -> Unit) {

        val view = layoutInflater.inflate(R.layout.new_photo_dialog, null, false)

        Picasso.get()
                .load(photoPath)
                .into(view.findViewById<ImageView>(R.id.newPhotoView))

        AlertDialog.Builder(this.requireContext())
                .setView(view)
                .setPositiveButton(R.string.yes) { _, _->

                    onYesAction.invoke(
                            view.findViewById<EditText>(R.id.photoNameEditText).text.toString()
                    )
                }
                .setNegativeButton(R.string.cancel) { _, _ ->}
                .show()
    }

    override fun notifyPhotoTaken(photo: Photo) {

        photosListener?.onNewPhotoTaken(photo)
    }
}
