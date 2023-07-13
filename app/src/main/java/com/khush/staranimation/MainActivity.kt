package com.khush.staranimation

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var container: FrameLayout
    private lateinit var job: Job

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.fl)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            container.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        container.setOnClickListener {
            shower()
        }
        container.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (!::job.isInitialized || job.isCancelled) {
                    job = CoroutineScope(Dispatchers.IO).launch {
                        while (isActive) {
                            withContext(Dispatchers.Main) {
                                shower()
                            }
                            delay(100)
                        }
                    }
                }
            } else if (event.action == KeyEvent.ACTION_UP) {
                if (::job.isInitialized && job.isActive) {
                    job.cancel()
                }
            }
            return@OnTouchListener true
        })
    }


    private fun shower() {

        val containerW = container.width
        val containerH = container.height
        val dip = 24f
        val r: Resources = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )
        var starW: Float = px
        var starH: Float = px
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2  //start pos, 0 to half start width
        container.addView(newStar)
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y,
            -starH, containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION,
            (Math.random() * 1080).toFloat())
        rotator.interpolator = LinearInterpolator()
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                container.removeView(newStar)
            }
        })
        set.start()
    }

//    private fun rotater() {
//        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
//        animator.duration = 1000
//        animator.disableViewDuringAnimation(rotateButton)
//        animator.start()
//    }
//
//    private fun translater() {
//        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
//        animator.repeatCount = 3
//        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(translateButton)
//        animator.start()
//    }
//
//    private fun scaler() {
//        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
//        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
//        val animator = ObjectAnimator.ofPropertyValuesHolder(
//            star, scaleX, scaleY)
//        animator.repeatCount = 1
//        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(scaleButton)
//        animator.start()
//    }
//
//    private fun fader() {
//        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
//        animator.repeatCount = 1
//        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(fadeButton)
//        animator.start()
//    }
//
//    private fun colorizer() {
//        var animator = ObjectAnimator.ofArgb(star.parent,
//            "backgroundColor", Color.BLACK, Color.RED)
//        animator.setDuration(500)
//        animator.repeatCount = 1
//        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(colorizeButton)
//        animator.start()
//    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator) {
                view.isEnabled = true
            }
        })
    }

}