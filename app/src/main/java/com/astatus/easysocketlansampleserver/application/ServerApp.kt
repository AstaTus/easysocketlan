package com.astatus.easysocketlansampleserver.application

import android.app.Application
import android.support.multidex.MultiDexApplication
import com.astatus.easysocketlansampleserver.activity.MainActivity
import com.astatus.easysocketlansampleserver.utils.FileUtil

/**
 * Created by Administrator on 2017/11/15.
 */
class ServerApp: MultiDexApplication() {

    companion object {
        private var sSingleton: ServerApp? = null

        public fun getSingleton() : ServerApp
        {
            return sSingleton!!
        }
    }

    //Other
    private lateinit var mCrashExceptionHandler: CrashExceptionHandler

    override fun onCreate() {
        super.onCreate()

        sSingleton = this

        initCrashHandler()
    }

    private fun initCrashHandler(){

        mCrashExceptionHandler = CrashExceptionHandler(
                applicationContext,
                FileUtil.getSDFile("astatus/easysocketlansampleserver/log/crash.txt"),
                MainActivity::class.java)

        Thread.setDefaultUncaughtExceptionHandler(mCrashExceptionHandler)
    }

}