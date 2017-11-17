package com.astatus.easysocketlansampleserver.fragment

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.astatus.easysocketlansampleserver.BR
import com.astatus.easysocketlansampleserver.R
import com.astatus.easysocketlansampleserver.activity.MainActivity
import com.astatus.easysocketlansampleserver.adapter.GeneralListAdapter
import com.astatus.easysocketlansampleserver.databinding.DialogMessageBinding
import com.astatus.easysocketlansampleserver.entity.ClientEntity
import com.astatus.easysocketlansampleserver.entity.MessageEntity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.WindowManager
import android.util.DisplayMetrics

/**
 * Created by Administrator on 2017/11/14.
 */
class MessageDialogFragment(): DialogFragment() {

    private lateinit var binding: DialogMessageBinding;
    private var clientEntity: ClientEntity? = null

    private var messageListAdapter: GeneralListAdapter<MessageEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<DialogMessageBinding>(layoutInflater, R.layout.dialog_message, container, false)

//        val  window: Window = getDialog().getWindow();
//        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));//注意此处
//
//        val dm = DisplayMetrics()
//        activity.windowManager.getDefaultDisplay().getMetrics(dm)
//
//        var p: WindowManager.LayoutParams = dialog.window.attributes; // 获取对话框当前的参数值
//
//        p.height = (dm.heightPixels * 0.9).toInt()
//        p.width = (dm.widthPixels * 0.9).toInt()
//        window.setLayout(p.width, p.height);    //这2行,和上面的一样,注意顺序就行;
//
//        //这个属性需要配合透明背景颜色,才会真正的 MATCH_PARENT
//         var attributes = window.getAttributes();
//        attributes.width = p.width
//        attributes.height = p.height
//        window.attributes = attributes

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
                            parent.sendMessage(clientEntity!!, message)

                            binding.chatET.setText("".toCharArray(), 0, 0)
                        }
                    }
                }

                return true;
            }
        })

        binding.closeBTN.setOnClickListener {
            dismiss()
        }

        var layoutManager = LinearLayoutManager(activity);
        binding.chatListRV.setLayoutManager(layoutManager)
        binding.chatListRV.setHasFixedSize(true);
    }

    override fun onStart() {
        super.onStart()

        if(clientEntity != null){
            messageListAdapter = GeneralListAdapter<MessageEntity>(R.layout.widget_chat_item, BR.message, clientEntity!!.messages,
                    null, null)
            binding.chatListRV.adapter = messageListAdapter


            binding.nameTV.setText(clientEntity!!.name)
            binding.addressTV.setText(clientEntity!!.address)
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
                dismiss()
            }
        }
    }
}