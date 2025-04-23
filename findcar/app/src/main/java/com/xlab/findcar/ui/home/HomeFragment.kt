package com.xlab.findcar.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.xlab.findcar.R
import com.xlab.findcar.databinding.FragmentHomeBinding
import com.xlab.findcar.ui.base.BaseFragment

class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupButtons()
    }

    private fun setupButtons() {
        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.nav_search)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.nav_add)
        }
    }

    override fun setupObservers() {
        // Setup observers here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 