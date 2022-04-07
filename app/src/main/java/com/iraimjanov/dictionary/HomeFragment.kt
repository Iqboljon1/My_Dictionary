package com.iraimjanov.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.iraimjanov.dictionary.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        buildBottomNavigationView()

        binding.imageSetting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        return binding.root
    }

    private fun buildBottomNavigationView() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        val navigationController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navigationController)
    }

    companion object {
        var menuItem: String = ""
    }

}