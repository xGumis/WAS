package com.polarlooptheory.was.views.adapters.abilities.character

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mFeature
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_abilities.view.*
import kotlinx.android.synthetic.main.list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CharacterFeaturesAdapter : RecyclerView.Adapter<CharacterFeaturesAdapter.Holder>() {
    private var featureList: MutableList<mFeature> = mutableListOf()
    init{
        refreshList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.list_row, false)
        return Holder(
            inflatedView
        )
    }

    fun refreshList(){
        featureList.clear()
        GlobalScope.launch(Dispatchers.Main) {
        Scenario.dummyCharacter?.abilities?.features?.forEach {
                val req =
                    async { Abilities.getFeatures(Scenario.connectedScenario.scenario, it) }.await()
                if(!req.isNullOrEmpty()) featureList.add(req.first())
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = featureList[position]
        holder.bind(item,this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var feature : mFeature? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_abilities,null)
                view.detailsName.text = feature?.name
                view.detailsDescription.text = feature?.description
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(feature: mFeature,adapter: CharacterFeaturesAdapter){
            this.feature = feature
            view.listItemName.text = feature.name
            view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                GlobalScope.launch(Dispatchers.Main) {
                    if(character!!.abilities.features.contains(feature.name)){
                        character.abilities.features -= listOf(feature.name)
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
                            (view.context as MainActivity).makeToast("Feature deleted")
                            adapter.refreshList()
                        }
                        else{
                            (view.context as MainActivity).makeToast(Settings.error_message)
                            Settings.error_message = ""
                        }
                    }else (view.context as MainActivity).makeToast("Character doesn't have this feature")
                }

            }
        }

    }
}