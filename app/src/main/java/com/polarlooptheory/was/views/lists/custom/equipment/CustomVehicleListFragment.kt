package com.polarlooptheory.was.views.lists.custom.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.views.CustomVehicleFragment
import com.polarlooptheory.was.views.adapters.CustomVehicleListAdapter
import kotlinx.android.synthetic.main.characters.view.*

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
            (parentFragment as NavigationHost).navigateTo(CustomVehicleFragment(),false)
        }
        val vehicleList = listOf(
            mVehicle("Niestandardowy pojazd","Jeździ bardzo szybko","20sz",3,true,true)
        )
        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter = CustomVehicleListAdapter(vehicleList)
        view.char_list.adapter = adapter
        return view
    }
}