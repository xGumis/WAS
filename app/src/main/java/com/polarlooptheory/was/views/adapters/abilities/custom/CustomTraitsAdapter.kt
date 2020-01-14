package com.polarlooptheory.was.views.adapters.abilities.custom

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.abilities.mTrait
import com.polarlooptheory.was.views.adapters.abilities.character.CharacterTraitsAdapter
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_abilities.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class CustomTraitsAdapter(private var traitList: List<mTrait>) : RecyclerView.Adapter<CustomTraitsAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return traitList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = traitList[position]
        holder.bind(item)
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

        fun bind(trait: mTrait){
            this.trait = trait
            view.listItemName.text = trait.name
        }

    }
}