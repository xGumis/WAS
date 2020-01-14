package com.polarlooptheory.was.views.lists.database.abilities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mFeature
import com.polarlooptheory.was.model.abilities.mProficiency
import com.polarlooptheory.was.views.adapters.abilities.database.BaseFeaturesAdapter
import com.polarlooptheory.was.views.adapters.abilities.database.BaseProficienciesAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class BaseProficienciesFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BaseProficienciesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val list: MutableList<mProficiency> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Abilities.getProficiencies(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()) list.addAll(req)
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            BaseProficienciesAdapter(list)
        view.scenarioListRec.adapter = adapter
        return view
    }
}