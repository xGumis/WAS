package com.polarlooptheory.was.views.lists.custom.abilities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mFeature
import com.polarlooptheory.was.model.abilities.mLanguage
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.adapters.abilities.custom.CustomLanguagesAdapter
import com.polarlooptheory.was.views.adapters.app.CharacterListAdapter
import com.polarlooptheory.was.views.character.statistics.CharacterBaseInfoFragment
import com.polarlooptheory.was.views.custom.abilities.CustomLanguageFragment
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomLanguageListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomLanguagesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.text = "ADD LANGUAGE"
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(CustomLanguageFragment(null, true),false)
        }
        val list: MutableList<mLanguage> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Abilities.getLanguages(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()){
                req.forEach{
                    if(it.custom) list.add(it)
                }
            }        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            CustomLanguagesAdapter(
                list
            )
        view.char_list.adapter = adapter
        return view
    }
}
