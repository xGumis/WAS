package com.polarlooptheory.was.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.materialdrawer.DrawerBuilder
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.model.equipment.mWeapon
import com.polarlooptheory.was.views.adapters.VehicleListAdapter
import com.polarlooptheory.was.views.adapters.WeaponListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*

class WeaponListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: WeaponListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val weaponList = listOf(
            mWeapon("asd","dsa","asdsad"),
            mWeapon("asd","dsa","asdsad"),
            mWeapon("asd","dsa","asdsad")
        )
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter = WeaponListAdapter(weaponList)
        view.scenarioListRec.adapter = adapter
        return view
    }
}