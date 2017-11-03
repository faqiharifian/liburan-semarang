package com.arifian.training.liburansemarang.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arifian.training.liburansemarang.R
import com.arifian.training.liburansemarang.Utils.GlideApp
import com.arifian.training.liburansemarang.databinding.ItemHeaderBinding
import com.arifian.training.liburansemarang.databinding.ItemWisataBinding
import com.arifian.training.liburansemarang.models.Header
import com.arifian.training.liburansemarang.models.Item
import com.arifian.training.liburansemarang.models.Wisata
import com.arifian.training.liburansemarang.models.remote.WisataClient

/**
 * Created by faqih on 30/10/17.
 */

class WisataAdapter(wisataArrayList: List<Item>, var listener: OnWisataClickListener) : RecyclerView.Adapter<WisataAdapter.ViewHolder>() {
    private val list = ArrayList<Item>()

    init {
        this.list.addAll(wisataArrayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var binding: Any?
        var view: View
        if(viewType == Item.TYPE_HEADER){
            binding = ItemHeaderBinding.inflate(inflater, parent, false)
            view = (binding as ItemHeaderBinding).root
        }else {
            binding = ItemWisataBinding.inflate(inflater, parent, false)
            view = (binding as ItemWisataBinding).root
        }
        return ViewHolder(binding, view)
    }

    override fun onBindViewHolder(holder: WisataAdapter.ViewHolder, position: Int) {
        val wisata = list[position]
        holder.bind(wisata, listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list.get(position).type
    }

    fun swapData(wisataArrayList: ArrayList<Item>){
        this.list.clear()
        this.list.addAll(wisataArrayList)
        this.notifyDataSetChanged()
    }

    interface OnWisataClickListener {
        fun onItemClick(wisata: Wisata)
    }

    inner class ViewHolder(private val binding: Any?, val view: View) : RecyclerView.ViewHolder(view){

        fun bind(item: Item, listener: OnWisataClickListener){
            if(item.type == Item.TYPE_HEADER){
                var currentItem = item as Header
                var currentBinding = binding as ItemHeaderBinding
                currentBinding.header = currentItem
            }else{
                var currentItem = item as Wisata
                var currentBinding = binding as ItemWisataBinding
                binding.wisata = currentItem

                GlideApp.with(currentBinding.ivItemGambar)
                        .load(WisataClient.IMAGE_URL + currentItem.gambarWisata!!)
                        .error(R.drawable.no_image_found)
                        .centerCrop()
                        .into(currentBinding.ivItemGambar)

                itemView.setOnClickListener { v -> listener.onItemClick(currentItem) }
            }

        }
    }
}
