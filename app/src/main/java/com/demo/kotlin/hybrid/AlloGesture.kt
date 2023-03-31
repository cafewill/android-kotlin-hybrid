package com.demo.java.hybrid

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.demo.kotlin.hybrid.Allo
import java.util.*

class AlloGesture (context: Context?) : OnTouchListener {
    private val listener: Listener?
    private val detector: GestureDetector

    interface Listener {
        fun onGesture (type: Int)
    }

    init {
        listener = context as Listener?
        detector = GestureDetector (context, SimpleListener ())
    }

    override fun onTouch (view: View, event: MotionEvent): Boolean {
        Allo.i ("onTouch $javaClass")
        
        val status = detector.onTouchEvent (event)
        try {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onGestureStarted ()
                }
                MotionEvent.ACTION_UP -> {
                    onGestureFinished ()
                }
            }
        } catch (e: Exception) { e.printStackTrace () }

        return status
    }

    fun onGestureStarted () {
        Allo.i ("onGestureStarted $javaClass")

        try {
            listener?.onGesture (GESTURE_STARTED)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onGestureFinished () {
        Allo.i ("onGestureFinished $javaClass")

        try {
            listener?.onGesture (GESTURE_FINISHED)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSwipeLeft () {
        Allo.i ("onSwipeLeft $javaClass")

        try {
            onSingleSwipeLeft ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSingleSwipeLeft () {
        Allo.i ("onSingleSwipeLeft $javaClass")

        try {
            listener?.onGesture (SINGLE_SWIPE_LEFT)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onDoubleSwipeLeft () {
        Allo.i ("onDoubleSwipeLeft $javaClass")

        try {
            listener?.onGesture (DOUBLE_SWIPE_LEFT)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSwipeRight () {
        Allo.i ("onSwipeRight $javaClass")

        try {
            onSingleSwipeRight ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSingleSwipeRight () {
        Allo.i ("onSingleSwipeRight $javaClass")

        try {
            listener?.onGesture (SINGLE_SWIPE_RIGHT)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onDoubleSwipeRight () {
        Allo.i ("onDoubleSwipeRight $javaClass")

        try {
            listener?.onGesture (DOUBLE_SWIPE_RIGHT)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSwipeTop () {
        Allo.i ("onSwipeTop $javaClass")

        try {
            onSingleSwipeTop ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSingleSwipeTop () {
        Allo.i ("onSingleSwipeTop $javaClass")

        try {
            listener?.onGesture (SINGLE_SWIPE_TOP)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onDoubleSwipeTop () {
        Allo.i ("onDoubleSwipeTop $javaClass")

        try {
            listener?.onGesture (DOUBLE_SWIPE_TOP)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSwipeBottom () {
        Allo.i ("onSwipeBottom $javaClass")

        try {
            onSingleSwipeBottom ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onSingleSwipeBottom () {
        Allo.i ("onSingleSwipeBottom $javaClass")

        try {
            listener?.onGesture (SINGLE_SWIPE_BOTTOM)
        } catch (e: Exception) { e.printStackTrace () }
    }

    fun onDoubleSwipeBottom () {
        Allo.i ("onDoubleSwipeBottom $javaClass")

        try {
            listener?.onGesture (DOUBLE_SWIPE_BOTTOM)
        } catch (e: Exception) { e.printStackTrace () }
    }

    private inner class SimpleListener : SimpleOnGestureListener () {

        private var prevTopDate = Date ()
        private var doneTopDate = Date ()
        private var prevBottomDate = Date ()
        private var doneBottomDate = Date ()
        private var prevLeftDate = Date ()
        private var doneLeftDate = Date ()
        private var prevRightDate = Date ()
        private var doneRightDate = Date ()

        override fun onFling (e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Allo.i ("onFling $javaClass")

            val status = false

            try {
                val diffX = e1.x - e2.x
                val diffY = e1.y - e2.y
                if (Math.abs (diffX) < Math.abs (diffY)) {
                    if (SWIPE_THRESHOLD < Math.abs (diffY) && SWIPE_VELOCITY_THRESHOLD < Math.abs (velocityY)) {
                        if (0 < diffY) {
                            doneTopDate = Date ()
                            val interval = Math.abs (doneTopDate.time - prevTopDate.time)
                            if (DOUBLE_THRESHOLD < interval) {
                                onSingleSwipeTop ()
                            } else {
                                onDoubleSwipeTop ()
                            }
                            prevTopDate = doneTopDate
                        }
                        if (0 > diffY) {
                            doneBottomDate = Date ()
                            val interval = Math.abs (doneBottomDate.time - prevBottomDate.time)
                            if (DOUBLE_THRESHOLD < interval) {
                                onSingleSwipeBottom ()
                            } else {
                                onDoubleSwipeBottom ()
                            }
                            prevBottomDate = doneBottomDate
                        }
                    }
                }
                if (Math.abs (diffX) > Math.abs (diffY)) {
                    if (SWIPE_THRESHOLD < Math.abs (diffX) && SWIPE_VELOCITY_THRESHOLD < Math.abs (velocityX)) {
                        if (0 < diffX) {
                            doneLeftDate = Date ()
                            val interval = Math.abs (doneLeftDate.time - prevLeftDate.time)
                            if (DOUBLE_THRESHOLD < interval) {
                                onSingleSwipeLeft ()
                            } else {
                                onDoubleSwipeLeft ()
                            }
                            prevLeftDate = doneLeftDate
                        }
                        if (0 > diffX) {
                            doneRightDate = Date ()
                            val interval = Math.abs (doneRightDate.time - prevRightDate.time)
                            if (DOUBLE_THRESHOLD < interval) {
                                onSingleSwipeRight ()
                            } else {
                                onDoubleSwipeRight ()
                            }
                            prevRightDate = doneRightDate
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace () }

            return status
        }
    }

    companion object {

        const val SWIPE_THRESHOLD = 100
        const val DOUBLE_THRESHOLD = 500
        const val SWIPE_VELOCITY_THRESHOLD = 100
        const val GESTURE_STARTED = 101
        const val GESTURE_FINISHED = 102
        const val SINGLE_SWIPE_TOP = 111
        const val SINGLE_SWIPE_BOTTOM = 112
        const val SINGLE_SWIPE_LEFT = 113
        const val SINGLE_SWIPE_RIGHT = 114
        const val DOUBLE_SWIPE_TOP = 121
        const val DOUBLE_SWIPE_BOTTOM = 122
        const val DOUBLE_SWIPE_LEFT = 123
        const val DOUBLE_SWIPE_RIGHT = 124
    }
}