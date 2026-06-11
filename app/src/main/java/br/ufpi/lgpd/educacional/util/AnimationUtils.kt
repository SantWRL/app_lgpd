package br.ufpi.lgpd.educacional.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView

/**
 * AnimationUtils – Provides reusable animations for UI elements.
 */
object AnimationUtils {

    private const val DEFAULT_DURATION = 350L

    /** Slide-up + fade-in for a single view */
    fun slideUpFadeIn(view: View, delay: Long = 0, duration: Long = DEFAULT_DURATION) {
        view.alpha = 0f
        view.translationY = 60f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(duration)
            .setStartDelay(delay)
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()
    }

    /** Gentle scale-pop for interactive elements */
    fun scalePop(view: View, delay: Long = 0) {
        view.alpha = 0f
        view.scaleX = 0.85f
        view.scaleY = 0.85f
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400L)
            .setStartDelay(delay)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }

    /** Fade-in only */
    fun fadeIn(view: View, delay: Long = 0, duration: Long = 300L) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setStartDelay(delay)
            .start()
    }

    /** Attach a staggered animation to a RecyclerView's children when they appear */
    fun attachStaggerAnimation(recyclerView: RecyclerView, maxItems: Int = 8) {
        recyclerView.itemAnimator?.apply {
            addDuration = 300L
            removeDuration = 200L
            moveDuration = 250L
            changeDuration = 200L
        }

        recyclerView.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val pos = recyclerView.getChildAdapterPosition(view)
                if (pos in 0 until maxItems) {
                    slideUpFadeIn(view, delay = pos * 60L, duration = 350L)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {}
        })
    }
}
