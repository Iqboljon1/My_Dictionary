package com.iraimjanov.dictionary

import android.app.AlertDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.iraimjanov.dictionary.databinding.AddAndEditCategoriesDialogBinding
import com.iraimjanov.dictionary.databinding.FragmentSettingsBinding
import com.iraimjanov.dictionary.db.AppDatabase
import com.iraimjanov.dictionary.models.Types

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var appDateBase: AppDatabase
    private var booleanAntiBag = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        appDateBase = AppDatabase.getInstance(requireActivity())
        buildBottomNavigationView()

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageAdd.setOnClickListener {
            when (menuItem) {
                "Categories" -> {
                    if (booleanAntiBag) {
                        addCategory()
                        booleanAntiBag = false
                    }
                }
                "Words" -> {
                    AddEditWordsFragment.mode = "add"
                    findNavController().navigate(R.id.action_settingsFragment_to_addEditWordsFragment)
                }
            }
        }

        return binding.root
    }

    private fun addCategory() {
        val addBinding = AddAndEditCategoriesDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireActivity()).create()

        addBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        addBinding.btnSave.setOnClickListener {
            val name = addBinding.edtName.text.toString().trim()
            if (name.isNotEmpty()) {
                try {
                    appDateBase.myDao().addTypes(Types(name))
                    dialog.cancel()
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(requireActivity(), "It already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireActivity(), "Row empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setOnCancelListener {
            booleanAntiBag = true
        }

        dialog.setView(addBinding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun buildBottomNavigationView() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerSetting) as NavHostFragment
        val navigationController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navigationController)
    }

    companion object {
        var menuItem: String = ""
    }

}