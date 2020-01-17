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
import com.polarlooptheory.was.views.character.statistics.CharacterBackgroundFragment
import com.polarlooptheory.was.views.character.statistics.CharacterHealthFragment
import com.polarlooptheory.was.views.lists.CharacterListFragment
import com.polarlooptheory.was.views.lists.NoteListFragment
import kotlinx.android.synthetic.main.char_proficiencies.*
import kotlinx.android.synthetic.main.char_proficiencies.view.*
import kotlinx.android.synthetic.main.char_stats.*
import kotlinx.android.synthetic.main.char_stats.view.*
import kotlinx.android.synthetic.main.char_stats.view.buttonBack
import kotlinx.android.synthetic.main.char_stats.view.buttonNext
import kotlinx.android.synthetic.main.char_stats.view.buttonSave2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterStatsFragment(private val char: mCharacter?, private val isNew : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Scenario.dummyCharacter == null) Scenario.dummyCharacter = mCharacter()
        val view = inflater.inflate(R.layout.char_stats, container, false)
        if(char!=null){
            view.formStrength.setText(char.attributes.strength.toString())
            view.formDexterity.setText(char.attributes.dexterity.toString())
            view.formConstitution.setText(char.attributes.constitution.toString())
            view.formWisdom.setText(char.attributes.wisdom.toString())
            view.formIntelligence.setText(char.attributes.intelligence.toString())
            view.formCharisma.setText(char.attributes.charisma.toString())
        }
        view.buttonSave2.setOnClickListener {
            val str = view.formStrength.text.toString()
            if(str.isNotBlank() && str.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.strength = str.toInt()
            val dex = view.formDexterity.text.toString()
            if(dex.isNotBlank() && dex.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.dexterity = dex.toInt()
            val con = view.formConstitution.text.toString()
            if(con.isNotBlank() && con.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.constitution = con.toInt()
            val wis = view.formWisdom.text.toString()
            if(wis.isNotBlank() && wis.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.wisdom = wis.toInt()
            val int = view.formIntelligence.text.toString()
            if(int.isNotBlank() && int.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.intelligence = int.toInt()
            val chr = view.formCharisma.text.toString()
            if(chr.isNotBlank() && chr.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.charisma = chr.toInt()
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
        view.buttonNext.setOnClickListener {
            val str = view.formStrength.text.toString()
            if(str.isNotBlank() && str.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.strength = str.toInt()
            val dex = view.formDexterity.text.toString()
            if(dex.isNotBlank() && dex.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.dexterity = dex.toInt()
            val con = view.formConstitution.text.toString()
            if(con.isNotBlank() && con.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.constitution = con.toInt()
            val wis = view.formWisdom.text.toString()
            if(wis.isNotBlank() && wis.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.wisdom = wis.toInt()
            val int = view.formIntelligence.text.toString()
            if(int.isNotBlank() && int.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.intelligence = int.toInt()
            val chr = view.formCharisma.text.toString()
            if(chr.isNotBlank() && chr.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.charisma = chr.toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterHealthFragment(char,isNew), false)
            }
        }
        view.buttonBack.setOnClickListener {
            val str = view.formStrength.text.toString()
            if(str.isNotBlank() && str.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.strength = str.toInt()
            val dex = view.formDexterity.text.toString()
            if(dex.isNotBlank() && dex.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.dexterity = dex.toInt()
            val con = view.formConstitution.text.toString()
            if(con.isNotBlank() && con.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.constitution = con.toInt()
            val wis = view.formWisdom.text.toString()
            if(wis.isNotBlank() && wis.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.wisdom = wis.toInt()
            val int = view.formIntelligence.text.toString()
            if(int.isNotBlank() && int.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.intelligence = int.toInt()
            val chr = view.formCharisma.text.toString()
            if(chr.isNotBlank() && chr.isDigitsOnly())
                Scenario.dummyCharacter?.attributes?.charisma = chr.toInt()
            GlobalScope.launch {
                (parentFragment as NavigationHost).navigateTo(CharacterBackgroundFragment(char,isNew), false)
            }
        }
        return view
    }
}