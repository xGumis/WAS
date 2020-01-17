package com.polarlooptheory.was.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mNote
import com.polarlooptheory.was.views.lists.NoteListFragment
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import kotlinx.android.synthetic.main.note_edit.view.*
import kotlinx.android.synthetic.main.roll.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class NoteEditFragment(private val note: mNote?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.note_edit, container, false)
        if(note!=null){
            view.editNoteName.setText(note.name)
            view.editNoteContent.setText(note.content)
        }
        view.buttonSave.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                    val req = when(note){
                        null -> async {
                            Scenario.createNote(
                                Scenario.connectedScenario.scenario,
                                view.editNoteName.text.toString(),
                                view.editNoteContent.text.toString()
                            )
                        }.await()
                        else -> async {
                        Scenario.patchNote(
                            Scenario.connectedScenario.scenario, note.id,
                            view.editNoteName.text.toString(),
                            view.editNoteContent.text.toString()
                        )
                        }.await()
                    }
                    if(req) (parentFragment as NavigationHost).navigateTo(NoteListFragment(),false)
                    else {
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
            }
        }
        return view
    }



}
