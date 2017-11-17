package com.astatus.easysocketlansampleserver.adapter

import android.support.v7.widget.RecyclerView

import android.view.ViewGroup

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.databinding.DataBindingUtil
import android.view.View


/**
 * Created by Administrator on 2017/11/11.
 */
class GeneralListAdapter<T>: RecyclerView.Adapter<GeneralListAdapter.GeneralViewHolder> {

    private var mDatas: List<T>

    private var mBrDataId: Int

    private var mBrHandlerId: Int?

    private var mHandler: Any?

    private var mLaytoutId: Int

    constructor(layout_id: Int, br_data_id: Int, datas: List<T>, br_handler_id: Int?, handler: Any?):super(){

        mLaytoutId = layout_id
        mBrDataId = br_data_id
        mDatas = datas

        mBrHandlerId = br_handler_id
        mHandler = handler
    }

    constructor(layout_id: Int, br_id: Int, datas: List<T>)
            :this(layout_id, br_id, datas, null, null){
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
            holder.binding!!.setVariable(mBrDataId, mDatas.get(position))

            if (mBrHandlerId != null && mHandler != null){

                holder.binding!!.setVariable(mBrHandlerId!!, mHandler)
            }

            holder.itemView.setTag(mDatas.get(position))
        }
    }

    override fun getItemCount(): Int {
        return mDatas.count()
    }


    class GeneralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: ViewDataBinding? = null
    }
}