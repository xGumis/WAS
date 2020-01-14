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
import com.polarlooptheory.was.model.equipment.mArmor
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_armor.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class BaseArmorListAdapter(private var armorList: List<mArmor>) : RecyclerView.Adapter<BaseArmorListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.base_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return armorList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = armorList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var armor : mArmor? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_armor,null)
                view.armorName.text = armor?.name
                view.armorCost.text = armor?.cost
                view.armorClass.text = armor?.armorClass?.base.toString()
                view.armorStealth.text = armor?.stealthDisadvantage.toString()
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(armor: mArmor){
            this.armor = armor
            view.listItemName.text = armor.name
        }

    }
}