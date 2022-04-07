package com.iraimjanov.dictionary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.dictionary.databinding.ItemRecyclerViewBinding
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words

class BasicViewPagerAdapter(
    private val arrayListTypes: ArrayList<Types>,
    private val hashMap: HashMap<String, ArrayList<Words>>,
    private val rvClickBasic: BasicRecyclerViewAdapter.RvClickBasic
) :
    RecyclerView.Adapter<BasicViewPagerAdapter.VH>() {

    inner class VH(var itemRV: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(position: Int) {
            itemRV.root.adapter = BasicRecyclerViewAdapter(hashMap[arrayListTypes[position].name]!! , object : BasicRecyclerViewAdapter.RvClickBasic{
                override fun next(words: Words) {
                    rvClickBasic.next(words)
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = arrayListTypes.size

}