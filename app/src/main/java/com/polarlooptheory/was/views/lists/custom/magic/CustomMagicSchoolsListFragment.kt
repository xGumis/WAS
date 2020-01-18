package com.polarlooptheory.was.views.lists.custom.magic

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.apiCalls.Types
import com.polarlooptheory.was.model.equipment.mArmor
import com.polarlooptheory.was.model.types.mMagicSchool
import com.polarlooptheory.was.views.adapters.abilities.custom.CustomMagicSchoolListAdapter
import com.polarlooptheory.was.views.adapters.eq.custom.CustomArmorListAdapter
import com.polarlooptheory.was.views.custom.eq.CustomArmorFragment
import com.polarlooptheory.was.views.custom.magic.CustomMagicSchoolFragment
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomMagicSchoolsListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomMagicSchoolListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.text = "ADD MAGIC SCHOOL"
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(CustomMagicSchoolFragment(null, true),false)
        }
        val list: MutableList<mMagicSchool> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req =
                async { Types.getMagicSchools(Scenario.connectedScenario.scenario) }.await()
            if(!req.isNullOrEmpty()){
                req.forEach{
                    if(it.custom) list.add(it)
                }
            }        }
        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            CustomMagicSchoolListAdapter(
                list
            )
        view.char_list.adapter = adapter
        return view
    }
}
