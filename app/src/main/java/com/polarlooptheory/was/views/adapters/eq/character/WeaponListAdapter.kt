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
import com.polarlooptheory.was.model.equipment.mWeapon
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_weapon.view.*
import kotlinx.android.synthetic.main.eq_list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WeaponListAdapter: RecyclerView.Adapter<WeaponListAdapter.Holder>() {
    private var weaponList: MutableList<mWeapon> = mutableListOf()
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
        weaponList.clear()
        GlobalScope.launch(Dispatchers.Main) {
        Scenario.connectedScenario.chosenCharacter?.equipment?.weapons?.forEach {
                val req =
                    async { Equipment.getWeapons(Scenario.connectedScenario.scenario, it.first) }.await()
                if(!req.isNullOrEmpty()) weaponList.addAll(req)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return weaponList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = weaponList[position]
        holder.bind(item,this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var weapon : mWeapon? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_weapon,null)
                view.weaponName.text = weapon?.name
                view.weaponCost.text = weapon?.cost
                view.weaponCategory.text = weapon?.category
                view.weaponDmg.text = weapon?.damageDice
                view.weaponDmgBonus.text = weapon?.damageBonus.toString()
                view.weaponDmgType.text = weapon?.damageType
                view.weaponLongRange.text = weapon?.longRange.toString()
                view.weaponRange.text = weapon?.weaponRange
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(weapon: mWeapon,adapter: WeaponListAdapter){
            this.weapon = weapon
            view.listItemName.text = weapon.name
            val qt = Scenario.dummyCharacter!!.equipment.weapons.find{ t -> t.first == weapon.name }!!.second
            view.quantity.setText(qt.toString()+"x")
            view.deleteItemButton.setOnClickListener {
                val character = Scenario.dummyCharacter
                val alert = AlertDialog.Builder(view.context)
                alert.setTitle("Remove " + weapon.name)
                alert.setMessage("Enter quantity")
                val nmbr = EditText(view.context)
                nmbr.inputType = InputType.TYPE_CLASS_NUMBER
                alert.setView(nmbr)
                alert.setPositiveButton("Ok") { _, _ ->
                    if (nmbr.text.isNotEmpty() && nmbr.text.isDigitsOnly()) {
                        val quantity = nmbr.text.toString().toInt()
                        val list = character!!.equipment.weapons.toMutableList()
                        if (list.filter { t -> t.first == weapon.name }.first().second <= quantity)
                            list.remove(Pair(weapon.name, quantity))
                        else {
                            val old = list.find { t -> t.first == weapon.name }
                            list.remove(old)
                            val new = Pair(old!!.first,old.second-quantity)
                            list.add(new)
                        }
                        character.equipment.weapons = list
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
                                (view.context as MainActivity).makeToast("Weapon deleted")
                                adapter.refreshList()
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