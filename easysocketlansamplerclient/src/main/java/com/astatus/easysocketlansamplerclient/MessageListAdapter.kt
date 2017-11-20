package com.astatus.easysocketlansamplerclient

import android.support.v7.widget.RecyclerView

import android.view.ViewGroup

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.databinding.DataBindingUtil
import android.view.View


/**
 * Created by Administrator on 2017/11/11.
 */
class MessageListAdapter : RecyclerView.Adapter<MessageListAdapter.GeneralViewHolder> {

    companion object {
        private val MESSAGE_SENDER_SELF: Int = 0
        private val MESSAGE_SENDER_OTHER: Int = 1

        private val MESSAGE_CHAT_RIGHT:Int = 0
        private val MESSAGE_CHAT_LEFT:Int = 1
    }

    private var mDatas: List<MessageEntity>


    constructor(datas: List<MessageEntity>):super(){
        mDatas = datas

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        var binding: ViewDataBinding? = null
        if (viewType == MESSAGE_CHAT_LEFT){
            binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.widget_left_chat_item, parent, false)
        }else{
            binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.widget_right_chat_item, parent, false)
        }

        val viewHolder = GeneralViewHolder(binding.getRoot())
        viewHolder.binding = binding

        return viewHolder
    }

    override fun onBindViewHolder(holder: GeneralViewHolder, position: Int) {
        if (holder.binding != null){
            if (getItemViewType(position) == MESSAGE_CHAT_LEFT){
                holder.binding!!.setVariable(BR.left_chat_message, mDatas.get(position))
            }else{
                holder.binding!!.setVariable(BR.right_chat_message, mDatas.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun getItemViewType(position: Int): Int {
        var message: MessageEntity = mDatas.get(position)
        if(message.sender == MESSAGE_SENDER_SELF){
            return MESSAGE_CHAT_RIGHT
        }else
            return MESSAGE_CHAT_LEFT
    }


    class GeneralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: ViewDataBinding? = null
    }
}