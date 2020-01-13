package com.polarlooptheory.was.views.adapters.app

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import kotlinx.android.synthetic.main.player_list_row.view.*

class PlayerListAdapter(private var playerList: List<String>) : RecyclerView.Adapter<PlayerListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.player_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = playerList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }

        fun bind(player: String){
            view.charName.text = player
        }

    }
}