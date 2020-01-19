package com.polarlooptheory.was.views.lists.custom.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mTool
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.views.custom.eq.CustomVehicleFragment
import com.polarlooptheory.was.views.adapters.eq.custom.CustomVehicleListAdapter
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CustomVehicleListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomVehicleListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.text = "ADD VEHICLE"
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(CustomVehicleFragment(null,true),false)
        }
        val list: MutableList<mVehicle> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Equipment.getVehicles(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()){
                req.forEach{
                    if(it.custom) {list.add(it);adapter.notifyDataSetChanged()}
                }
            }
        }

        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            CustomVehicleListAdapter(
                list
            )
        view.char_list.adapter = adapter
        return view
    }
}