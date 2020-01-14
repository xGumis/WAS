package com.polarlooptheory.was.views.custom.eq

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.polarlooptheory.was.R
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.buttonSubmit
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CustomGearFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_gear_vehicles, container, false)
        view.buttonSubmit.setOnClickListener {
            GlobalScope.launch {
                //TODO update postaci
            }
        }
        return view
    }
}