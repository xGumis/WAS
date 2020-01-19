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
import com.polarlooptheory.was.model.abilities.mTrait
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_abilities.view.*
import kotlinx.android.synthetic.main.list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterTraitsAdapter : RecyclerView.Adapter<CharacterTraitsAdapter.Holder>() {
    private var traitList: MutableList<mTrait> = mutableListOf()
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
        traitList.clear()
        GlobalScope.launch(Dispatchers.Main) {
        Scenario.dummyCharacter?.abilities?.traits?.forEach {
                val req =
                    async { Abilities.getTraits(Scenario.connectedScenario.scenario, it) }.await()
                if(!req.isNullOrEmpty()) traitList.add(req.first())
            }
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        return traitList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = traitList[position]
        holder.bind(item,this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var trait : mTrait? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_abilities,null)
                view.detailsName.text = trait?.name
                view.detailsDescription.text = trait?.description
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(trait: mTrait,adapter: CharacterTraitsAdapter){
            this.trait = trait
            view.listItemName.text = trait.name
            view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                GlobalScope.launch(Dispatchers.Main) {
                    if(character!!.abilities.traits.contains(trait.name)){
                        character.abilities.traits -= listOf(trait.name)
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
                            (view.context as MainActivity).makeToast("Trait deleted")
                            adapter.refreshList()
                        }
                        else{
                            (view.context as MainActivity).makeToast(Settings.error_message)
                            Settings.error_message = ""
                        }
                    }else (view.context as MainActivity).makeToast("Character doesn't have this traits")
                }
            }
        }

    }
}