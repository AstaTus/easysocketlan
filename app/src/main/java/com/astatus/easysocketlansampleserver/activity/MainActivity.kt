package com.astatus.easysocketlansampleserver.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast

import com.astatus.easysocketlan.LanServer
import com.astatus.easysocketlan.PacketHandler
import com.astatus.easysocketlan.entity.ClientDeviceEntity
import com.astatus.easysocketlan.listener.ILanServerListener
import com.astatus.easysocketlansampleserver.BR
//import com.astatus.easysocketlansampleserver.BR


import com.astatus.easysocketlansampleserver.R
import com.astatus.easysocketlansampleserver.adapter.GeneralListAdapter
import com.astatus.easysocketlansampleserver.entity.ClientEntity
import com.astatus.easysocketlansampleserver.entity.MessageEntity
import com.astatus.easysocketlansampleserver.fragment.MessageDialogFragment
import com.astatus.easysocketlansampleserver.lan.CmsgOpCode
import com.astatus.easysocketlansampleserver.lan.SmsgOpCode
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val LAN_SERVER_SEARCH_PORT: Int = 9000
        private val LAN_SERVER_SOCKET_PORT: Int = 10001

        private val MESSAGE_SENDER_SELF: Int = 0
        private val MESSAGE_SENDER_OTHER: Int = 1
    }

    private lateinit var clientListAdapter: GeneralListAdapter<ClientEntity>
    private var clients: ArrayList<ClientEntity> = ArrayList<ClientEntity>()

    private var messageFragment: MessageDialogFragment? = null

    private var mGson: Gson = Gson()

    inner class ClientItemHandler{
        public fun onMessageMoreClick(view: View) {

            this@MainActivity.showDialogFragment()

            if (messageFragment != null){
                var entity = view.getTag() as ClientEntity
                messageFragment!!.setClientEntity(entity)
            }
        }
    }

    private var clientItemHandler = ClientItemHandler()

    private var lanServer: LanServer =
            LanServer(LAN_SERVER_SEARCH_PORT, LAN_SERVER_SOCKET_PORT, object :ILanServerListener{


                override fun onSearchStart() {

                    search_state_TV.setText(R.string.search_start)
                    Toast.makeText(this@MainActivity, R.string.search_start, Toast.LENGTH_SHORT).show()
                }

                override fun onSearching() {
                    Toast.makeText(this@MainActivity, R.string.searching_boradcast, Toast.LENGTH_SHORT).show()
                }

                override fun onSearchEnd() {
                    search_state_TV.setText(R.string.activity_main_search_stop)
                    Toast.makeText(this@MainActivity, R.string.search_finish, Toast.LENGTH_SHORT).show()
                }

                override fun onSearchError(p0: String?) {
                    Toast.makeText(this@MainActivity, R.string.search_error, Toast.LENGTH_SHORT).show()
                }

                override fun onConnectStart() {

                    link_state_TV.setText(R.string.activity_main_link_linking)


                    Toast.makeText(this@MainActivity, R.string.connect_start, Toast.LENGTH_SHORT).show()
                }

                override fun onConnectError(p0: String?) {

                    link_state_TV.setText(R.string.activity_main_link_unlink)

                    Toast.makeText(this@MainActivity, R.string.connect_error, Toast.LENGTH_SHORT).show()
                }

                override fun onConnect(p0: String?, p1: Int) {
                    Toast.makeText(this@MainActivity, R.string.connecting_client, Toast.LENGTH_SHORT).show()
                }

                override fun onVerification(device: ClientDeviceEntity?, isSuccess: Boolean) {
                    if (device != null && isSuccess){
                        var c = ClientEntity()

                        c.address = device.ip + '_' + device.port.toString()
                        c.name = device.name

                        addClient(c)

                        last_client_TV.setText(c.address + " connected");

                        Toast.makeText(this@MainActivity, R.string.socket_verification, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onWriteStart(p0: String?) {
                    Toast.makeText(this@MainActivity, R.string.socket_write_start, Toast.LENGTH_SHORT).show()
                }

                override fun onWrite(p0: String?) {
                    Toast.makeText(this@MainActivity, R.string.socket_on_writing, Toast.LENGTH_SHORT).show()
                }

                override fun onReadStart(p0: String?) {
                    Toast.makeText(this@MainActivity, R.string.socket_read_start, Toast.LENGTH_SHORT).show()
                }

                override fun onRead(p0: String?) {
                    Toast.makeText(this@MainActivity, R.string.socket_on_reading, Toast.LENGTH_SHORT).show()
                }

                override fun onDisconnect(id: String?, error: String?) {

                    if (id != null){

                        if (messageFragment != null && messageFragment!!.isVisible){
                            messageFragment!!.onDisconnected(id!!)
                        }


                        removeClient(id)

                        last_client_TV.setText(id + " disconnected");

                        Toast.makeText(this@MainActivity, R.string.socket_disconnected, Toast.LENGTH_SHORT).show()

                    }
                }
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initLanServer()
    }

    private fun initView(){

        clientListAdapter = GeneralListAdapter<ClientEntity>(R.layout.widget_client_item, BR.client, clients,
                BR.handler, clientItemHandler)


        var layoutManager = LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setHasFixedSize(true);

        recyclerView.adapter = clientListAdapter

        search_BTN.setOnClickListener {
            lanServer.search()
        }

    }

    private fun initLanServer(){

        lanServer.addHandler(object :PacketHandler<String>(CmsgOpCode.CMSG_MESSAGE_CODE, String::class.java){
            override fun parserError(id: String?, name: String?, message: String?) {

            }

            override fun handle(id: String, name: String, message: String?) {
                if (message != null){

                    addMessage(id, message)
                }
            }
        })

        lanServer.connect()
    }


    fun showDialogFragment() {
        var fragment = supportFragmentManager.findFragmentByTag("dialogFragment")
        if (fragment == null){
            fragment = MessageDialogFragment()
        }
        messageFragment = fragment as MessageDialogFragment
        messageFragment!!.show(supportFragmentManager, "dialogFragment")
    }

    private fun addClient(c : ClientEntity){
        clients.add(c)
        clientListAdapter.notifyDataSetChanged()
    }

    private fun removeClient(id: String){
        var c = getClient(id)
        clients.remove(c)

        clientListAdapter.notifyDataSetChanged()

    }

    private fun getClient(id: String): ClientEntity?{

        for (c in clients){
            if (c.address.equals(id)){
                return c
            }
        }

        return null
    }

    private fun addMessage(id: String, message: String){
        var c = getClient(id)
        if (c != null){
            c.messages.add(MessageEntity(MESSAGE_SENDER_OTHER, message))
        }

        if (messageFragment != null && messageFragment!!.isVisible){
            messageFragment!!.updateMessage()
        }
    }

    public fun sendMessage(client: ClientEntity, messsage: String){

        val json = mGson.toJson(messsage, String::class.java)

        lanServer.send(client.address, SmsgOpCode.SMSG_MESSAGE_CODE, json)

        client.messages.add(MessageEntity(MESSAGE_SENDER_SELF, messsage))

        if (messageFragment != null && messageFragment!!.isVisible){
            messageFragment!!.updateMessage()
        }
    }
}
