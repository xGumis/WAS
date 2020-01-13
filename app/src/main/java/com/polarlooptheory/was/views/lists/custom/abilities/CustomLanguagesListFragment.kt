package com.polarlooptheory.was.views.lists.custom.abilities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.adapters.app.CharacterListAdapter
import com.polarlooptheory.was.views.character.statistics.CharacterBaseInfoFragment
import kotlinx.android.synthetic.main.characters.view.*


class CustomLanguagesListFragment : Fragment() {
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
        adapter =
            CharacterListAdapter(
                characterList
            )
        view.char_list.adapter = adapter
        return view
    }
}
