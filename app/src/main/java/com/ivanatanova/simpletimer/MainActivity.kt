package com.ivanatanova.simpletimer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isTimerFinished = true
    private var isTimerPaused = false
    var timePassed = 0f
    val timerDurationMs = 20000L
    val timer: Timer = TimerImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timer_background.setOnClickListener {
            if (isTimerFinished) {
                onTimerStarted()
            } else {
                if (!isTimerPaused) {
                    onTimerPaused()
                } else {
                    onTimerRestarted()
                }
            }
        }
//        timer_progress_bar.onProgressChangeListener = { progress ->
//            if (progress >= 100f) {
//                onTimerFinished()
//            }
//        }
    }

    private fun onTimerRestarted() {
        Log.d("Timer", "On timer restarted")
        val progress = timer_progress_bar.progress
//        timer_progress_bar.startAngle = progress
//        timer_progress_bar.setProgressWithAnimation(timerDurationMs - timePassed, (timerDurationMs - timePassed).toInt()*1000L)
        timer_press_txt.visibility = View.GONE
        isTimerPaused = false
        timer.onRestart()
    }

    private fun onTimerPaused() {
        Log.d("Timer", "On timer paused")
        isTimerPaused = true
//        val progress = timer_progress_bar.progress
//        timer_progress_bar.indeterminateMode = false
        timer_press_txt.visibility = View.VISIBLE

        timer.onPause()
    }

    private fun onTimerFinished() {
        Log.d("Timer", "On timer finished")
//        timer_progress_bar.progress = 0f
//        timer_progress_bar.invalidate()
        timer_press_txt.visibility = View.VISIBLE
        timer.onStop()
        isTimerFinished = true
    }

    private fun onTimerStarted() {
        timePassed = 0f
        isTimerFinished = false

//        timer_progress_bar.progressMax = timerDurationMs
//        timer_progress_bar.setProgressWithAnimation(timerDurationMs, timerDurationMs.toInt()*1000L)
//
        timer_press_txt.visibility = View.GONE

        timer.onStart(timerDurationMs)
    }
}
