package com.polarlooptheory.was.views.adapters.eq.character

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.equipment.mWeapon
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_weapon.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class WeaponListAdapter(private var weaponList: List<mWeapon>) : RecyclerView.Adapter<WeaponListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return weaponList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = weaponList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var weapon : mWeapon? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_weapon,null)
                view.weaponName.text = weapon?.name
                view.weaponCost.text = weapon?.cost
                view.weaponCategory.text = weapon?.category
                view.weaponDmg.text = weapon?.damageDice
                view.weaponDmgBonus.text = weapon?.damageBonus.toString()
                view.weaponDmgType.text = weapon?.damageType
                view.weaponLongRange.text = weapon?.longRange.toString()
                view.weaponRange.text = weapon?.weaponRange
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(weapon: mWeapon){
            this.weapon = weapon
            view.listItemName.text = weapon.name
        }

    }
}