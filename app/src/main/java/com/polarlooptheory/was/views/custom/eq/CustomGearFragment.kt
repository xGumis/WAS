package com.polarlooptheory.was.views.custom.eq

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mGear
import com.polarlooptheory.was.views.adapters.eq.custom.CustomGearListAdapter
import com.polarlooptheory.was.views.lists.custom.equipment.CustomArmorsListFragment
import com.polarlooptheory.was.views.lists.custom.equipment.CustomGearListFragment
import kotlinx.android.synthetic.main.custom_armor.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.buttonSubmit
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.*
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customCost
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.customDescription as customDescription1
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.customName as customName1


class CustomGearFragment(private val gear: mGear?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_gear_vehicles, container, false)
        if(gear!=null){
            view.customName1.setText(gear.name)
            if (!isNew) {view.customName1.inputType = InputType.TYPE_NULL; view.customName1.isFocusable = false}
            view.customCost.setText(gear.cost)
            view.customWeight.setText(gear.weight)
            view.customDescription.setText(gear.description)
            view.customVisible.isChecked = gear.custom
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName1.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (gear) {
                        null -> async {
                            Equipment.createGear(
                                Scenario.connectedScenario.scenario,
                                view.customName1.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight.text.toString().toInt(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Equipment.patchGear(
                                Scenario.connectedScenario.scenario,
                                view.customName1.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight.text.toString().toInt(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomGearListFragment(),
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
