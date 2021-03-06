package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.GlideApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.ui.adapters.RoutePhotosRecyclerViewAdapter
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
    }

    var isTakingNewPhotoEnabled = true
        set(value) {
            field = value
            takePhotoButton?.isGone = !value
        }

    private var photosListener: Listener? = null

    private var isScrollingEnabled = true

    @Inject
    lateinit var presenter: RoutePhotosPresenter

    val adapter: RecyclerView.Adapter<*>
        get() = photosRecyclerView.adapter!!

    override var isNoPhotosTextViewGone = true
        set(value) {

            noPhotosTextView?.isGone = value
            field = value
        }
        get() {

            return noPhotosTextView?.isGone ?: field
        }

    private lateinit var listener: ((Int, View) -> Unit)

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

            throw IllegalStateException("Activity should implement RoutePhotosFragment.Listener." +
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
        isNoPhotosTextViewGone = adapter.itemCount != 0

    }

    override fun onStartPostVerification() {
        super.onStartPostVerification()

        listener = { _, _ -> isNoPhotosTextViewGone = adapter.itemCount != 0 }

        (adapter as? RoutePhotosRecyclerViewAdapter)?.addOnClickDeletePhotoListener(listener)
    }

    override fun onStopPostVerification() {
        super.onStopPostVerification()
        (adapter as? RoutePhotosRecyclerViewAdapter)?.removeOnClickDeletePhotoListener(listener)
    }

    override fun onDestroyViewPostVerification() {

        super.onDestroyViewPostVerification()
        presenter.notifyOnDestroy(true)
    }

    override fun onClick(v: View?) {

        presenter.onClickTakePhotoButton()
    }

    @SuppressLint("InflateParams")
    override fun displayNewPhotoDialog(photoPath: File, onYesAction: (String) -> Unit, onNoAction: () -> Unit) {

        val view = layoutInflater.inflate(R.layout.new_photo_dialog, null, false)

        GlideApp.with(this.requireContext())
                .load(photoPath)
                .fitCenter()
                .centerInside()
                .into(view.findViewById(R.id.newPhotoView))


        AlertDialog.Builder(this.requireContext())
                .setView(view)
                .setPositiveButton(R.string.save) { _, _->

                    onYesAction(view.findViewById<EditText>(R.id.photoNameEditText).text.toString())
                }
                .setNegativeButton(R.string.cancel) { _, _ -> onNoAction() }
                .setOnCancelListener { onNoAction() }
                .show()
    }

    override fun notifyPhotoTaken(photo: RoutePhoto) {

        isNoPhotosTextViewGone = true
        photosListener?.onNewPhotoTaken(photo)
    }
}
