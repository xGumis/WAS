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
import com.polarlooptheory.was.model.abilities.mTrait
import com.polarlooptheory.was.views.lists.custom.abilities.CustomTraitsListFragment
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomTraitFragment(private val trait: mTrait?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_feat_trait_magicschool, container, false)
        if(trait!=null){
            view.customName.setText(trait.name)
            if (!isNew) {view.customName.inputType = InputType.TYPE_NULL; view.customName.isFocusable = false}
            view.customDescription.setText(trait.description)
            view.switchVisible.isChecked = trait.visible
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (trait) {
                        null -> async {
                            Abilities.createTrait(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Abilities.patchTrait(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomTraitsListFragment(),
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
