package com.polarlooptheory.was.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Scenario
import kotlinx.android.synthetic.main.roll.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class RollFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.roll, container, false)
        view.buttonRoll.setOnClickListener {
            GlobalScope.launch {
                    val character = Scenario.connectedScenario.chosenCharacter
                    val number = view.editText2.text.toString().toInt()
                    val sides = view.editText3.text.toString().toInt()
                    val vis = view.switchPublic.isChecked
                if(character != null){
                    val req = async{ Scenario.rollDice(Scenario.connectedScenario.scenario,character,number,sides,vis)}.await()
                    if(req.isNullOrEmpty()){
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
                else {
                    (activity as MainActivity).makeToast("Must choose a character")
                }
            }
        }
        return view
    }


}
