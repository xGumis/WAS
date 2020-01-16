package com.polarlooptheory.was.views.adapters.app

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import kotlinx.android.synthetic.main.player_list_row.view.*

class PlayerListAdapter() : RecyclerView.Adapter<PlayerListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.player_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return Scenario.connectedScenario.scenario.onlinePlayers.size + Scenario.connectedScenario.scenario.offlinePlayers.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val players =  Scenario.connectedScenario.scenario.onlinePlayers+Scenario.connectedScenario.scenario.offlinePlayers
        val item = players[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v

        fun bind(player: String){
            view.charName.text = player
        }

    }
}