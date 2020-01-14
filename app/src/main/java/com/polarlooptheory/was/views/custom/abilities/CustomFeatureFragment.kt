package com.polarlooptheory.was.views.custom.abilities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.views.character.statistics.CharacterBackgroundFragment
import kotlinx.android.synthetic.main.char_base_info.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CustomFeatureFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_feat_trait_magicschool, container, false)
        view.buttonSubmit.setOnClickListener {
            GlobalScope.launch {
                //TODO update postaci
            }
        }
        return view
    }
}


