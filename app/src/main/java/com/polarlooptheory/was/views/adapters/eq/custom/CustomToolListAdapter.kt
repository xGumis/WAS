package com.polarlooptheory.was.views.adapters.eq.custom

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
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mTool
import com.polarlooptheory.was.views.adapters.app.inflate
import com.polarlooptheory.was.views.custom.eq.CustomToolFragment
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.android.synthetic.main.description_tool.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CustomToolListAdapter(private var toolList: List<mTool>) : RecyclerView.Adapter<CustomToolListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val inflatedView = parent.inflate(R.layout.char_list_row, false)
    return Holder(
        inflatedView
    )
}

    override fun getItemCount(): Int {
        return toolList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = toolList[position]
        holder.bind(item,this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var tool : mTool? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_tool,null)
                view.toolName.text = tool?.name
                view.toolCost.text = tool?.cost
                view.toolDescription.text = tool?.description
                view.toolCategory.text = tool?.category
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(tool: mTool, adapter: CustomToolListAdapter){
            this.tool = tool
            view.charName.text = tool.name
            view.charEditButton.setOnClickListener {
                ((view.context as MainActivity).supportFragmentManager.fragments.firstOrNull() as NavigationHost).navigateTo(
                    CustomToolFragment(tool, false),true)
            }
            view.deleteItemButton6.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = async{ Equipment.deleteTool(Scenario.connectedScenario.scenario, tool.name)}.await()
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