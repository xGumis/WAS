package com.polarlooptheory.was.views.custom.eq

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.apiCalls.Scenario.loadedResources.gear
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import com.polarlooptheory.was.views.lists.custom.equipment.CustomGearListFragment
import com.polarlooptheory.was.views.lists.custom.equipment.CustomVehicleListFragment
import kotlinx.android.synthetic.main.custom_armor.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.*
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.buttonSubmit
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customCost
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customDescription
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customName
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customVisible
import kotlinx.android.synthetic.main.note_edit.view.*
import kotlinx.android.synthetic.main.roll.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.custom_armor.view.customName as customName1


class CustomVehicleFragment(private val vehicle: mVehicle?, private val isNew: Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_gear_vehicles, container, false)
        if(vehicle!=null){
            view.customName.setText(vehicle.name)
            if (!isNew) {view.customName.inputType = InputType.TYPE_NULL; view.customName.isFocusable = false}
            view.customCost.setText(vehicle.cost)
            view.customWeight.setText(vehicle.weight.toString())
            view.customDescription.setText(vehicle.description)
            view.customVisible.isChecked = vehicle.custom
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (vehicle) {
                        null -> async {
                            Equipment.createVehicle(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight.text.toString().toIntOrNull(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Equipment.patchVehicle(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight.text.toString().toIntOrNull(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomVehicleListFragment(),
                        false
                    )
                    else {
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
            }else
                (activity as MainActivity).makeToast("Name cannot be empty")
        }
        return view
    }



}
