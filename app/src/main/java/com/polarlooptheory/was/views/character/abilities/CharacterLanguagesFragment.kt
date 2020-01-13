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
import com.polarlooptheory.was.model.abilities.mLanguage
import com.polarlooptheory.was.views.adapters.abilities.character.CharacterLanguagesAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CharacterLanguagesFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CharacterLanguagesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val list: MutableList<mLanguage> = mutableListOf()
        Scenario.connectedScenario.chosenCharacter?.abilities?.languages?.forEach {
            GlobalScope.launch(Dispatchers.Main) {
                val req =
                    async { Abilities.getLanguages(Scenario.connectedScenario.scenario, it) }.await()
                if(!req.isNullOrEmpty()) list.addAll(req)
            }
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            CharacterLanguagesAdapter(
                list
            )
        view.scenarioListRec.adapter = adapter
        return view
    }
}
