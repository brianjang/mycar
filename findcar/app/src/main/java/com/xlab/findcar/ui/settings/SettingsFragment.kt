package com.xlab.findcar.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xlab.findcar.R
import com.xlab.findcar.ui.base.BaseFragment

class SettingsFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun setupViews() {
        // Setup views here
    }

    override fun setupObservers() {
        // Setup observers here
    }
} 