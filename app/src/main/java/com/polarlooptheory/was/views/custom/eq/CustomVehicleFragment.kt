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
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.views.lists.custom.equipment.CustomVehicleListFragment
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


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
            view.customWeight4.setText(vehicle.weight.toString())
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
                                view.customWeight4.text.toString().toIntOrNull(),
                                view.customVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Equipment.patchVehicle(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight4.text.toString().toIntOrNull(),
                                view.customVisible.isChecked
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
