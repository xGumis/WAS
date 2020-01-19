package com.polarlooptheory.was.views.character.abilities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mFeature
import com.polarlooptheory.was.views.adapters.abilities.character.CharacterFeaturesAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CharacterFeaturesFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: CharacterFeaturesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            CharacterFeaturesAdapter()
        view.scenarioListRec.adapter = adapter
        return view
    }
}
