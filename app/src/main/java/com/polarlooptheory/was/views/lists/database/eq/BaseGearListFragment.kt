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
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mGear
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.views.adapters.eq.database.BaseGearListAdapter
import com.polarlooptheory.was.views.adapters.eq.database.BaseVehicleListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class BaseGearListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BaseGearListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val list: MutableList<mGear> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Equipment.getGears(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()) {list.addAll(req);adapter.notifyDataSetChanged()}
            else{(activity as MainActivity).makeToast(Settings.error_message);Settings.error_message = ""}
        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            BaseGearListAdapter(
                list
            )
        view.scenarioListRec.adapter = adapter
        return view
    }
}
