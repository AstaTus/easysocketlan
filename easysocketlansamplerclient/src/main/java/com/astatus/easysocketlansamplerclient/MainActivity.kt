package com.astatus.easysocketlansamplerclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.astatus.easysocketlan.LanClient
import com.astatus.easysocketlan.PacketHandler
import com.astatus.easysocketlan.listener.ILanClientListener
import com.astatus.easysocketlansampleserver.entity.MessageEntity
import com.astatus.easysocketlansampleserver.lan.SmsgOpCode
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MenuInflater
import android.view.MenuItem


class MainActivity : AppCompatActivity() {

    companion object {
        private val CLIENT_NAME: String = "alien"
        private val LAN_SEARCH_PORT: Int = 9000

        private val LAN_SEARCH_STATE: Int = 1
        private val LAN_CONNECT_STATE: Int = 2
        private val LAN_DISCONNECT_STATE: Int = 3


        private val MESSAGE_SENDER_SELF: Int = 0
        private val MESSAGE_SENDER_OTHER: Int = 1
    }

    private var lanState: Int = LAN_SEARCH_STATE

    private var gson = Gson()

    private lateinit var lanClient: LanClient

    private lateinit var messageAdapter: GeneralListAdapter<MessageEntity>

    private var messages: ArrayList<MessageEntity> = ArrayList<MessageEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        initLanClient()
    }

    private fun initView(){
        messageAdapter =
                GeneralListAdapter<MessageEntity>(R.layout.widget_chat_item, BR.message, messages)

        var layoutManager = LinearLayoutManager(this);
        chat_list_RV.setLayoutManager(layoutManager)
        chat_list_RV.setHasFixedSize(true);

        chat_list_RV.adapter = messageAdapter

        lan_BTN.setText(R.string.btn_search_text)
        lan_BTN.isEnabled = true
        lan_BTN.setOnClickListener {

            if(lanState.equals(LAN_SEARCH_STATE)){
                lan_BTN.isEnabled = false
                lanClient.start()
            }else if (lanState.equals(LAN_CONNECT_STATE)){
                lan_BTN.isEnabled = false
                lanClient.connect()
            }else if (lanState.equals(LAN_DISCONNECT_STATE)){
                lan_BTN.isEnabled = true
                lanClient.stop()
            }
        }

        chat_ET.setOnEditorActionListener(object :TextView.OnEditorActionListener {

            override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    if (lanState.equals(LAN_DISCONNECT_STATE)){
                        var message: String = chat_ET.text.toString()
                        if (message.length > 0) {

                            val json = gson.toJson(message, String::class.java)
                            lanClient.send(CmsgOpCode.CMSG_MESSAGE_CODE, json)

                            addMessage(MESSAGE_SENDER_SELF, message)

                            chat_ET.setText("".toCharArray(), 0, 0)
                        }
                    }
                }

                return true;
            }
        })


        name_TV.setText(CLIENT_NAME)

    }

    private fun addMessage(sender: Int, message: String){
        var entity: MessageEntity = MessageEntity(sender, message)
        messages.add(entity)

        messageAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null){
            when(item.itemId){

                R.id.action_bar_info->{

                }
                R.id.action_bar_lan->{

                }
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun initLanClient(){
        lanClient = LanClient(CLIENT_NAME, LAN_SEARCH_PORT, object :ILanClientListener{


            override fun onSearchStart() {
                lan_state_TV.setText(R.string.lan_search_running)
            }

            override fun onSearchSuccess() {
                lan_state_TV.setText(R.string.lan_search_finded)

                lanState = LAN_CONNECT_STATE
                lan_BTN.setText(R.string.btn_connect_text)
            }

            override fun onSearchError(error: String?) {

                lan_state_TV.setText(R.string.lan_search_stop)
                lanState = LAN_SEARCH_STATE
                lan_BTN.isEnabled = true
                lan_BTN.setText(R.string.btn_search_text)

                Toast.makeText(this@MainActivity, "onSearchError:" + error!!, Toast.LENGTH_SHORT).show()
            }


            override fun onConnectStart() {
                lan_state_TV.setText(R.string.lan_link_connect_start)
            }

            override fun onConnected(id: String) {
                lan_state_TV.setText(R.string.lan_link_connecting)

                address_TV.setText(id)


                lanState = LAN_DISCONNECT_STATE
                lan_BTN.setText(R.string.btn_reset_text)
                lan_BTN.isEnabled = true

            }

            override fun onConnectError(error: String?) {

                lan_state_TV.setText(R.string.lan_link_disconnect)

                lan_BTN.isEnabled = true

                Toast.makeText(this@MainActivity, "onConnectError:" + error!!, Toast.LENGTH_SHORT).show()

            }

            override fun onReadStart(id: String?) {
            }

            override fun onWriteStart(id: String?) {
            }

            override fun onRead(id: String?) {
                }

            override fun onWrite(id: String?) {
                }

            override fun onDisconnect(id: String?, error: String?) {
                lan_state_TV.setText(R.string.lan_link_disconnect)
                lan_BTN.setText(R.string.btn_search_text)
                lan_BTN.isEnabled = true
                lanState = LAN_SEARCH_STATE

                Toast.makeText(this@MainActivity, "onDisconnect:" + error, Toast.LENGTH_SHORT).show()

            }
        })


        lanClient.addHandler(object : PacketHandler<String>(SmsgOpCode.SMSG_MESSAGE_CODE, String::class.java){
            override fun parserError(id: String?, name: String?, message: String?) {

            }

            override fun handle(id: String, name: String, message: String?) {
                if (message != null){
                    addMessage(MESSAGE_SENDER_OTHER, message)
                }
            }
        })
    }
}
