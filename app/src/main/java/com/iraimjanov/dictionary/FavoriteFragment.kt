package com.iraimjanov.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.iraimjanov.dictionary.adapters.BasicRecyclerViewAdapter
import com.iraimjanov.dictionary.databinding.FragmentFavoriteBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Words

class FavoriteFragment : Fragment() {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var appDataBase: AppDatabase
    lateinit var basicRecyclerViewAdapter: BasicRecyclerViewAdapter
    lateinit var arrayListWords: ArrayList<Words>
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        loadData()
        return binding.root
    }

    private fun loadData() {
        appDataBase = AppDatabase.getInstance(requireActivity())
        arrayListWords = appDataBase.myDao().getAllWordsNoFlowable() as ArrayList<Words>
        arrayListWords = buildArraylistWords(arrayListWords)
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerParent) as NavHostFragment
        navController = navHostFragment.navController
        basicRecyclerViewAdapter = BasicRecyclerViewAdapter(arrayListWords,
            object : BasicRecyclerViewAdapter.RvClickBasic {
                override fun next(words: Words) {
                    navController.navigate(R.id.action_homeFragment_to_showFragment,
                        bundleOf("word" to words))
                }
            })
        binding.recyclerView.adapter = basicRecyclerViewAdapter
    }

    private fun buildArraylistWords(arrayListWords: ArrayList<Words>): ArrayList<Words> {
        val arrayList = ArrayList<Words>()
        for (words in arrayListWords) {
            if (words.like.toBoolean()) {
                words.type = appDataBase.myDao().getTypeById(words.type!!.toInt())[0].name
                arrayList.add(words)
            }
        }
        return arrayList
    }

}