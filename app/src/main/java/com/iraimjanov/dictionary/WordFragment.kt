package com.iraimjanov.dictionary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.iraimjanov.dictionary.adapters.WordsRecyclerViewAdapter
import com.iraimjanov.dictionary.databinding.DeleteDialogBinding
import com.iraimjanov.dictionary.databinding.FragmentWordBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class WordFragment : Fragment() {
    private lateinit var binding: FragmentWordBinding
    private lateinit var wordsRecyclerViewAdapter: WordsRecyclerViewAdapter
    private lateinit var appDatabase: AppDatabase
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var menuBuilder: MenuBuilder
    private var booleanAntiBagDialog = true
    private var booleanAntiBagPopupMenu = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        SettingsFragment.menuItem = "Words"
        binding = FragmentWordBinding.inflate(layoutInflater)
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerParent) as NavHostFragment
        navController = navHostFragment.navController

        appDatabase = AppDatabase.getInstance(requireActivity())
        appDatabase.myDao().getAllWords()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                wordsRecyclerViewAdapter = WordsRecyclerViewAdapter(
                    buildArraylistWords(it as ArrayList<Words>),
                    object : WordsRecyclerViewAdapter.RvClickWords {
                        override fun more(view: View, words: Words) {
                            if (booleanAntiBagPopupMenu) {
                                buildPopupMenu(view, words)
                                booleanAntiBagPopupMenu = false
                            }
                        }

                    })
                binding.recyclerView.adapter = wordsRecyclerViewAdapter
            }
        return binding.root
    }

    private fun buildArraylistWords(arrayListWords: ArrayList<Words>): ArrayList<Words> {
        val arrayList = ArrayList<Words>()
        for (words in arrayListWords) {
            words.type = appDatabase.myDao().getTypeById(words.type!!.toInt())[0].name
            arrayList.add(words)
        }
        return arrayList
    }

    @SuppressLint("SetTextI18n")
    private fun buildDeleteDialog(words: Words) {
        val deleteBinding = DeleteDialogBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireActivity()).create()

        deleteBinding.tvInfo.text = "Do you want to delete this word?"

        deleteBinding.btnCancel.setOnClickListener {
            alertDialog.cancel()
        }


        deleteBinding.btnDelete.setOnClickListener {
            appDatabase.myDao().deleteWords(words)
            deleteImageFromFilesDir(words.image!!)
            Toast.makeText(requireActivity(), "Word deleted", Toast.LENGTH_SHORT).show()
            alertDialog.cancel()
        }

        alertDialog.setOnCancelListener {
            booleanAntiBagDialog = true
        }

        alertDialog.setView(deleteBinding.root)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun deleteImageFromFilesDir(patch: String) {
        if (requireActivity().filesDir.isDirectory) {
            val file = File(patch)
            for (i in requireActivity().filesDir.listFiles().indices) {
                if (requireActivity().filesDir.listFiles()[i].absolutePath == file.absolutePath) {
                    requireActivity().filesDir.listFiles()[i].delete()
                    break
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun buildPopupMenu(
        view: View,
        words: Words,
    ) {
        menuBuilder = MenuBuilder(requireActivity())
        val menuInflater = MenuInflater(requireActivity())
        menuInflater.inflate(R.menu.popup_menu, menuBuilder)
        val menuPopupHelper = MenuPopupHelper(requireActivity(), menuBuilder, view)
        menuPopupHelper.setForceShowIcon(true)
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.menu_edit -> {
                        AddEditWordsFragment.words = words
                        AddEditWordsFragment.mode = "edit"
                        navController.navigate(R.id.action_settingsFragment_to_addEditWordsFragment)
                    }

                    R.id.menu_delete -> {
                        if (booleanAntiBagDialog) {
                            buildDeleteDialog(words)
                            booleanAntiBagDialog = false
                        }
                    }
                }
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        menuPopupHelper.setOnDismissListener {
            booleanAntiBagPopupMenu = true
        }

        menuPopupHelper.show()
    }
}