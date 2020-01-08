package com.polarlooptheory.was.views.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.views.start.RegisterFragment
import kotlinx.android.synthetic.main.char_stats.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterStatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.char_stats, container, false)
        view.buttonSave2.setOnClickListener {
            GlobalScope.launch {
                //TODO update postaci
            }
        }
        view.buttonNext.setOnClickListener {
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterHealthFragment(), false)
            }
        }
        view.buttonBack.setOnClickListener {
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterBackgroundFragment(), false)
            }
        }
        return view
    }
}