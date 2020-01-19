package com.polarlooptheory.was.views.adapters.eq.database

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mTool
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_tool.view.*
import kotlinx.android.synthetic.main.list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BaseToolListAdapter(private var toolList: List<mTool>) : RecyclerView.Adapter<BaseToolListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.base_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return toolList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = toolList[position]
        holder.bind(item)
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

        fun bind(tool: mTool){
            this.tool = tool
            view.listItemName.text = tool.name
            if (Scenario.dummyCharacter == null)
                view.deleteItemButton.isVisible = false
            else view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                val alert = AlertDialog.Builder(view.context)
                alert.setTitle("Add " + tool.name)
                alert.setMessage("Enter quantity")
                val nmbr = EditText(view.context)
                nmbr.inputType = InputType.TYPE_CLASS_NUMBER
                alert.setView(nmbr)
                alert.setPositiveButton("Ok") { _, _ ->
                    if (nmbr.text.isNotEmpty() && nmbr.text.isDigitsOnly()) {
                        val quantity = nmbr.text.toString().toInt()
                        val list = character!!.equipment.tools.toMutableList()
                        if (list.filter { t -> t.first == tool.name }.isEmpty())
                            list.add(Pair(tool.name, quantity))
                        else {
                            val old = list.find { t -> t.first == tool.name }
                            list.remove(old)
                            val new = Pair(old!!.first,old.second+quantity)
                            list.add(new)
                        }
                        character.equipment.tools = list
                        GlobalScope.launch(Dispatchers.Main) {
                            val req = async {
                                Scenario.patchCharacterEquipment(
                                    Scenario.connectedScenario.scenario,
                                    character.name,
                                    character.equipment.armorClass,
                                    character.equipment.armors,
                                    character.equipment.attacks,
                                    character.equipment.currency.cp,
                                    character.equipment.currency.sp,
                                    character.equipment.currency.ep,
                                    character.equipment.currency.gp,
                                    character.equipment.currency.pp,
                                    character.equipment.gear,
                                    character.equipment.tools,
                                    character.equipment.vehicles,
                                    character.equipment.weapons
                                )
                            }.await()
                            if (req) {
                                (view.context as MainActivity).makeToast("Tool added")
                            } else {
                                (view.context as MainActivity).makeToast(Settings.error_message)
                                Settings.error_message = ""
                            }
                        }
                    } else (view.context as MainActivity).makeToast("Number cannot be empty")
                }
                alert.setNegativeButton(
                    "Cancel",
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, _ -> dialogInterface.cancel() })
                val dialog: AlertDialog = alert.create()
                dialog.show()
            }
        }

    }
}