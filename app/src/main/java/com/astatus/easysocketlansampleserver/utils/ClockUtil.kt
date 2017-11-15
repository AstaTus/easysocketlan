package com.huizetime.basketball.time.utils

import android.os.Handler
import android.os.Message
import java.util.*

/**
 * Created by Administrator on 2017/11/3.
 */
class ClockUtil {

    private var isSec = false
    private var onTimeChangeListener: OnTimeChangeListener? = null
    private var going: Boolean = false
    private var mCurrentStartTime: Long = 0
    private var mTotalTime: Long = 0
    private var mCurrentRestTime: Long = 0


    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val now = Date()
            val current_time = now.time
            mCurrentRestTime = mTotalTime - (current_time - mCurrentStartTime)

            if (mCurrentRestTime <= 60000) {
                isSec = false
                if (mCurrentRestTime < 0) {
                    mCurrentRestTime = 0
                }
            }

            if (onTimeChangeListener != null)
                onTimeChangeListener!!.onTimeChange(mCurrentRestTime, isSec)

            if (mCurrentRestTime <= 0) {
                return
            }

            //            myApp.sendBroadcast(intent);
            startRun()
        }
    }

    fun setTimeMill(mill: Long) {
        mTotalTime = mill
        mCurrentRestTime = mTotalTime
        if (mTotalTime <= 60000)
            isSec = false
        else
            isSec = true

        resetCurrentTime()

        if (onTimeChangeListener != null) {
            onTimeChangeListener!!.onSetTime(mTotalTime, isSec)
        }
    }


    fun start() {
        handler.removeMessages(0)
        startRun()
        resetCurrentTime()
        going = true
    }

    fun pause() {
        handler.removeMessages(0)
        val now = Date()
        mTotalTime = mTotalTime - (now.time - mCurrentStartTime)
        mCurrentRestTime = mTotalTime
        going = false

        if (mCurrentRestTime <= 60000)
            isSec = false

        if (onTimeChangeListener != null) {
            onTimeChangeListener!!.onSetTime(mTotalTime, isSec)
        }
    }

    private fun resetCurrentTime() {
        val now = Date()
        mCurrentStartTime = now.time
    }

    fun resume() {
        handler.removeMessages(0)
        startRun()
        resetCurrentTime()
        going = true
    }

    private fun startRun() {
        if (isSec) {
            handler.sendEmptyMessageDelayed(0, 1000)
        } else {
            handler.sendEmptyMessageDelayed(0, 125)
        }
    }

    fun getTime(): Long {
        return mCurrentRestTime
    }

    fun setOnTimeChangeListener(onTimeChangeListener: OnTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener
    }

    fun isGoing(): Boolean {
        return going
    }

    interface OnTimeChangeListener {
        fun onTimeChange(time: Long, isSec: Boolean)

        fun onSetTime(time: Long, isSec: Boolean)
    }
}