package com.iraimjanov.dictionary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.dictionary.databinding.ItemBasicBinding
import com.iraimjanov.dictionary.models.Words

class BasicRecyclerViewAdapter(
    private val arrayListWords: ArrayList<Words>,
    private val rvClickBasic: RvClickBasic,
) :
    RecyclerView.Adapter<BasicRecyclerViewAdapter.VH>() {

    inner class VH(private val itemRV: ItemBasicBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(words: Words) {
            itemRV.tvTypeName.text = words.type
            itemRV.tvWordName.text = words.name
            itemRV.lyNext.setOnClickListener {
                rvClickBasic.next(words)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayListWords[position])
    }

    override fun getItemCount(): Int = arrayListWords.size

    interface RvClickBasic {
        fun next(words: Words)
    }

}