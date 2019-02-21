package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
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
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.ui.decorations.BottomSpaceItemDecoration
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import kotlinx.android.synthetic.main.fragment_route_photos.*
import java.io.File
import java.lang.IllegalStateException
import javax.inject.Inject

class RoutePhotosFragment : BaseFragment(), View.OnClickListener, RoutePhotosView {

    interface Listener {

        var photosAdapter: RecyclerView.Adapter<*>

        fun onNewPhotoTaken(photo: RoutePhoto)

        fun onClickDeletePhoto(position: Int)
    }

    var isTakingNewPhotoEnabled = true
        set(value) {
            field = value
            takePhotoButton?.isGone = !value
        }

    var photosListener: Listener? = null

    var isScrollingEnabled = true

    @Inject
    lateinit var presenter: RoutePhotosPresenter

    @Inject
    lateinit var picasso: Picasso

    val adapter: RecyclerView.Adapter<*>
        get() = photosRecyclerView.adapter

    override fun onAttach(context: Context?) {
        requiresVerification()
        super.onAttach(context)
    }

    override fun onAttachPostVerification(context: Context?) {

        AnotherBikeApp.get(this.requireActivity().application)
                .routePhotosComponent(this)
                .inject(this)

        super.onAttachPostVerification(context)

        if (context is Listener) {

            photosListener = context

        } else {

            throw IllegalStateException("Activity should implement RoutePhotosPresenter.Listener." +
                    " It's ${context!!.javaClass.canonicalName}")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_route_photos, container, false)
        (view as NestedScrollView).setOnTouchListener { _, _ -> !isScrollingEnabled }
        return view
    }

    override fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {

        super.onViewCreatedPostVerification(view, savedInstanceState)
        takePhotoButton.setOnClickListener(this)
        takePhotoButton.isGone = !isTakingNewPhotoEnabled
        photosRecyclerView.apply {
            setItemViewCacheSize(10)
            isNestedScrollingEnabled = false
            this.adapter = this@RoutePhotosFragment.photosListener!!.photosAdapter
            layoutManager = LinearLayoutManager(this@RoutePhotosFragment.requireContext())
            addItemDecoration(
                    BottomSpaceItemDecoration(
                            dpToPixels(this@RoutePhotosFragment.requireContext(), 5F)
                    )
            )
        }
    }

    override fun onDestroyViewPostVerification() {
        super.onDestroyViewPostVerification()
        presenter.notifyOnDestroy(true)
    }

    override fun onClick(v: View?) {

        presenter.onClickTakePhotoButton()
    }

    @SuppressLint("InflateParams")
    override fun displayNewPhotoDialog(photoPath: File, onYesAction: (String) -> Unit) {

        val view = layoutInflater.inflate(R.layout.new_photo_dialog, null, false)

        picasso.load(photoPath)
                .fit()
                .centerInside()
                .into(view.findViewById<ImageView>(R.id.newPhotoView))

        AlertDialog.Builder(this.requireContext())
                .setView(view)
                .setPositiveButton(R.string.save) { _, _->

                    onYesAction.invoke(
                            view.findViewById<EditText>(R.id.photoNameEditText).text.toString()
                    )
                }
                .setNegativeButton(R.string.cancel) { _, _ ->}
                .show()
    }

    override fun notifyPhotoTaken(photo: RoutePhoto) {

        photosListener?.onNewPhotoTaken(photo)
    }
}
