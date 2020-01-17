package com.polarlooptheory.was.views.character.equipment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.lists.CharacterListFragment
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import kotlinx.android.synthetic.main.char_background.view.*
import kotlinx.android.synthetic.main.login_screen.view.*
import kotlinx.android.synthetic.main.money.*
import kotlinx.android.synthetic.main.money.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MoneyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.money, container, false)
        view.editCopper.setText(Scenario.connectedScenario.chosenCharacter!!.equipment.currency.cp.toString())
        view.editSilver.setText(Scenario.connectedScenario.chosenCharacter!!.equipment.currency.sp.toString())
        view.editGold.setText(Scenario.connectedScenario.chosenCharacter!!.equipment.currency.gp.toString())
        view.editElectrum.setText(Scenario.connectedScenario.chosenCharacter!!.equipment.currency.ep.toString())
        view.editPlatinum.setText(Scenario.connectedScenario.chosenCharacter!!.equipment.currency.pp.toString())
        Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter

        view.buttonSave.setOnClickListener {
            val character = Scenario.dummyCharacter
            character!!.equipment.currency.cp = view.editCopper.text.toString().toInt()
            character.equipment.currency.sp = view.editSilver.text.toString().toInt()
            character.equipment.currency.gp = view.editGold.text.toString().toInt()
            character.equipment.currency.ep = view.editElectrum.text.toString().toInt()
            character.equipment.currency.pp = view.editPlatinum.text.toString().toInt()
                GlobalScope.launch(Dispatchers.Main) {
                    val req =
                        async {
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
                        }.await()
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
        return view
    }



}
