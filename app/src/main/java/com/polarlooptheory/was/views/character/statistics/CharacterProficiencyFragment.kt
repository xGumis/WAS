package com.polarlooptheory.was.views.character.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
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

class CharacterProficiencyFragment(private val char: mCharacter?, private val isNew : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Scenario.dummyCharacter == null) Scenario.dummyCharacter = mCharacter()
        val view = inflater.inflate(R.layout.char_proficiencies, container, false)
        if(char!=null){
            view.formProficiency.setText(char.proficiency.toString())
            view.formPassivePerception.setText(char.passivePerception.toString())
            view.formPassiveInsight.setText(char.passiveInsight.toString())
            view.formInspiration.setText(char.inspiration.toString())
        }
        view.buttonSave2.setOnClickListener {
            val pro = view.formProficiency.text.toString()
            if(pro.isNotBlank() && pro.isDigitsOnly())
                Scenario.dummyCharacter?.proficiency = pro.toInt()
            val pp = view.formPassivePerception.text.toString()
            if(pp.isNotBlank() && pp.isDigitsOnly())
                Scenario.dummyCharacter?.passivePerception = pp.toInt()
            val pit = view.formPassiveInsight.text.toString()
            if(pit.isNotBlank() && pit.isDigitsOnly())
                Scenario.dummyCharacter?.passiveInsight = pit.toInt()
            val pin = view.formInspiration.text.toString()
            if(pin.isNotBlank() && pin.isDigitsOnly())
                Scenario.dummyCharacter?.inspiration = pin.toInt()
            val character = Scenario.dummyCharacter
            if(!character?.name.isNullOrEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = if(isNew)
                        async {
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
                        }.await() else async {
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
                    }.await()
                    val req1 = async{
                        Scenario.patchCharacterEquipment(
                            Scenario.connectedScenario.scenario,
                            character!!.name,
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
                    }.await()
                    val req2 = async{Scenario.patchCharacterSpells(
                        Scenario.connectedScenario.scenario,
                        character!!.name,
                        character.spells.baseStat,
                        character.spells.spellAttackBonus,
                        character.spells.spellSaveDc,
                        character.spells.spellSlots,
                        character.spells.spells
                    )
                    }.await()
                    if (req&&req1&&req2)(parentFragment as NavigationHost).navigateTo(
                        CharacterListFragment(),
                        false
                    )
                    else {
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
            }else
                (activity as MainActivity).makeToast("Character name cannot be empty")
        }
        view.buttonBack.setOnClickListener {
            val pro = view.formProficiency.text.toString()
            if(pro.isNotBlank() && pro.isDigitsOnly())
                Scenario.dummyCharacter?.proficiency = pro.toInt()
            val pp = view.formPassivePerception.text.toString()
            if(pp.isNotBlank() && pp.isDigitsOnly())
                Scenario.dummyCharacter?.passivePerception = pp.toInt()
            val pit = view.formPassiveInsight.text.toString()
            if(pit.isNotBlank() && pit.isDigitsOnly())
                Scenario.dummyCharacter?.passiveInsight = pit.toInt()
            val pin = view.formInspiration.text.toString()
            if(pin.isNotBlank() && pin.isDigitsOnly())
                Scenario.dummyCharacter?.inspiration = pin.toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterHealthFragment(char,isNew), false)
            }
        }
        view.buttonNext.setOnClickListener {
            val pro = view.formProficiency.text.toString()
            if(pro.isNotBlank() && pro.isDigitsOnly())
                Scenario.dummyCharacter?.proficiency = pro.toInt()
            val pp = view.formPassivePerception.text.toString()
            if(pp.isNotBlank() && pp.isDigitsOnly())
                Scenario.dummyCharacter?.passivePerception = pp.toInt()
            val pit = view.formPassiveInsight.text.toString()
            if(pit.isNotBlank() && pit.isDigitsOnly())
                Scenario.dummyCharacter?.passiveInsight = pit.toInt()
            val pin = view.formInspiration.text.toString()
            if(pin.isNotBlank() && pin.isDigitsOnly())
                Scenario.dummyCharacter?.inspiration = pin.toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterMagicFragment(char,isNew), false)
            }
        }
        return view
    }
}