package com.polarlooptheory.was.views.character.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import kotlinx.android.synthetic.main.char_health.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CharacterHealthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.char_health, container, false)
        view.buttonSave2.setOnClickListener {
            GlobalScope.launch {
                //TODO update postaci
            }
        }
        view.buttonNext.setOnClickListener {
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterProficiencyFragment(), false)
            }
        }
        view.buttonBack.setOnClickListener {
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterStatsFragment(), false)
            }
        }
        return view
    }
}