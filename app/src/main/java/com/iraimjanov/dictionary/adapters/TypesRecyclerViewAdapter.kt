package com.iraimjanov.dictionary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.dictionary.R
import com.iraimjanov.dictionary.databinding.ItemTypesBinding
import com.iraimjanov.dictionary.models.Types

class TypesRecyclerViewAdapter(
    private val arrayListTypes: ArrayList<Types>,
    private val rvClickTypes: RvClickTypes,
) :
    RecyclerView.Adapter<TypesRecyclerViewAdapter.VH>() {

    inner class VH(private var itemRV: ItemTypesBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(types: Types) {
            itemRV.tvName.text = types.name
            itemRV.imageMore.setOnClickListener {
                    rvClickTypes.more(it , types)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemTypesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayListTypes[position])

    }

    override fun getItemCount(): Int = arrayListTypes.size

    interface RvClickTypes {
        fun more(view:View , types: Types)
    }
}