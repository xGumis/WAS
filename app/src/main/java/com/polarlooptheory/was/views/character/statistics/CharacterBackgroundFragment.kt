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
import com.polarlooptheory.was.views.lists.CharacterListFragment
import com.polarlooptheory.was.views.lists.NoteListFragment
import kotlinx.android.synthetic.main.char_background.*
import kotlinx.android.synthetic.main.char_background.view.*
import kotlinx.android.synthetic.main.note_edit.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterBackgroundFragment(private val char: mCharacter?, private val isNew : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(Scenario.dummyCharacter==null) Scenario.dummyCharacter = mCharacter()
        val view = inflater.inflate(R.layout.char_background, container, false)
        if(char!=null){
            view.formProfession.setText(char.profession)
            view.formBackground.setText(char.background)
            view.formLevel.setText(char.level.toString())
            view.formExperience.setText(char.experience.toString())
        }
        view.buttonSave2.setOnClickListener {
            Scenario.dummyCharacter?.profession = view.formProfession.text.toString()
            Scenario.dummyCharacter?.background = view.formBackground.text.toString()
            val lvl = view.formLevel.text.toString()
            if(lvl.isNotBlank() && lvl.isDigitsOnly())
                Scenario.dummyCharacter?.level = lvl.toInt()
            val exp = view.formBackground.text.toString()
            if(exp.isNotBlank() && exp.isDigitsOnly())
                Scenario.dummyCharacter?.experience = exp.toInt()
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
                    if (req&&req1&&req2) (parentFragment as NavigationHost).navigateTo(
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
            GlobalScope.launch {
                Scenario.dummyCharacter?.profession = view.formProfession.text.toString()
                Scenario.dummyCharacter?.background = view.formBackground.text.toString()
                val lvl = view.formLevel.text.toString()
                if(lvl.isNotBlank() && lvl.isDigitsOnly())
                    Scenario.dummyCharacter?.level = lvl.toInt()
                val exp = view.formBackground.text.toString()
                if(exp.isNotBlank() && exp.isDigitsOnly())
                    Scenario.dummyCharacter?.experience = exp.toInt()
                (parentFragment as NavigationHost).navigateTo(CharacterBaseInfoFragment(char,isNew), false)
            }
        }
        view.buttonNext.setOnClickListener {
            GlobalScope.launch {
                Scenario.dummyCharacter?.profession = view.formProfession.text.toString()
                Scenario.dummyCharacter?.background = view.formBackground.text.toString()
                val lvl = view.formLevel.text.toString()
                if(lvl.isNotBlank() && lvl.isDigitsOnly())
                    Scenario.dummyCharacter?.level = lvl.toInt()
                val exp = view.formBackground.text.toString()
                if(exp.isNotBlank() && exp.isDigitsOnly())
                    Scenario.dummyCharacter?.experience = exp.toInt()
                (parentFragment as NavigationHost).navigateTo(CharacterStatsFragment(char,isNew), false)
            }
        }
        return view
    }
}