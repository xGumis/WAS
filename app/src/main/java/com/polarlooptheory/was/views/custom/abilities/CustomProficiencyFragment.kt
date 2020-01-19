package com.polarlooptheory.was.views.custom.abilities

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
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mProficiency
import com.polarlooptheory.was.views.lists.custom.abilities.CustomProficienciesListFragment
import kotlinx.android.synthetic.main.custom_prof.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomProficiencyFragment(private val proficiency: mProficiency?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_prof, container, false)
        if(proficiency!=null){
            view.customName.setText(proficiency.name)
            if (!isNew) {view.customName.inputType = InputType.TYPE_NULL; view.customName.isFocusable = false}
            view.customType.setText(proficiency.type)
            view.switchVisible.isChecked = proficiency.visible
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (proficiency) {
                        null -> async {
                            Abilities.createProficiency(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customType.text.toString(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Abilities.patchProficiency(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customType.text.toString(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomProficienciesListFragment(),
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
