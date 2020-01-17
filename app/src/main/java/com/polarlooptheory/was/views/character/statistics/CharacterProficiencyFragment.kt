package com.polarlooptheory.was.views.character.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.character.statistics.CharacterHealthFragment
import com.polarlooptheory.was.views.character.statistics.CharacterMagicFragment
import com.polarlooptheory.was.views.lists.CharacterListFragment
import com.polarlooptheory.was.views.lists.NoteListFragment
import kotlinx.android.synthetic.main.char_background.*
import kotlinx.android.synthetic.main.char_background.view.*
import kotlinx.android.synthetic.main.char_proficiencies.*
import kotlinx.android.synthetic.main.char_proficiencies.view.*
import kotlinx.android.synthetic.main.char_proficiencies.view.buttonBack
import kotlinx.android.synthetic.main.char_proficiencies.view.buttonNext
import kotlinx.android.synthetic.main.char_proficiencies.view.buttonSave2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterProficiencyFragment(private val char: mCharacter?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Scenario.dummyCharacter == null) Scenario.dummyCharacter = mCharacter()
        val view = inflater.inflate(R.layout.char_proficiencies, container, false)
        if(char!=null){
            formProficiency.setText(char.proficiency)
            formPassivePerception.setText(char.passivePerception)
            formPassiveInsight.setText(char.passiveInsight)
            formInspiration.setText(char.inspiration)
        }
        view.buttonSave2.setOnClickListener {
            Scenario.dummyCharacter?.proficiency = view.formProficiency.text.toString().toInt()
            Scenario.dummyCharacter?.passivePerception = view.formPassivePerception.text.toString().toInt()
            Scenario.dummyCharacter?.passiveInsight = view.formPassiveInsight.text.toString().toInt()
            Scenario.dummyCharacter?.inspiration = view.formInspiration.text.toString().toInt()
            val character = Scenario.dummyCharacter
            if(character?.name.isNullOrEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (char) {
                        null -> async {
                            Scenario.createCharacter(
                                Scenario.connectedScenario.scenario,
                                character!!.name,
                                character.alignment,
                                character.attributes,
                                character.background,
                                character.experience,
                                character.health,
                                character.hitDices,
                                character.initiative,
                                character.inspiration,
                                character.level,
                                character.passiveInsight,
                                character.passivePerception,
                                character.profession,
                                character.proficiency,
                                character.race,
                                character.speed
                            )
                            Scenario.patchCharacterEquipment(Scenario.connectedScenario.scenario,
                                character.name,
                                character.equipment.armorClass,
                                character.equipment.armors,
                                character.equipment.attacks,
                                character.equipment.currency.cp,
                                character.equipment.currency.sp,
                                character.equipment.currency.ep,
                                character.equipment.currency.gp,
                                character.equipment.currency.pp,
                                character.equipment.gear,
                                character.equipment.tools,
                                character.equipment.vehicles,
                                character.equipment.weapons
                            )
                            Scenario.patchCharacterSpells(
                                Scenario.connectedScenario.scenario,
                                character.name,
                                character.spells.baseStat,
                                character.spells.spellAttackBonus,
                                character.spells.spellSaveDc,
                                character.spells.spellSlots,
                                character.spells.spells
                            )
                        }.await()
                        else -> async {
                            Scenario.patchCharacter(
                                Scenario.connectedScenario.scenario,
                                character!!.name,
                                character.alignment,
                                character.attributes,
                                character.background,
                                character.experience,
                                character.health,
                                character.hitDices,
                                character.initiative,
                                character.inspiration,
                                character.level,
                                character.passiveInsight,
                                character.passivePerception,
                                character.profession,
                                character.proficiency,
                                character.race,
                                character.speed
                            )
                            Scenario.patchCharacterEquipment(Scenario.connectedScenario.scenario,
                                character.name,
                                character.equipment.armorClass,
                                character.equipment.armors,
                                character.equipment.attacks,
                                character.equipment.currency.cp,
                                character.equipment.currency.sp,
                                character.equipment.currency.ep,
                                character.equipment.currency.gp,
                                character.equipment.currency.pp,
                                character.equipment.gear,
                                character.equipment.tools,
                                character.equipment.vehicles,
                                character.equipment.weapons
                            )
                            Scenario.patchCharacterSpells(
                                Scenario.connectedScenario.scenario,
                                character.name,
                                character.spells.baseStat,
                                character.spells.spellAttackBonus,
                                character.spells.spellSaveDc,
                                character.spells.spellSlots,
                                character.spells.spells
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CharacterListFragment(),
                        false
                    )
                    else {
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
            }
        }
        view.buttonBack.setOnClickListener {
            Scenario.dummyCharacter?.proficiency = view.formProficiency.text.toString().toInt()
            Scenario.dummyCharacter?.passivePerception = view.formPassivePerception.text.toString().toInt()
            Scenario.dummyCharacter?.passiveInsight = view.formPassiveInsight.text.toString().toInt()
            Scenario.dummyCharacter?.inspiration = view.formInspiration.text.toString().toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterHealthFragment(char), false)
            }
        }
        view.buttonNext.setOnClickListener {
            Scenario.dummyCharacter?.proficiency = view.formProficiency.text.toString().toInt()
            Scenario.dummyCharacter?.passivePerception = view.formPassivePerception.text.toString().toInt()
            Scenario.dummyCharacter?.passiveInsight = view.formPassiveInsight.text.toString().toInt()
            Scenario.dummyCharacter?.inspiration = view.formInspiration.text.toString().toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterMagicFragment(char), false)
            }
        }
        return view
    }
}