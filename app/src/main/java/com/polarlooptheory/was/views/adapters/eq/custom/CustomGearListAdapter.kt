package com.polarlooptheory.was.views.adapters.eq.custom

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mGear
import com.polarlooptheory.was.views.adapters.app.inflate
import com.polarlooptheory.was.views.custom.eq.CustomGearFragment
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.android.synthetic.main.description_vehicle_gear.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CustomGearListAdapter(private var gearList: List<mGear>) : RecyclerView.Adapter<CustomGearListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return gearList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = gearList[position]
        holder.bind(item,this)
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

        fun bind(gear: mGear, adapter: CustomGearListAdapter){
            this.gear = gear
            view.charName.text = gear.name
            view.charEditButton.setOnClickListener {
                ((view.context as MainActivity).supportFragmentManager.fragments.firstOrNull() as NavigationHost).navigateTo(
                    CustomGearFragment(gear, false),true)
            }
            view.deleteItemButton6.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = async{ Equipment.deleteGear(Scenario.connectedScenario.scenario, gear.name)}.await()
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