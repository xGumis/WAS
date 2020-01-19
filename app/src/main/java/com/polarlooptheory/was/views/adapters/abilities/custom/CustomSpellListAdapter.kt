package com.polarlooptheory.was.views.adapters.abilities.custom

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
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.views.adapters.app.inflate
import com.polarlooptheory.was.views.custom.magic.CustomSpellFragment
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.android.synthetic.main.description_spell.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CustomSpellListAdapter(private var spellList: List<mSpell>) : RecyclerView.Adapter<CustomSpellListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return spellList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = spellList[position]
        holder.bind(item,this)
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

        fun bind(spell: mSpell, adapter: CustomSpellListAdapter){
            this.spell = spell
            view.charName.text = spell.name
            view.charEditButton.setOnClickListener {
                ((view.context as MainActivity).supportFragmentManager.fragments.firstOrNull() as NavigationHost).navigateTo(
                    CustomSpellFragment(spell, false),true)
            }
            view.deleteItemButton6.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = async{ Abilities.deleteSpell(Scenario.connectedScenario.scenario, spell.name)}.await()
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