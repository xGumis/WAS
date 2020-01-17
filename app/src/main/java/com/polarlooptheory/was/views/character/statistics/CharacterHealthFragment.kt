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
import com.polarlooptheory.was.views.lists.CharacterListFragment
import com.polarlooptheory.was.views.lists.NoteListFragment
import kotlinx.android.synthetic.main.char_background.view.*
import kotlinx.android.synthetic.main.char_base_info.*
import kotlinx.android.synthetic.main.char_base_info.view.*
import kotlinx.android.synthetic.main.char_health.*
import kotlinx.android.synthetic.main.char_health.view.*
import kotlinx.android.synthetic.main.char_health.view.buttonBack
import kotlinx.android.synthetic.main.char_health.view.buttonNext
import kotlinx.android.synthetic.main.char_health.view.buttonSave2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterHealthFragment(private val char: mCharacter?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Scenario.dummyCharacter == null) Scenario.dummyCharacter = mCharacter()
        val view = inflater.inflate(R.layout.char_health, container, false)
        if (char != null) {
            formMaxHP.setText(char.health.maxHealth)
            formCurrentHP.setText(char.health.actualHealth)
            formTemporaryHP.setText(char.health.temporaryHealth)
            formHitDiceType.setText(char.hitDices.dice)
            formTotalHitDice.setText(char.hitDices.total)
            formUsedHitDice.setText(char.hitDices.used)
        }
        view.buttonSave2.setOnClickListener {
            Scenario.dummyCharacter?.health?.maxHealth = view.formMaxHP.text.toString().toInt()
            Scenario.dummyCharacter?.health?.actualHealth = view.formCurrentHP.text.toString().toInt()
            Scenario.dummyCharacter?.health?.temporaryHealth = view.formTemporaryHP.text.toString().toInt()
            Scenario.dummyCharacter?.hitDices?.dice = view.formHitDiceType.text.toString()
            Scenario.dummyCharacter?.hitDices?.total = view.formTotalHitDice.text.toString().toInt()
            Scenario.dummyCharacter?.hitDices?.used = view.formUsedHitDice.text.toString().toInt()
            val character = Scenario.dummyCharacter
            if (character?.name.isNullOrEmpty()) {
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
                            Scenario.patchCharacterEquipment(
                                Scenario.connectedScenario.scenario,
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
                            Scenario.patchCharacterEquipment(
                                Scenario.connectedScenario.scenario,
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
        view.buttonNext.setOnClickListener {
            Scenario.dummyCharacter?.health?.maxHealth = view.formMaxHP.text.toString().toInt()
            Scenario.dummyCharacter?.health?.actualHealth = view.formCurrentHP.text.toString().toInt()
            Scenario.dummyCharacter?.health?.temporaryHealth = view.formTemporaryHP.text.toString().toInt()
            Scenario.dummyCharacter?.hitDices?.dice = view.formHitDiceType.text.toString()
            Scenario.dummyCharacter?.hitDices?.total = view.formTotalHitDice.text.toString().toInt()
            Scenario.dummyCharacter?.hitDices?.used = view.formUsedHitDice.text.toString().toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterProficiencyFragment(char), false)
            }
        }
        view.buttonBack.setOnClickListener {
            Scenario.dummyCharacter?.health?.maxHealth = view.formMaxHP.text.toString().toInt()
            Scenario.dummyCharacter?.health?.actualHealth = view.formCurrentHP.text.toString().toInt()
            Scenario.dummyCharacter?.health?.temporaryHealth = view.formTemporaryHP.text.toString().toInt()
            Scenario.dummyCharacter?.hitDices?.dice = view.formHitDiceType.text.toString()
            Scenario.dummyCharacter?.hitDices?.total = view.formTotalHitDice.text.toString().toInt()
            Scenario.dummyCharacter?.hitDices?.used = view.formUsedHitDice.text.toString().toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterStatsFragment(char), false)
            }
        }
        return view
    }
}