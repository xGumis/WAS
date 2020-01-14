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
import com.polarlooptheory.was.model.abilities.mProficiency
import com.polarlooptheory.was.model.abilities.mSpell
import com.polarlooptheory.was.model.types.mMagicSchool
import com.polarlooptheory.was.views.adapters.app.inflate
import kotlinx.android.synthetic.main.description_abilities.view.*
import kotlinx.android.synthetic.main.list_row.view.*

class CustomMagicSchoolListAdapter(private var magicShoolList: List<mMagicSchool>) : RecyclerView.Adapter<CustomMagicSchoolListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return magicShoolList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = magicShoolList[position]
        holder.bind(item)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var magicSchool : mMagicSchool? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_abilities,null)
                view.detailsName.text = magicSchool?.name
                view.detailsDescription.text = magicSchool?.description
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(magicSchool: mMagicSchool){
            this.magicSchool = magicSchool
            view.listItemName.text = magicSchool.name
        }

    }
}