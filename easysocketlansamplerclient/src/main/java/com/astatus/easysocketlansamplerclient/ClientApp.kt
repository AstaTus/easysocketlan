package com.astatus.easysocketlansamplerclient

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication

/**
 * Created by Administrator on 2017/11/16.
 */
class ClientApp: Application() {

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

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }


    private fun initCrashHandler(){

        mCrashExceptionHandler = CrashExceptionHandler(
                applicationContext,
                FileUtil.getSDFile("astatus/easysocketlansampleserver/log/crash.txt"),
                MainActivity::class.java)

        Thread.setDefaultUncaughtExceptionHandler(mCrashExceptionHandler)
    }
}