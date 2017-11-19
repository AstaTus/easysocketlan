package com.astatus.easysocketlansamplerclient

import android.app.Application
import android.support.multidex.MultiDexApplication

/**
 * Created by Administrator on 2017/11/16.
 */
class ClientApp: MultiDexApplication() {

    companion object {
        private var sSingleton: ClientApp? = null

        public fun getSingleton() : ClientApp
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