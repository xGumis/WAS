package com.polarlooptheory.was.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.adapters.app.CharacterListAdapter
import com.polarlooptheory.was.views.character.statistics.CharacterBaseInfoFragment
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CharacterListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(CharacterBaseInfoFragment(),false)
        }
        val list: MutableList<mCharacter> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req = async{Scenario.getCharacters(Scenario.connectedScenario.scenario)}.await()
            if(req) adapter.notifyDataSetChanged()
        }

        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            CharacterListAdapter(
                list
            )
        view.char_list.adapter = adapter
        return view
    }
}