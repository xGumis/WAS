package com.polarlooptheory.was.views.adapters.misc

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mNote
import com.polarlooptheory.was.views.NoteEditFragment
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteListAdapter(private var noteList: List<mNote>,private val fragment: Fragment?) : RecyclerView.Adapter<NoteListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(inflatedView)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = noteList[position]
        holder.bind(item, fragment, this)
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

        fun bind(note: mNote,fragment: Fragment? ,adapter: NoteListAdapter){
            this.note = note
            view.charName.text = note.name
            view.charEditButton.setOnClickListener {
                (fragment as NavigationHost).navigateTo(NoteEditFragment(true, note),false)
            }
            view.deleteItemButton6.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = async{ Scenario.deleteNote(Scenario.connectedScenario.scenario, note.id)}.await()
                    if(req) adapter.notifyDataSetChanged()
                }
            }
        }
    }
}