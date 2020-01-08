package com.polarlooptheory.was.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.mNote
import kotlinx.android.synthetic.main.char_list_row.view.*

class NoteListAdapter(private var noteList: List<mNote>) : RecyclerView.Adapter<NoteListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = noteList[position]
        holder.bind(item)
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

        fun bind(note: mNote){
            this.note = note
            view.charName.text = note.name
        }

    }
}