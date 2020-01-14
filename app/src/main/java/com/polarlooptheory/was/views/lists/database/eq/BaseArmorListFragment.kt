package com.polarlooptheory.was.views.lists.database.eq

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
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mArmor
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.views.adapters.eq.database.BaseArmorListAdapter
import com.polarlooptheory.was.views.adapters.eq.database.BaseVehicleListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class BaseArmorListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BaseArmorListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val list: MutableList<mArmor> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Equipment.getArmors(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()) list.addAll(req)
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            BaseArmorListAdapter(
                list
            )
        view.scenarioListRec.adapter = adapter
        return view
    }
}
