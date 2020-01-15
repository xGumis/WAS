package com.polarlooptheory.was.views.adapters.app

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.mCharacter
import com.polarlooptheory.was.views.ScenarioFragment
import kotlinx.android.synthetic.main.char_list_row.view.*

class CharacterListAdapter() : RecyclerView.Adapter<CharacterListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return Scenario.connectedScenario.charactersList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = Scenario.connectedScenario.charactersList.values.toTypedArray()[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        private var char : mCharacter? = null

        fun bind(char: mCharacter){
            this.char = char
            view.charName.text = char.name
        }

    }
}