package com.polarlooptheory.was.views.character.spells

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.views.adapters.abilities.character.SpellListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SpellListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SpellListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            SpellListAdapter()
        view.scenarioListRec.adapter = adapter
        return view
    }
}
