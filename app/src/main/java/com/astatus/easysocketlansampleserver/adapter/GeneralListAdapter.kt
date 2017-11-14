package com.astatus.easysocketlansampleserver.adapter

import android.support.v7.widget.RecyclerView

import android.view.ViewGroup

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.databinding.DataBindingUtil
import com.astatus.easysocketlansampleserver.R
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View


/**
 * Created by Administrator on 2017/11/11.
 */
class GeneralListAdapter<T>: RecyclerView.Adapter<GeneralListAdapter.GeneralViewHolder> {

    private var mDatas: List<T>

    private var mHandlers: HashMap<Int, Any>?

    private var mBrId: Int

    private var mLaytoutId: Int

    constructor(layout_id: Int, br_id: Int, datas: List<T>, handlers: HashMap<Int, Any>?):super(){

        mLaytoutId = layout_id
        mBrId = br_id
        mDatas = datas
        mHandlers = handlers
    }

    constructor(layout_id: Int, br_id: Int, datas: List<T>)
            :this(layout_id, br_id, datas, null){
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, mLaytoutId, parent, false)
        val viewHolder = GeneralViewHolder(binding.getRoot())
        viewHolder.binding = binding
        return viewHolder
    }

    override fun onBindViewHolder(holder: GeneralViewHolder, position: Int) {
        if (holder.binding != null){
            holder.binding!!.setVariable(mBrId, mDatas.get(position))

            if (mHandlers != null){
                for (handler in  mHandlers!!){
                    holder.binding!!.setVariable(handler.key, handler.value)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return mDatas.count()
    }


    class GeneralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: ViewDataBinding? = null
    }
}