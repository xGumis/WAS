package com.polarlooptheory.was.views.adapters.eq.database

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mGear
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_vehicle_gear.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class BaseGearListAdapter(private var gearList: List<mGear>) : RecyclerView.Adapter<BaseGearListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.base_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return gearList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = gearList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var gear : mGear? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_vehicle_gear,null)
                view.vehicleName.text = gear?.name
                view.vehicleCost.text = gear?.cost
                view.vehicleDescription.text = gear?.description
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(gear: mGear){
            this.gear = gear
            view.listItemName.text = gear.name
        }

    }
}