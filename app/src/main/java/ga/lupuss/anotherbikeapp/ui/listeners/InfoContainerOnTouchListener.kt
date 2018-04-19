package ga.lupuss.anotherbikeapp.ui.listeners

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.gms.maps.GoogleMap
import ga.lupuss.anotherbikeapp.R

/**
 * Handle infoContainer show/hide animations.
 * Animates google map padding changes, infoContainer moves, button's icon rotation and shortStatsContainer appearing.
 */
class InfoContainerOnTouchListener(context: Context,
                                   private val infoContainer: FrameLayout,
                                   private val infoContainerExpandButton: FrameLayout,
                                   private val shortStatsContainer: LinearLayout,
                                   private val map: GoogleMap,
                                   isExpand: Boolean,
                                   private var onExpandStateChanged: ((Boolean) -> Unit)? = null
) : View.OnTouchListener {

    private val isPortrait =
            context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    private val icon: ImageView =
            infoContainerExpandButton.findViewById(R.id.statsContainerExpandButtonIcon)

    private val maxTranslation = if (isPortrait) infoContainer.height.toFloat() else infoContainer.width.toFloat()
    private val minTranslation = 0F
    private var currentTranslation = 0F

    private val maxIconRotation = 180F
    private val minIconRotation = 0F

    private val maxShortStatsAlpha = 1F
    private val minShortStatsAlpha = 0F


    private val maxMapPadding = if (isPortrait) infoContainer.height else infoContainer.width
    private val minMapPadding = 0
    private var currentPadding: Int = if (isExpand) {

        if (isPortrait) infoContainer.height else infoContainer.height

    } else {
        0
    }

    companion object {
        private const val SWIPE_VELOCITY_THRESHOLD = 100
        private const val TOP = 1
        private const val BOTTOM = 2
        private const val LEFT = 3
        private const val RIGHT = 4
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            val swipeDirection: Int

            @Suppress("LiftReturnOrAssignment")
            if (Math.abs(velocityX) < SWIPE_VELOCITY_THRESHOLD
                    && Math.abs(velocityY) < SWIPE_VELOCITY_THRESHOLD) {

                swipeDirection = 0

            } else if (Math.abs(velocityX) > Math.abs(velocityY)) {

                swipeDirection = if (velocityX >= 0) RIGHT else LEFT

            } else {

                swipeDirection = if (velocityY >= 0) BOTTOM else TOP
            }


            val show: Boolean

            show = if (swipeDirection == 0 || (isPortrait && swipeDirection > 2)) {

                currentTranslation < maxTranslation - currentTranslation

            } else {

                (swipeDirection == TOP || swipeDirection == LEFT)
            }

            animation(show)

            return true
        }

    })

    override fun onTouch(p0: View?, motionEvent: MotionEvent): Boolean {

        val result = gestureDetector.onTouchEvent(motionEvent)

        if (!result) {

            if (motionEvent.action == MotionEvent.ACTION_UP) {

                animation(currentTranslation < maxTranslation - currentTranslation)

            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                var finalTranslation = if (isPortrait) {

                    motionEvent.rawY - (infoContainerExpandButton.top + infoContainerExpandButton.height * 1.2F)
                } else {
                    motionEvent.rawX - (infoContainerExpandButton.left + infoContainerExpandButton.width * 1.2F)
                }

                if (finalTranslation > maxTranslation) {

                    finalTranslation = maxTranslation

                } else if (finalTranslation < minTranslation) {

                    finalTranslation = minTranslation
                }

                currentPadding =
                        Math.ceil(
                                maxMapPadding * ((maxTranslation - finalTranslation) / maxTranslation).toDouble()
                        ).toInt()


                if (isPortrait) {

                    map.setPadding(0, 0, 0, currentPadding)
                    infoContainerExpandButton.translationY = finalTranslation
                    infoContainer.translationY = finalTranslation
                } else {

                    map.setPadding(0, 0, currentPadding, 0)
                    infoContainerExpandButton.translationX = finalTranslation
                    infoContainer.translationX = finalTranslation
                }

                val factor = (finalTranslation / maxTranslation)

                icon.rotation = factor * maxIconRotation
                shortStatsContainer.alpha = factor * maxShortStatsAlpha
                currentTranslation =
                        if (isPortrait) infoContainer.translationY else infoContainer.translationX
            }

            onExpandStateChanged?.invoke(
                    (isPortrait && infoContainerExpandButton.translationY == 0F)
                            || (!isPortrait && infoContainerExpandButton.translationX == 0F)
            )
        }

        return true
    }

    private fun animation(show: Boolean) {

        val property = if (isPortrait) "translationY" else "translationX"

        val frameLayoutValueAnimator = ObjectAnimator.ofFloat(
                infoContainer,
                property,
                currentTranslation,
                if (show) minTranslation else maxTranslation
        )
        val imageButtonValueAnimator = ObjectAnimator.ofFloat(
                infoContainerExpandButton,
                property, currentTranslation,
                if (show) minTranslation else maxTranslation
        )
        val iconAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
                icon,
                "rotation",
                icon.rotation,
                if (show) minIconRotation else maxIconRotation
        )
        val shortStatsAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
                shortStatsContainer,
                "alpha",
                shortStatsContainer.alpha,
                if (show) minShortStatsAlpha else maxShortStatsAlpha
        )

        val mapPaddingAnimator: ValueAnimator = ValueAnimator.ofInt(
                currentPadding, if (show) maxMapPadding else minMapPadding
        )
        mapPaddingAnimator.addUpdateListener {

            currentPadding = if (isPortrait) {

                map.setPadding(0, 0, 0, it.animatedValue as Int)
                it.animatedValue as Int

            } else {

                map.setPadding(0, 0, it.animatedValue as Int, 0)
                it.animatedValue as Int
            }
        }

        val animator = AnimatorSet()

        animator.duration = countDuration(show)
        animator.playTogether(
                frameLayoutValueAnimator,
                imageButtonValueAnimator,
                iconAnimator,
                mapPaddingAnimator,
                shortStatsAnimator
        )
        onExpandStateChanged?.invoke(show)
        animator.start()
    }

    private fun countDuration(show: Boolean): Long {
        val maxDuration = 300L
        val minDuration = 100L
        var duration = if (show) {

            Math.ceil(
                    maxDuration * (currentTranslation / maxTranslation).toDouble()
            ).toLong()

        } else {

            Math.ceil(
                    maxDuration * ((maxTranslation - currentTranslation) / maxTranslation).toDouble()
            ).toLong()
        }


        if (duration < minDuration) {

            duration = minDuration
        }

        return duration
    }

}