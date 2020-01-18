package com.polarlooptheory.was.views.custom.eq

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.polarlooptheory.was.apiCalls.Types
import com.polarlooptheory.was.model.equipment.mWeapon
import com.polarlooptheory.was.views.lists.custom.equipment.CustomArmorsListFragment
import com.polarlooptheory.was.views.lists.custom.equipment.CustomWeaponsListFragment
import kotlinx.android.synthetic.main.custom_armor.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.buttonSubmit
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.customDescription
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.customName
import kotlinx.android.synthetic.main.custom_gear_vehicles.view.*
import kotlinx.android.synthetic.main.custom_spell.view.*
import kotlinx.android.synthetic.main.custom_weapon.view.*
import kotlinx.android.synthetic.main.custom_weapon.view.customCost
import kotlinx.android.synthetic.main.custom_weapon.view.customVisible
import kotlinx.android.synthetic.main.custom_weapon.view.customWeight
import kotlinx.android.synthetic.main.description_weapon.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomWeaponFragment(private val weapon: mWeapon?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_weapon, container, false)
        if(weapon!=null){
            view.customName3.setText(weapon.name)
            if (!isNew) {view.customName3.inputType = InputType.TYPE_NULL; view.customName3.isFocusable = false}
            view.customCategory.setText(weapon.category)
            view.customDmgType.setText(weapon.damageType)
            view.customDmgDice.setText(weapon.damageDice)
            view.customDmgBonus.setText(weapon.damageBonus)
            view.customCost.setText(weapon.cost)
            view.customWeaponReach.setText(weapon.weaponRange)
            view.customNormalRange.setText(weapon.normalRange)
            view.customLongRange.setText(weapon.longRange)
            view.customNormalThrowingRange.setText(weapon.normalThrowRange)
            view.customLongThrowingRange.setText(weapon.longThrowRange)
            view.customWeight.setText(weapon.weight)
            view.customVisible.isChecked = weapon.custom
        }
        view.customDmgType.setOnClickListener {
            val list: MutableList<String> = mutableListOf()
            GlobalScope.launch(Dispatchers.Main) {
                val req =
                    async { Types.getDamageTypes(Scenario.connectedScenario.scenario) }.await()
                if(!req.isNullOrEmpty()){
                    req.forEach{
                        list.add(it.name)
                    }
                }
            }
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose a damage type")
            builder.setSingleChoiceItems(list.toTypedArray(), -1){
                    dialog: DialogInterface?, i: Int ->
                view.customDmgType.setText(list[i])
                dialog?.dismiss()
            }
            builder.setNeutralButton("Cancel"){
                    dialog, _ -> dialog.cancel()
            }
            val dialog = builder.create()
            dialog.show()
        }


        view.buttonSubmit.setOnClickListener {
            if (!view.customName3.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (weapon) {
                        null -> async {
                            Equipment.createWeapon(
                                Scenario.connectedScenario.scenario,
                                view.customName3.text.toString(),
                                view.customDmgType.text.toString(),
                                view.customCategory.text.toString(),
                                view.customCost.text.toString(),
                                view.customDmgBonus.text.toString().toInt(),
                                view.customDmgDice.text.toString(),
                                view.customLongRange.text.toString().toInt(),
                                view.customLongThrowingRange.text.toString().toInt(),
                                view.customNormalRange.text.toString().toInt(),
                                view.customNormalThrowingRange.text.toString().toInt(),
                                null,
                                view.customWeaponReach.text.toString(),
                                view.customWeight.text.toString().toInt(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Equipment.patchWeapon(
                                Scenario.connectedScenario.scenario,
                                view.customName3.text.toString(),
                                view.customDmgType.text.toString(),
                                view.customCategory.text.toString(),
                                view.customCost.text.toString(),
                                view.customDmgBonus.text.toString().toInt(),
                                view.customDmgDice.text.toString(),
                                view.customLongRange.text.toString().toInt(),
                                view.customLongThrowingRange.text.toString().toInt(),
                                view.customNormalRange.text.toString().toInt(),
                                view.customNormalThrowingRange.text.toString().toInt(),
                                null,
                                view.customWeaponReach.text.toString(),
                                view.customWeight.text.toString().toInt(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomWeaponsListFragment(),
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
