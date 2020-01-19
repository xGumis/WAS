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

class CharacterHealthFragment(private val isNew : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Scenario.dummyCharacter == null) Scenario.dummyCharacter = mCharacter()
        val view = inflater.inflate(R.layout.char_health, container, false)
        val char = Scenario.dummyCharacter
        if (char != null) {
            view.formMaxHP.setText(char.health.maxHealth.toString())
            view.formCurrentHP.setText(char.health.actualHealth.toString())
            view.formTemporaryHP.setText(char.health.temporaryHealth.toString())
            view.formHitDiceType.setText(char.hitDices.dice)
            view.formTotalHitDice.setText(char.hitDices.total.toString())
            view.formUsedHitDice.setText(char.hitDices.used.toString())
        }
        view.buttonSave2.setOnClickListener {
            val mh = view.formMaxHP.text.toString()
            if(mh.isNotBlank() && mh.isDigitsOnly())
                Scenario.dummyCharacter?.health?.maxHealth = mh.toInt()
            val ah = view.formCurrentHP.text.toString()
            if(ah.isNotBlank() && ah.isDigitsOnly())
                Scenario.dummyCharacter?.health?.actualHealth = ah.toInt()
            val h = view.formTemporaryHP.text.toString()
            if(h.isNotBlank() && h.isDigitsOnly())
                Scenario.dummyCharacter?.health?.temporaryHealth = h.toInt()
            Scenario.dummyCharacter?.hitDices?.dice = view.formHitDiceType.text.toString()
            val tot = view.formTotalHitDice.text.toString()
            if(tot.isNotBlank() && tot.isDigitsOnly())
                Scenario.dummyCharacter?.hitDices?.total = tot.toInt()
            val use = view.formUsedHitDice.text.toString()
            if(use.isNotBlank() && use.isDigitsOnly())
                Scenario.dummyCharacter?.hitDices?.used = use.toInt()
            val character = Scenario.dummyCharacter
            if (!character?.name.isNullOrEmpty()) {
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
        view.buttonNext.setOnClickListener {
            val mh = view.formMaxHP.text.toString()
            if(mh.isNotBlank() && mh.isDigitsOnly())
                Scenario.dummyCharacter?.health?.maxHealth = mh.toInt()
            val ah = view.formCurrentHP.text.toString()
            if(ah.isNotBlank() && ah.isDigitsOnly())
                Scenario.dummyCharacter?.health?.actualHealth = ah.toInt()
            val h = view.formTemporaryHP.text.toString()
            if(h.isNotBlank() && h.isDigitsOnly())
                Scenario.dummyCharacter?.health?.temporaryHealth = h.toInt()
            Scenario.dummyCharacter?.hitDices?.dice = view.formHitDiceType.text.toString()
            val tot = view.formTotalHitDice.text.toString()
            if(tot.isNotBlank() && tot.isDigitsOnly())
                Scenario.dummyCharacter?.hitDices?.total = tot.toInt()
            val use = view.formUsedHitDice.text.toString()
            if(use.isNotBlank() && use.isDigitsOnly())
                Scenario.dummyCharacter?.hitDices?.used = use.toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterProficiencyFragment(isNew), false)
            }
        }
        view.buttonBack.setOnClickListener {
            val mh = view.formMaxHP.text.toString()
            if(mh.isNotBlank() && mh.isDigitsOnly())
                Scenario.dummyCharacter?.health?.maxHealth = mh.toInt()
            val ah = view.formCurrentHP.text.toString()
            if(ah.isNotBlank() && ah.isDigitsOnly())
                Scenario.dummyCharacter?.health?.actualHealth = ah.toInt()
            val h = view.formTemporaryHP.text.toString()
            if(h.isNotBlank() && h.isDigitsOnly())
                Scenario.dummyCharacter?.health?.temporaryHealth = h.toInt()
            Scenario.dummyCharacter?.hitDices?.dice = view.formHitDiceType.text.toString()
            val tot = view.formTotalHitDice.text.toString()
            if(tot.isNotBlank() && tot.isDigitsOnly())
                Scenario.dummyCharacter?.hitDices?.total = tot.toInt()
            val use = view.formUsedHitDice.text.toString()
            if(use.isNotBlank() && use.isDigitsOnly())
                Scenario.dummyCharacter?.hitDices?.used = use.toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterStatsFragment(isNew), false)
            }
        }
        return view
    }
}