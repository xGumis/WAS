package com.polarlooptheory.was.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.model.mNote
import com.polarlooptheory.was.views.NoteEditFragment
import com.polarlooptheory.was.views.adapters.misc.NoteListAdapter
import kotlinx.android.synthetic.main.characters.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: NoteListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.text = "ADD NOTE"
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(NoteEditFragment(),false)
        }
        val list: MutableList<mNote> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            val req = async{ Scenario.getNotes(Scenario.connectedScenario.scenario)}.await()
            if(req) adapter.notifyDataSetChanged()
        }

        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            NoteListAdapter(list)
        view.char_list.adapter = adapter

        return view
    }
}