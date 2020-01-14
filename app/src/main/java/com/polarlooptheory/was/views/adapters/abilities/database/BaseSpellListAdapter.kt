package com.polarlooptheory.was.views.adapters.abilities.database

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_spell.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class BaseSpellListAdapter(private var spellList: List<mSpell>) : RecyclerView.Adapter<BaseSpellListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.base_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return spellList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = spellList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var spell : mSpell? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_spell,null)
                view.spellName.text = spell?.name
                view.spellLevel.text = spell?.level.toString()
                view.spellSchool.text = spell?.magicSchool
                view.spellRange.text = spell?.range
                view.spellCastTime.text = spell?.castingTime
                view.spellComponents.text = spell?.components
                view.spellDuration.text = spell?.duration
                view.spellMaterial.text = spell?.material
                view.spellConcetration.text = spell?.concentration.toString()
                view.spellRitual.text = spell?.ritual.toString()
                view.spellDescription.text = spell?.description
                view.spellHigherLvL.text = spell?.higherLevels
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(spell: mSpell){
            this.spell = spell
            view.listItemName.text = spell.name
        }

    }
}