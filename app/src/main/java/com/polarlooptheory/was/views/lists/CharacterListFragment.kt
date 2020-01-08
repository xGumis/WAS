package com.polarlooptheory.was.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.materialdrawer.DrawerBuilder
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mVehicle
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.adapters.CharacterListAdapter
import com.polarlooptheory.was.views.adapters.VehicleListAdapter
import com.polarlooptheory.was.views.character.CharacterBaseInfoFragment
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.android.synthetic.main.scenario_list.view.*

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
        val characterList = listOf(
            mCharacter(name = "Franciszek")
        )
        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter = CharacterListAdapter(characterList)
        view.char_list.adapter = adapter
        return view
    }
}