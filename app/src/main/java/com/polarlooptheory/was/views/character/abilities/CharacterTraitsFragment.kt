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
import com.polarlooptheory.was.model.abilities.mTrait
import com.polarlooptheory.was.views.adapters.abilities.character.CharacterTraitsAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CharacterTraitsFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CharacterTraitsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val list: MutableList<mTrait> = mutableListOf()
        Scenario.connectedScenario.chosenCharacter?.abilities?.traits?.forEach {
            GlobalScope.launch(Dispatchers.Main) {
                val req =
                    async { Abilities.getTraits(Scenario.connectedScenario.scenario, it) }.await()
                if(!req.isNullOrEmpty()) list.addAll(req)
            }
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            CharacterTraitsAdapter(
                list
            )
        view.scenarioListRec.adapter = adapter
        return view
    }
}
