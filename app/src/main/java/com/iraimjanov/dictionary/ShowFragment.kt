package com.iraimjanov.dictionary

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.iraimjanov.dictionary.databinding.FragmentShowBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Words

class ShowFragment : Fragment() {
    private lateinit var binding: FragmentShowBinding
    private lateinit var appDataBase: AppDatabase
    private lateinit var words: Words
    private var like: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShowBinding.inflate(layoutInflater)
        loadData()
        showActivity()
        words.type = appDataBase.myDao().getTypeByName(words.type!!)[0].id.toString()
        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageLike.setOnClickListener {
            val boolean = like
            like = if (boolean) {
                binding.imageLike.setImageResource(R.drawable.ic_like)
                false
            } else {
                binding.imageLike.setImageResource(R.drawable.ic_liked)
                true
            }
            words.like = like.toString()
            appDataBase.myDao().updateWords(words)
        }

        return binding.root
    }

    private fun loadData() {
        appDataBase = AppDatabase.getInstance(requireActivity())
        words = arguments?.getSerializable("word") as Words
        like = words.like.toBoolean()
    }

    @SuppressLint("SetTextI18n")
    private fun showActivity() {
        binding.tvActionBarWordName.text = words.name
        binding.tvWordName.text = "Word: ${words.name}"
        binding.tvWordType.text = "Type: ${words.type}"
        binding.tvDescription.text = "Description: ${words.description}"
        Glide.with(requireActivity()).load(words.image)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_image)).centerCrop()
            .into(binding.imageView)
        if (like) {
            binding.imageLike.setImageResource(R.drawable.ic_liked)
        } else {
            binding.imageLike.setImageResource(R.drawable.ic_like)
        }
    }

}