package com.ivanatanova.simpletimer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.krtkush.lineartimer.LinearTimer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val timerDurationMs = 20000L
    val timer: Timer = TimerImpl()
    var linearTimer: LinearTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timer_background.setOnClickListener {
            when (timer.getState()) {
                TimerState.PAUSED -> {
                    onTimerRestarted()
                }
                TimerState.RESTARTED -> {
                    onTimerPaused()
                }
                TimerState.START -> {
                    onTimerPaused()
                }
                TimerState.NONE -> {
                    onTimerStarted()
                }
                TimerState.STOPPED -> {
                    onTimerStarted()
                }
            }
        }

        timer.addOnTickListener = { onTick ->
            if((onTick * 1000) >= timerDurationMs){
                onTimerFinished()
            }else {
                timer_time_txt.text = "${(timerDurationMs - (onTick * 1000)) / 1000}"
            }
        }
    }

    private fun onTimerRestarted() {
        Log.d("Timer", "On timer restarted")

        timer_press_txt.visibility = View.GONE
        timer.onRestart()
        linearTimer?.resumeTimer()
    }

    private fun onTimerPaused() {
        Log.d("Timer", "On timer paused")
        timer_press_txt.visibility = View.VISIBLE
        timer.onPause()
        linearTimer?.pauseTimer()
    }

    private fun onTimerFinished() {
        Log.d("Timer", "On timer finished")
        timer_press_txt.visibility = View.VISIBLE
        timer.onStop()
    }

    private fun onTimerStarted() {
        linearTimer = LinearTimer.Builder()
            .linearTimerView(timer_progress_bar)
            .duration(timerDurationMs)
            .build()
        linearTimer?.startTimer()

        timer_press_txt.visibility = View.GONE
        timer.onStart(timerDurationMs)
    }
}
