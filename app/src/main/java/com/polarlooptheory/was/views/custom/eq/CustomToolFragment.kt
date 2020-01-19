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
import com.polarlooptheory.was.model.equipment.mTool
import com.polarlooptheory.was.views.lists.custom.equipment.CustomToolsListFragment
import kotlinx.android.synthetic.main.custom_tool.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.customName as customName1


class CustomToolFragment(private val tool: mTool?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_tool, container, false)
        if(tool!=null){
            view.customName1.setText(tool.name)
            if (!isNew) {view.customName1.inputType = InputType.TYPE_NULL; view.customName1.isFocusable = false}
            view.customCategory.setText(tool.category)
            view.customCost.setText(tool.cost)
            view.customWeight.setText(tool.weight.toString())
            view.customDescription2.setText(tool.description)
            view.customVisible.isChecked = tool.custom
        }
        view.buttonSubmit2.setOnClickListener {
            if (!view.customName1.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (tool) {
                        null -> async {
                            Equipment.createTool(
                                Scenario.connectedScenario.scenario,
                                view.customName1.text.toString(),
                                view.customDescription2.text.toString(),
                                view.customCategory.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight.text.toString().toIntOrNull(),
                                view.customVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Equipment.patchTool(
                                Scenario.connectedScenario.scenario,
                                view.customName1.text.toString(),
                                view.customDescription2.text.toString(),
                                view.customCategory.text.toString(),
                                view.customCost.text.toString(),
                                view.customWeight.text.toString().toIntOrNull(),
                                view.customVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomToolsListFragment(),
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
