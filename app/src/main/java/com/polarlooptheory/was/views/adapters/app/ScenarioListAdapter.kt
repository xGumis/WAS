package com.polarlooptheory.was.views.adapters.app

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.apiCalls.Scenario.scenariosList
import com.polarlooptheory.was.model.mScenario
import com.polarlooptheory.was.views.ScenarioFragment
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

    class Holder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        private var scenario: mScenario? = null

        fun bind(scenario: mScenario) {
            this.scenario = scenario
            view.textScenarioName.text = scenario.name
            view.imageButton2.setOnClickListener {
                val alert = AlertDialog.Builder(view.context)
                alert.setTitle("Scenario code")
                alert.setMessage(scenario.scenarioKey)
                alert.setNeutralButton("OK", null)
                val dialog: AlertDialog = alert.create()
                dialog.show()
            }
            view.imageButton3.setOnClickListener {
                Scenario.connectedScenario.scenario = scenario
                (view.context as NavigationHost).navigateTo(ScenarioFragment(),false)
            }
        }

    }
}