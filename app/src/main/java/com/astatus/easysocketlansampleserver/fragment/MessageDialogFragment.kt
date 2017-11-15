package com.astatus.easysocketlansampleserver.fragment

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.astatus.easysocketlansampleserver.BR
import com.astatus.easysocketlansampleserver.R
import com.astatus.easysocketlansampleserver.activity.MainActivity
import com.astatus.easysocketlansampleserver.adapter.GeneralListAdapter
import com.astatus.easysocketlansampleserver.databinding.DialogMessageBinding
import com.astatus.easysocketlansampleserver.entity.ClientEntity
import com.astatus.easysocketlansampleserver.entity.MessageEntity


/**
 * Created by Administrator on 2017/11/14.
 */
class MessageDialogFragment(): DialogFragment() {

    private lateinit var binding: DialogMessageBinding;
    private var clientEntity: ClientEntity? = null

    private var messageListAdapter: GeneralListAdapter<MessageEntity>? = null

    private var chatTextWatcher = object : TextWatcher{
        override fun afterTextChanged(editable: Editable?) {

            binding.sendBTN.isEnabled = false
            if (editable != null){
                if (editable.length > 0){
                    binding.sendBTN.isEnabled = true
                }
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val  window: Window = getDialog().getWindow();
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(-1, -2);//这2行,和上面的一样,注意顺序就行;
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DataBindingUtil.inflate<DialogMessageBinding>(layoutInflater, R.layout.dialog_message, container, false)

        initView()

        return binding.root;
    }

    protected fun initView(){

        binding.chatET.addTextChangedListener(chatTextWatcher)

        binding.sendBTN.setOnClickListener {

            var message: String = binding.chatET.text.toString()
            if (message.length > 0){
                if (clientEntity != null){
                    var parent = activity as MainActivity
                    parent.sendMessage(clientEntity!!, message)
                }
            }
        }
    }

    public fun setClientEntity(entity: ClientEntity){

        messageListAdapter = GeneralListAdapter<MessageEntity>(R.layout.widget_chat_item, BR.message, entity.messages,
                null, null)
        clientEntity = entity
        binding.nameTV.setText(entity.name)
        binding.addressTV.setText(entity.address)
    }

    public fun updateMessage(){

        if (messageListAdapter != null){
            messageListAdapter!!.notifyItemChanged(messageListAdapter!!.itemCount - 1)
        }
    }

    public fun onDisconnected(address: String){

        if (clientEntity != null){
            if (address.equals(clientEntity!!.address)){
                dismiss()
            }
        }
    }
}