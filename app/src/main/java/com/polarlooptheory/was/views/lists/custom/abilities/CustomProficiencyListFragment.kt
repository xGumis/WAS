package com.polarlooptheory.was.views.lists.custom.abilities

import android.content.Context
import android.net.Uri
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
import com.polarlooptheory.was.model.abilities.mLanguage
import com.polarlooptheory.was.model.abilities.mProficiency
import com.polarlooptheory.was.views.adapters.abilities.custom.CustomLanguagesAdapter
import com.polarlooptheory.was.views.adapters.abilities.custom.CustomProficienciesAdapter
import com.polarlooptheory.was.views.custom.abilities.CustomLanguageFragment
import com.polarlooptheory.was.views.custom.abilities.CustomProficiencyFragment
import com.polarlooptheory.was.views.custom.abilities.CustomTraitFragment
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomProficienciesListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomProficienciesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.text = "ADD PROFICIENCY"
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(CustomProficiencyFragment(null, true),false)
        }
        val list: MutableList<mProficiency> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Abilities.getProficiencies(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()){
                req.forEach{
                    if(it.custom) list.add(it)
                }
            }        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            CustomProficienciesAdapter(
                list
            )
        view.char_list.adapter = adapter
        return view
    }
}
