package com.polarlooptheory.was.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.adapters.app.PlayerListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PlayerListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PlayerListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            val req = async{ Scenario.getPlayers(Scenario.connectedScenario.scenario)}.await()
            if(req) adapter.notifyDataSetChanged()
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter = PlayerListAdapter()
        view.scenarioListRec.adapter = adapter
        return view
    }
}