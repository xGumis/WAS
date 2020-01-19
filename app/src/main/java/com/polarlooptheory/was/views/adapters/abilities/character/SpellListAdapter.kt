package com.polarlooptheory.was.views.adapters.abilities.character

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.base_list_row.view.*
import kotlinx.android.synthetic.main.description_spell.view.*
import kotlinx.android.synthetic.main.list_row.view.*
import kotlinx.android.synthetic.main.list_row.view.deleteItemButton
import kotlinx.android.synthetic.main.list_row.view.listItemName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SpellListAdapter: RecyclerView.Adapter<SpellListAdapter.Holder>() {
    private var spellList: MutableList<mSpell> = mutableListOf()
    init {
        refreshList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.list_row, false)
        return Holder(
            inflatedView
        )
    }

    fun refreshList(){
        spellList.clear()
        GlobalScope.launch(Dispatchers.Main) {
        Scenario.dummyCharacter?.spells?.spells?.forEach {
                val req =
                    async { Abilities.getSpells(Scenario.connectedScenario.scenario, it) }.await()
                if(!req.isNullOrEmpty()) spellList.add(req.first())
            }
            notifyDataSetChanged()
        }
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

        fun bind(spell: mSpell,adapter: SpellListAdapter){
            this.spell = spell
            view.listItemName.text = spell.name
            view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                GlobalScope.launch(Dispatchers.Main) {
                    if(character!!.spells.spells.contains(spell.name)){
                        character.spells.spells -= listOf(spell.name)
                        val req = async{
                            Scenario.patchCharacterAbilities(
                                Scenario.connectedScenario.scenario,
                                character.name,
                                character.abilities.features,
                                character.abilities.languages,
                                character.abilities.proficiencies,
                                character.abilities.traits
                            )}.await()
                        if(req){
                            (view.context as MainActivity).makeToast("Spell deleted")
                            adapter.refreshList()
                        }
                        else{
                            (view.context as MainActivity).makeToast(Settings.error_message)
                            Settings.error_message = ""
                        }
                    }else (view.context as MainActivity).makeToast("Character doesn't know this spell")
                }
            }
        }

    }
}