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
import kotlinx.android.synthetic.main.custom_armor.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.buttonSubmit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CustomArmorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_armor, container, false)
        view.buttonSubmit.setOnClickListener {
            GlobalScope.launch {
                //TODO update postaci
            }
        }
        return view
    }
}
