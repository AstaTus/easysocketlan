package com.astatus.easysocketlansampleserver.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.astatus.easysocketlansampleserver.R
import com.astatus.easysocketlansampleserver.activity.MainActivity
import com.astatus.easysocketlansampleserver.entity.ClientEntity
import com.astatus.easysocketlansampleserver.adapter.MessageListAdapter
import com.astatus.easysocketlansampleserver.databinding.FragmentMessageBinding
import com.astatus.easysocketlansampleserver.entity.MessageEntity
import com.astatus.easysocketlansampleserver.lan.SmsgOpCode

/**
 * Created by Administrator on 2017/11/14.
 */
class MessageFragment(): Fragment() {

    private lateinit var binding: FragmentMessageBinding;

    private var clientEntity: ClientEntity? = null

    private var messageListAdapter: MessageListAdapter? = null

    private lateinit var listener: IMessageFragmentListener

    interface IMessageFragmentListener{

        fun sendMessage(client: ClientEntity, messsage: String)

        fun changeClientListFragment()
    }

    public fun setParam(listener: IMessageFragmentListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<FragmentMessageBinding>(layoutInflater, R.layout.fragment_message, container, false)

        initView()

        return binding.root;
    }

    protected fun initView(){

        binding.chatET.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    var message: String = binding.chatET.text.toString()
                    if (message.length > 0){
                        if (clientEntity != null){
                            var parent = activity as MainActivity
                            listener.sendMessage(clientEntity!!, message)

                            binding.chatET.setText("".toCharArray(), 0, 0)
                        }
                    }
                }

                return true;
            }
        })


        var layoutManager = LinearLayoutManager(activity);
        binding.chatListRV.setLayoutManager(layoutManager)
        binding.chatListRV.setHasFixedSize(true);
    }

    override fun onStart() {
        super.onStart()

        if(clientEntity != null){
            messageListAdapter = MessageListAdapter(clientEntity!!.messages)
            binding.chatListRV.adapter = messageListAdapter
        }
    }

    public fun setClientEntity(entity: ClientEntity){

        clientEntity = entity
    }

    public fun updateMessage(){

        if (messageListAdapter != null){
            messageListAdapter!!.notifyDataSetChanged()
        }
    }

    public fun onDisconnected(address: String){

        if (clientEntity != null){
            if (address.equals(clientEntity!!.address)){

            }
        }
    }


}