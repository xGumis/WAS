package com.polarlooptheory.was.views.adapters

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mVehicle
import kotlinx.android.synthetic.main.description_vehicle_gear.view.*
import kotlinx.android.synthetic.main.list_row.view.*
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