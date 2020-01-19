package com.polarlooptheory.was.views.adapters.misc

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mNote
import com.polarlooptheory.was.views.NoteEditFragment
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteListAdapter : RecyclerView.Adapter<NoteListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(inflatedView)
    }

    override fun getItemCount(): Int {
        return Scenario.connectedScenario.notesList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = Scenario.connectedScenario.notesList[position]
        holder.bind(item,this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var note : mNote? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("ass")
        }

        fun bind(note: mNote,adapter: NoteListAdapter){
            this.note = note
            view.charName.text = note.name
            view.charEditButton.setOnClickListener {
                ((view.context as MainActivity).supportFragmentManager.fragments.firstOrNull() as NavigationHost).navigateTo(NoteEditFragment(note, false),true)
            }
            view.deleteItemButton6.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = async{ Scenario.deleteNote(Scenario.connectedScenario.scenario, note.id)}.await()
                    if(req) adapter.notifyDataSetChanged()
                    else {
                        (view.context as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
            }
        }
    }
}