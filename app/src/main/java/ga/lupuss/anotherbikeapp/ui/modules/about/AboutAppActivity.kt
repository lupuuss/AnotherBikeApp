package ga.lupuss.anotherbikeapp.ui.modules.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.BuildConfig
import ga.lupuss.anotherbikeapp.FLAT_ICON_AUTHORS_URL
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import kotlinx.android.synthetic.main.activity_about_app.*
import java.util.regex.Pattern
import javax.inject.Inject
import android.text.style.URLSpan
import android.text.SpannableString
import android.widget.TextView
import android.text.TextPaint





class AboutAppActivity : ThemedActivity(), AboutAppView {

    @Inject
    lateinit var presenter: AboutAppPresenter


    private class URLSpanNoUnderline(url: String) : URLSpan(url) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        requiresVerification()
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePostVerification(savedInstanceState: Bundle?) {

        AnotherBikeApp
                .get(application)
                .aboutAppComponent(this)
                .inject(this)

        super.onCreatePostVerification(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        activateToolbar(aboutAppToolbar)

        val pattern = Pattern.compile("@\\S+")

        Linkify.addLinks(
                weatherIconsCreditsText,
                pattern, FLAT_ICON_AUTHORS_URL,
                null,
                Linkify.TransformFilter { _, url ->

                    url.substring(1)
                }
        )

        stripUnderlines(weatherIconsCreditsText)

        versionText.text = BuildConfig.VERSION_NAME

        presenter.notifyOnViewReady()
    }

    private fun stripUnderlines(textView: TextView) {
        val s = SpannableString(textView.text)
        val spans = s.getSpans(0, s.length, URLSpan::class.java)
        spans.forEach { span ->
            val start = s.getSpanStart(span)
            val end = s.getSpanEnd(span)
            s.removeSpan(span)
            s.setSpan(URLSpanNoUnderline(span.url), start, end, 0)
        }
        textView.text = s
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickFirebase(view: View) {

        presenter.onClickFirebase()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickOpenWeatherMap(view: View) {

        presenter.onClickOpenWeatherMap()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickFlatIcon(view: View) {

        presenter.onClickFlatIcon()
    }

    override fun onDestroyPostVerification() {

        super.onDestroyPostVerification()
        presenter.notifyOnViewReady()
    }

    companion object {

        @JvmStatic
        fun newIntent(context: Context) = Intent(context, AboutAppActivity::class.java)
    }
}
