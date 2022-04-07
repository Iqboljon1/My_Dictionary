package com.iraimjanov.dictionary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import com.iraimjanov.dictionary.adapters.TypesRecyclerViewAdapter
import com.iraimjanov.dictionary.databinding.AddAndEditCategoriesDialogBinding
import com.iraimjanov.dictionary.databinding.DeleteDialogBinding
import com.iraimjanov.dictionary.databinding.FragmentCategoryBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var appDataBase: AppDatabase
    private lateinit var typesRecyclerViewAdapter: TypesRecyclerViewAdapter
    private lateinit var menuBuilder: MenuBuilder
    private var booleanAntiBagDialog = true
    private var booleanAntiBagPopupMenu = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        SettingsFragment.menuItem = "Categories"
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        appDataBase = AppDatabase.getInstance(requireActivity())

        appDataBase.myDao().getAllTypes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                typesRecyclerViewAdapter = TypesRecyclerViewAdapter(
                    it as ArrayList<Types>,
                    object : TypesRecyclerViewAdapter.RvClickTypes {
                        override fun more(view: View, types: Types) {
                            if (booleanAntiBagPopupMenu) {
                                buildPopupMenu(view, types)
                                booleanAntiBagPopupMenu = false
                            }
                        }
                    })
                binding.recyclerView.adapter = typesRecyclerViewAdapter
            }
        return binding.root
    }

    private fun editCategory(types: Types) {
        val editBinding = AddAndEditCategoriesDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireActivity()).create()

        editBinding.edtName.setText(types.name)

        editBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        editBinding.btnSave.setOnClickListener {
            val name = editBinding.edtName.text.toString().trim()
            val id = types.id
            if (name.isNotEmpty()) {
                try {
                    appDataBase.myDao().updateTypes(Types(id, name))
                    dialog.cancel()
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(requireActivity(), "It already exists", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireActivity(), "Row empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setOnCancelListener {
            booleanAntiBagDialog = true
        }

        dialog.setView(editBinding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun buildDeleteDialog(types: Types) {
        val deleteBinding = DeleteDialogBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireActivity()).create()

        deleteBinding.tvInfo.text = "Do you want to delete this category?"

        deleteBinding.btnCancel.setOnClickListener {
            alertDialog.cancel()
        }

        deleteBinding.btnDelete.setOnClickListener {
            appDataBase.myDao().deleteTypes(types)
            val arrayList =
                appDataBase.myDao().selectWordsByTypesId(types.id.toString()) as ArrayList<Words>
            for (words in arrayList) {
                appDataBase.myDao().deleteWords(words)
                deleteImageFromFilesDir(words.image!!)
            }
            Toast.makeText(requireActivity(), "Category deleted", Toast.LENGTH_SHORT).show()
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
        types: Types,
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
                        if (booleanAntiBagDialog) {
                            editCategory(types)
                            booleanAntiBagDialog = false
                        }
                    }

                    R.id.menu_delete -> {
                        if (booleanAntiBagDialog) {
                            buildDeleteDialog(types)
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

