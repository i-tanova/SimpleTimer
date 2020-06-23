package com.ivanatanova.simpletimer

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface Timer {
    var addOnTickListener: ((Long) -> Unit)?
    fun onStart(durationMs: Long)
    fun onPause()
    fun onStop()
    fun onRestart()
    fun getState(): TimerState
    fun getTimeMs() : Long
}

enum class TimerState {
    PAUSED,
    RESTARTED,
    START,
    NONE,
    STOPPED
}

class TimerImpl : Timer {

    private val INTERVAL_MS = 1000L
    private var rxTimer = Observable.interval(INTERVAL_MS, INTERVAL_MS, TimeUnit.MILLISECONDS)
    private var subscription: Disposable? = null

    private var timerState = TimerState.NONE
    private var durationMs = 0L
    private var timePaused = 0L
    private var time = 0L
    override var addOnTickListener: ((Long) -> Unit)? = null

    override fun onStart(durationMs: Long) {
        timerState = TimerState.START
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
        if (timerState == TimerState.RESTARTED) {
            time = timePaused + timeTick
        } else {
            time = timeTick
        }

        println("Rx time: $time")
        addOnTickListener?.invoke(time)
    }

    override fun onPause() {
        timerState = TimerState.PAUSED
        timePaused = time
        stopRxTimer()
    }

    override fun onStop() {
        timerState = TimerState.STOPPED
        timePaused = 0
        subscription?.dispose()
    }

    override fun onRestart() {
        timerState = TimerState.RESTARTED
        startRxTimer()
    }

    override fun getState(): TimerState {
        return timerState
    }

    override fun getTimeMs(): Long {
        return time
    }
}