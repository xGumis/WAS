package com.polarlooptheory.was.views.character.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.views.character.statistics.CharacterBackgroundFragment
import kotlinx.android.synthetic.main.char_base_info.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CharacterBaseInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.char_base_info, container, false)
        view.buttonSave4.setOnClickListener {
            GlobalScope.launch {
                //TODO update postaci
            }
        }
        view.buttonNext.setOnClickListener {
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterBackgroundFragment(), false)
            }
        }
        return view
    }
}