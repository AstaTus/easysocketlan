package com.astatus.easysocketlansampleserver.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.astatus.easysocketlan.LanServer
import com.astatus.easysocketlan.PacketHandler
import com.astatus.easysocketlan.entity.ClientDeviceEntity
import com.astatus.easysocketlan.listener.ILanServerListener
//import com.astatus.easysocketlansampleserver.BR


import com.astatus.easysocketlansampleserver.R
import com.astatus.easysocketlansampleserver.entity.ClientEntity
import com.astatus.easysocketlansampleserver.entity.MessageEntity
import com.astatus.easysocketlansampleserver.fragment.MessageFragment
import com.astatus.easysocketlansampleserver.lan.CmsgOpCode
import com.astatus.easysocketlansampleserver.lan.SmsgOpCode
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.astatus.easysocketlansampleserver.fragment.ClientListFragment
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    companion object {

        private val LAN_SERVER_SEARCH_PORT: Int = 9000
        private val LAN_SERVER_SOCKET_PORT: Int = 10001

        private val MESSAGE_SENDER_SELF: Int = 0
        private val MESSAGE_SENDER_OTHER: Int = 1
    }

    private lateinit var messageFragment: MessageFragment

    private lateinit var clientListFragment: ClientListFragment

    private var clients: ArrayList<ClientEntity> = ArrayList<ClientEntity>()

    private var mGson: Gson = Gson()


    private var clientListFragmentListener =  object : ClientListFragment.IClientListFragmentListener{
        override fun changeMessageFragment(client: ClientEntity) {
            this@MainActivity.changeMessageFragment(client)
        }
    }


    private var messageFragmentListener = object : MessageFragment.IMessageFragmentListener{
        override fun sendMessage(client: ClientEntity, messsage: String) {
            val json = mGson.toJson(messsage, String::class.java)

            lanServer.send(client.address, SmsgOpCode.SMSG_MESSAGE_CODE, json)

            client.messages.add(MessageEntity(MainActivity.MESSAGE_SENDER_SELF, messsage))

            if (messageFragment.isVisible){
                messageFragment.updateMessage()
            }
        }

        override fun changeClientListFragment() {
            this@MainActivity.changeClientListFragment()
        }
    }


    private var lanServer: LanServer =
            LanServer(LAN_SERVER_SEARCH_PORT, LAN_SERVER_SOCKET_PORT, object :ILanServerListener{


                override fun onSearchStart() {

                    Toast.makeText(this@MainActivity, R.string.search_start, Toast.LENGTH_SHORT).show()
                }

                override fun onSearching() {

                    Toast.makeText(this@MainActivity, R.string.searching_boradcast, Toast.LENGTH_SHORT).show()
                }

                override fun onSearchEnd() {

                    Toast.makeText(this@MainActivity, R.string.search_finish, Toast.LENGTH_SHORT).show()
                }

                override fun onSearchError(p0: String?) {

                    Toast.makeText(this@MainActivity, R.string.search_error, Toast.LENGTH_SHORT).show()
                }

                override fun onConnectStart() {

                    Toast.makeText(this@MainActivity, R.string.connect_start, Toast.LENGTH_SHORT).show()
                }

                override fun onConnectError(p0: String?) {

                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle(this@MainActivity.getString(R.string.common_hint))
                    builder.setMessage(this@MainActivity.getString(R.string.lan_connect_error))

                    builder.setPositiveButton("确定",
                            DialogInterface.OnClickListener { dialogInterface, i -> finish(); })

                    val dialog = builder.create()
                    dialog.show()
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

                override fun onDisconnect(id: String, error: String?) {

                    if (messageFragment.isVisible){
                        messageFragment.onDisconnected(id)
                    }

                    removeClient(id)

                    Toast.makeText(this@MainActivity, R.string.socket_disconnected, Toast.LENGTH_SHORT).show()


                }
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLanServer()

        initFragment()

        changeClientListFragment()
    }

    private fun initFragment(){
        messageFragment = MessageFragment()
        messageFragment.setParam(messageFragmentListener)

        clientListFragment = ClientListFragment()
        clientListFragment.setParam(clients, clientListFragmentListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null){
            when(item.itemId){
                R.id.action_bar_search->{
                    lanServer.search()
                }
            }
        }

        return super.onOptionsItemSelected(item)
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

    fun changeClientListFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FrameLayout, clientListFragment).commit();
    }

    fun changeMessageFragment(client: ClientEntity) {

        messageFragment.setClientEntity(client)

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FrameLayout, messageFragment).commit();


    }

    private fun addClient(c : ClientEntity){
        clients.add(c)

        if (clientListFragment.isVisible){
            clientListFragment.updateList()
        }

    }

    private fun removeClient(id: String){
        var c = getClient(id)
        clients.remove(c)

        if (clientListFragment.isVisible){
            clientListFragment.updateList()
        }

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

        if ( messageFragment.isVisible){
            messageFragment.updateMessage()
        }
    }
}
