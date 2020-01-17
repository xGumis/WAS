package com.polarlooptheory.was.views.character.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mArmor
import com.polarlooptheory.was.views.adapters.eq.character.ArmorListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ArmorListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ArmorListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val list: MutableList<mArmor> = mutableListOf()
        Scenario.connectedScenario.chosenCharacter?.equipment?.armors?.forEach {
            GlobalScope.launch(Dispatchers.Main) {
                val req =
                    async { Equipment.getArmors(Scenario.connectedScenario.scenario, it.first) }.await()
                if(!req.isNullOrEmpty()) list.addAll(req)
            }
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            ArmorListAdapter(
                list
            )
        view.scenarioListRec.adapter = adapter
        return view
    }
}