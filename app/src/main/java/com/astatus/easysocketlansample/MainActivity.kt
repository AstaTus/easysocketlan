package com.astatus.easysocketlansample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.astatus.easysocketlan.LanServer
import com.astatus.easysocketlan.listener.ILanServerListener

class MainActivity : AppCompatActivity() {

    companion object {
        private val LAN_SERVER_SEARCH_PORT: Int = 9000
        private val LAN_SERVER_SOCKET_PORT: Int = 10001
    }

    private var lanServer: LanServer =
            LanServer(LAN_SERVER_SEARCH_PORT, LAN_SERVER_SOCKET_PORT, object :ILanServerListener{
                override fun onReadError(p0: String?, p1: String?, p2: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onConnect(p0: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onSearchStart() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onWriteError(p0: String?, p1: String?, p2: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onVerification(p0: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onConnectStart() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onSearching() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onWriteStart(p0: String?, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onRead(p0: String?, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onSearchEnd() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onSearchError(p0: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onReadStart(p0: String?, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onConnectError(p0: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onWrite(p0: String?, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lanServer.start()
    }
}
