package com.ivanatanova.simpletimer

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface Timer {
    fun onTick(timePassed: Long)
    fun onStart(durationMs: Long)
    fun onPause()
    fun onStop()
    fun onRestart()
}

class TimerImpl : Timer {

    private val INTERVAL_MS = 2000L
    private var rxTimer = Observable.interval(INTERVAL_MS, INTERVAL_MS, TimeUnit.MILLISECONDS)
    var subscription: Disposable? = null

    enum class TimerState {
        PAUSED,
        RESTARTED,
        START,
        NONE,
        STOPED
    }

    var state = TimerState.NONE
    var durationMs = 0L
    var timePaused = 0L
    var time = 0L

    override fun onTick(timePassed: Long) {
        println("Tick: $timePassed")
    }

    override fun onStart(durationMs: Long) {
        state = TimerState.START
        this.durationMs = durationMs
        startRxTimer()
    }

    private fun startRxTimer() {
        subscription?.dispose()
        subscription = rxTimer
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onRxTick(it)
            }
    }

    private fun stopRxTimer() {
        subscription?.dispose()
    }

    private fun onRxTick(it: Long) {
        val timeTick =
            it + 1 // RX interval start from 0, but we've added initial delay so it should start from 1
        if (state == TimerState.RESTARTED) {
            time = timePaused + timeTick
        } else {
            time = timeTick
        }

        println("Rx time: $time")
    }

    override fun onPause() {
        state = TimerState.PAUSED
        timePaused = time
        stopRxTimer()
    }

    override fun onStop() {
        state = TimerState.STOPED
        timePaused = 0
        subscription?.dispose()
    }

    override fun onRestart() {
        state = TimerState.RESTARTED
        startRxTimer()
    }

}