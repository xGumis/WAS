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
import androidx.core.text.isDigitsOnly
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mArmor
import com.polarlooptheory.was.views.lists.custom.abilities.CustomProficienciesListFragment
import com.polarlooptheory.was.views.lists.custom.equipment.CustomArmorsListFragment
import kotlinx.android.synthetic.main.char_base_info.view.*
import kotlinx.android.synthetic.main.custom_armor.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.buttonSubmit
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.customName
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.switchVisible
import kotlinx.android.synthetic.main.custom_prof.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.custom_armor.view.customName as customName1


class CustomArmorFragment(private val armor: mArmor?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_armor, container, false)
        if(armor!=null){
            view.customName1.setText(armor.name)
            if (!isNew) {view.customName1.inputType = InputType.TYPE_NULL; view.customName1.isFocusable = false}
            view.customAC.setText(armor.armorClass.toString())
            view.customDexBonus.isChecked = armor.armorClass.dexBonus
            if(view.customDexBonus.isChecked) {view.customMaxDexBonus.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL; view.customMaxDexBonus.isFocusable = true; view.customMaxDexBonus.setText(armor.armorClass.maxBonus.toString())}
            else {view.customDexBonus.inputType = InputType.TYPE_NULL; view.customMaxDexBonus.isFocusable = false; view.customMaxDexBonus.setText("0")}
            view.customCost.setText(armor.cost)
            view.customMinStr.setText(armor.strMinimum.toString())
            view.customWeight.setText(armor.weight.toString())
            view.customStealthDisadvantage.isChecked = armor.stealthDisadvantage
            view.customVisible.isChecked = armor.custom
        }
        view.customDexBonus.setOnClickListener {
            if(view.customDexBonus.isChecked) {view.customMaxDexBonus.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL; view.customMaxDexBonus.isFocusable = true}
            else {view.customDexBonus.inputType = InputType.TYPE_NULL; view.customMaxDexBonus.isFocusable = false; view.customMaxDexBonus.setText("0")}
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName1.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (armor) {
                        null -> async {
                            Equipment.createArmor(
                                Scenario.connectedScenario.scenario,
                                view.customName1.text.toString(),
                                view.customAC.text.toString().toIntOrNull(),
                                view.customDexBonus.isChecked,
                                view.customMaxDexBonus.text.toString().toIntOrNull(),
                                view.customCost.text.toString(),
                                view.customStealthDisadvantage.isChecked,
                                view.customMinStr.text.toString().toIntOrNull(),
                                view.customWeight.text.toString().toIntOrNull(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Equipment.patchArmor(
                                Scenario.connectedScenario.scenario,
                                view.customName1.text.toString(),
                                view.customAC.text.toString().toIntOrNull(),
                                view.customDexBonus.isChecked,
                                view.customMaxDexBonus.text.toString().toIntOrNull(),
                                view.customCost.text.toString(),
                                view.customStealthDisadvantage.isChecked,
                                view.customMinStr.text.toString().toIntOrNull(),
                                view.customWeight.text.toString().toIntOrNull(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomArmorsListFragment(),
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
