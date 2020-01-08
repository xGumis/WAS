package com.polarlooptheory.was.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mArmor
import com.polarlooptheory.was.views.adapters.ArmorListAdapter
import kotlinx.android.synthetic.main.scenario_list.view.*

class ArmorListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ArmorListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        val armorList = listOf(
            mArmor("asd"),
            mArmor("asd"),
            mArmor("asd")
        )
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter = ArmorListAdapter(armorList)
        view.scenarioListRec.adapter = adapter
        return view
    }
}