package com.iraimjanov.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.iraimjanov.dictionary.adapters.BasicRecyclerViewAdapter
import com.iraimjanov.dictionary.adapters.BasicViewPagerAdapter
import com.iraimjanov.dictionary.databinding.FragmentBasicBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words

class BasicFragment : Fragment() {
    lateinit var binding: FragmentBasicBinding
    lateinit var appDateBase: AppDatabase
    lateinit var arrayListTypes: ArrayList<Types>
    lateinit var arrayListWords: ArrayList<Words>
    lateinit var hashMap: HashMap<String, ArrayList<Words>>
    lateinit var basicViewPagerAdapter: BasicViewPagerAdapter
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBasicBinding.inflate(layoutInflater)
        navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerParent) as NavHostFragment
        navController = navHostFragment.navController
        loadData()
        buildTabLayout()
        return binding.root
    }

    private fun loadData() {
        appDateBase = AppDatabase.getInstance(requireActivity())
        arrayListTypes = appDateBase.myDao().getAllTypesNoFlowable() as ArrayList<Types>
        arrayListWords = appDateBase.myDao().getAllWordsNoFlowable() as ArrayList<Words>
        arrayListWords = buildArraylistWords(arrayListWords)
        hashMap = buildHashMap(arrayListTypes, arrayListWords)
        basicViewPagerAdapter = BasicViewPagerAdapter(arrayListTypes,
            hashMap,
            object : BasicRecyclerViewAdapter.RvClickBasic {
                override fun next(words: Words) {
                    navController.navigate(R.id.action_homeFragment_to_showFragment , bundleOf("word" to words) )
                }
            })
        binding.viewPager.adapter = basicViewPagerAdapter
    }

    private fun buildTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = arrayListTypes[position].name
        }.attach()
    }

    private fun buildHashMap(
        arrayListTypes: ArrayList<Types>,
        arrayListWords: ArrayList<Words>,
    ): HashMap<String, ArrayList<Words>> {
        val hashMap = HashMap<String, ArrayList<Words>>()
        for (i in arrayListTypes) {
            hashMap[i.name!!] = buildArrayListForHashMap(i.name!!, arrayListWords)
        }
        return hashMap
    }

    private fun buildArrayListForHashMap(
        type: String,
        arrayListWords: ArrayList<Words>,
    ): ArrayList<Words> {
        val arrayList = ArrayList<Words>()
        for (i in arrayListWords) {
            if (i.type == type) {
                arrayList.add(i)
            }
        }
        return arrayList
    }

    private fun buildArraylistWords(arrayListWords: ArrayList<Words>): ArrayList<Words> {
        val arrayList = ArrayList<Words>()
        for (words in arrayListWords) {
            words.type = appDateBase.myDao().getTypeById(words.type!!.toInt())[0].name
            arrayList.add(words)
        }
        return arrayList
    }

}