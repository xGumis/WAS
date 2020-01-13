package com.polarlooptheory.was.views.adapters.app

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario.scenariosList
import com.polarlooptheory.was.model.mScenario
import kotlinx.android.synthetic.main.scenario_list_row.view.*

class ScenarioListAdapter : RecyclerView.Adapter<ScenarioListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.scenario_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return scenariosList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = scenariosList[position]
        holder.bind(item)
    }

    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var scenario : mScenario? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            println("ass")
        }

        fun bind(scenario: mScenario){
            this.scenario = scenario
            view.textScenarioName.text = scenario.name
        }

    }
}