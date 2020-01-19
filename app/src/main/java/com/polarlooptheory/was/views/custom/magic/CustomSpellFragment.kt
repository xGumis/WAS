package com.polarlooptheory.was.views.custom.magic

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
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.apiCalls.Types
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.views.lists.custom.magic.CustomMagicSchoolsListFragment
import com.polarlooptheory.was.views.lists.custom.magic.CustomSpellsListFragment
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.*
import kotlinx.android.synthetic.main.custom_feat_trait_magicschool.view.buttonSubmit
import kotlinx.android.synthetic.main.custom_spell.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.custom_spell.view.customDescription as customDescription1
import kotlinx.android.synthetic.main.custom_spell.view.customName as customName1


class CustomSpellFragment(private val spell: mSpell?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_spell, container, false)
        if(spell!=null){
            view.customName.setText(spell.name)
            if (!isNew) {view.customName.inputType = InputType.TYPE_NULL; view.customName.isFocusable = false}
            view.customLevel.setText(spell.level)
            view.customMagicSchool.setText(spell.magicSchool)
            view.customRange.setText(spell.range)
            view.customDuration.setText(spell.duration)
            view.customCastingTime.setText(spell.castingTime)
            view.customComponents.setText(spell.components)
            view.customMaterial.setText(spell.material)
            view.customHigherLvL.setText(spell.higherLevels)
            view.customDescription.setText(spell.description)
            view.customConcetration.isChecked = spell.concentration
            view.customRitual.isChecked = spell.ritual
            view.customVisible.isChecked = spell.visible
        }
        view.customMagicSchool.setOnClickListener {
            val list: MutableList<String> = mutableListOf()
            GlobalScope.launch(Dispatchers.Main) {
                val req =
                    async { Types.getMagicSchools(Scenario.connectedScenario.scenario) }.await()
                if(!req.isNullOrEmpty()){
                    req.forEach{
                        list.add(it.name)
                    }
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose a magic school")
                    builder.setSingleChoiceItems(list.toTypedArray(), -1){
                            dialog: DialogInterface?, i: Int ->
                        view.customMagicSchool.setText(list[i])
                        dialog?.dismiss()
                    }
                    builder.setNeutralButton("Cancel"){
                            dialog, _ -> dialog.cancel()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (spell) {
                        null -> async {
                            Abilities.createSpell(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customMagicSchool.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCastingTime.text.toString(),
                                view.customComponents.text.toString(),
                                view.customConcetration.isChecked,
                                view.customDuration.text.toString(),
                                view.customHigherLvL.text.toString(),
                                view.customLevel.text.toString().toInt(),
                                view.customMaterial.text.toString(),
                                view.customRange.text.toString(),
                                view.customRitual.isChecked,
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Abilities.patchSpell(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customMagicSchool.text.toString(),
                                view.customDescription.text.toString(),
                                view.customCastingTime.text.toString(),
                                view.customComponents.text.toString(),
                                view.customConcetration.isChecked,
                                view.customDuration.text.toString(),
                                view.customHigherLvL.text.toString(),
                                view.customLevel.text.toString().toInt(),
                                view.customMaterial.text.toString(),
                                view.customRange.text.toString(),
                                view.customRitual.isChecked,
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomSpellsListFragment(),
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
