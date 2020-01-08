package com.polarlooptheory.was.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.mCharacter
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class CharacterListAdapter(private var charList: List<mCharacter>) : RecyclerView.Adapter<CharacterListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return charList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = charList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var char : mCharacter? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("ass")
        }

        fun bind(char: mCharacter){
            this.char = char
            view.charName.text = char.name
        }

    }
}