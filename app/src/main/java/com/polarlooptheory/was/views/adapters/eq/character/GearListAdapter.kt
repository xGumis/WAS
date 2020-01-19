package com.polarlooptheory.was.views.adapters.eq.character

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
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Equipment
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.equipment.mGear
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_vehicle_gear.view.*
import kotlinx.android.synthetic.main.eq_list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class GearListAdapter : RecyclerView.Adapter<GearListAdapter.Holder>() {
    private var gearList: MutableList<mGear> = mutableListOf()
    init{
        refreshList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.eq_list_row, false)
        return Holder(
            inflatedView
        )
    }

    fun refreshList(){
        gearList.clear()
        GlobalScope.launch(Dispatchers.Main) {
        Scenario.connectedScenario.chosenCharacter?.equipment?.gear?.forEach {
                val req =
                    async { Equipment.getGears(Scenario.connectedScenario.scenario, it.first) }.await()
                if(!req.isNullOrEmpty()) gearList.addAll(req)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return gearList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = gearList[position]
        holder.bind(item,this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var gear : mGear? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_vehicle_gear,null)
                view.vehicleName.text = gear?.name
                view.vehicleCost.text = gear?.cost
                view.vehicleDescription.text = gear?.description
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(gear: mGear,adapter: GearListAdapter){
            this.gear = gear
            view.listItemName.text = gear.name
            val qt = Scenario.dummyCharacter!!.equipment.gear.find{ t -> t.first == gear.name }!!.second
            view.quantity.setText(qt.toString()+"x")
            view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                val alert = AlertDialog.Builder(view.context)
                alert.setTitle("Remove " + gear.name)
                alert.setMessage("Enter quantity")
                val nmbr = EditText(view.context)
                nmbr.inputType = InputType.TYPE_CLASS_NUMBER
                alert.setView(nmbr)
                alert.setPositiveButton("Ok") { _, _ ->
                    if (nmbr.text.isNotEmpty() && nmbr.text.isDigitsOnly()) {
                        val quantity = nmbr.text.toString().toInt()
                        val list = character!!.equipment.gear.toMutableList()
                        if (list.filter { t -> t.first == gear.name }.first().second <= quantity)
                            list.remove(Pair(gear.name, quantity))
                        else {
                            val old = list.find { t -> t.first == gear.name }
                            list.remove(old)
                            val new = Pair(old!!.first,old.second-quantity)
                            list.add(new)
                        }
                        character.equipment.gear = list
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
                                (view.context as MainActivity).makeToast("Gear deleted")
                                adapter.notifyDataSetChanged()
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