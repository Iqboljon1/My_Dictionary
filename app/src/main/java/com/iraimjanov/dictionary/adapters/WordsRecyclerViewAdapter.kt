package com.iraimjanov.dictionary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.dictionary.R
import com.iraimjanov.dictionary.databinding.ItemWordsBinding
import com.iraimjanov.dictionary.models.Words

class WordsRecyclerViewAdapter(
    private val arrayListWords: ArrayList<Words>,
    private val rvClickWords: RvClickWords,
) :
    RecyclerView.Adapter<WordsRecyclerViewAdapter.VH>() {

    inner class VH(private val itemRV: ItemWordsBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(words: Words) {
            itemRV.tvTypeName.text = words.type
            itemRV.tvWordName.text = words.name
            itemRV.imageMore.setOnClickListener {
                rvClickWords.more(it, words)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemWordsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayListWords[position])

    }

    override fun getItemCount(): Int = arrayListWords.size

    interface RvClickWords {
        fun more(view: View, words: Words)
    }

}