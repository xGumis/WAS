package com.polarlooptheory.was.views.adapters.abilities.database

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mProficiency
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.base_list_row.view.*
import kotlinx.android.synthetic.main.description_abilities.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BaseProficienciesAdapter(private var proficiencyList: List<mProficiency>) : RecyclerView.Adapter<BaseProficienciesAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.base_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return proficiencyList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = proficiencyList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var proficiency : mProficiency? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_abilities,null)
                view.detailsName.text = proficiency?.name
                view.detailsDescription.text = proficiency?.type
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(proficiency: mProficiency){
            this.proficiency = proficiency
            view.listItemName.text = proficiency.name
            if (Scenario.dummyCharacter == null)
                view.deleteItemButton.isVisible = false
            else view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                GlobalScope.launch(Dispatchers.Main) {
                    if(!character!!.abilities.proficiencies.contains(proficiency.name)){
                        character.abilities.proficiencies += listOf(proficiency.name)
                        val req = async{Scenario.patchCharacterAbilities(
                            Scenario.connectedScenario.scenario,
                            character.name,
                            character.abilities.features,
                            character.abilities.languages,
                            character.abilities.proficiencies,
                            character.abilities.traits
                        )}.await()
                        if(req){
                            (view.context as MainActivity).makeToast("Proficiency added")
                        }
                        else{
                            (view.context as MainActivity).makeToast(Settings.error_message)
                            Settings.error_message = ""
                        }
                    }else (view.context as MainActivity).makeToast("Character already have this proficiency")
                }
            }
        }

    }
}