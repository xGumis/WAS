package com.polarlooptheory.was.views.adapters.abilities.custom

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
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mLanguage
import com.polarlooptheory.was.views.adapters.app.inflate
import com.polarlooptheory.was.views.custom.abilities.CustomLanguageFragment
import kotlinx.android.synthetic.main.char_list_row.view.*
import kotlinx.android.synthetic.main.description_abilities_language.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CustomLanguagesAdapter(private var languageList: List<mLanguage>) : RecyclerView.Adapter<CustomLanguagesAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflatedView = parent.inflate(R.layout.char_list_row, false)
        return Holder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = languageList[position]
        holder.bind(item, this)
    }


    class Holder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var language : mLanguage? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v!=null){
                val context = v.context
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.description_abilities_language,null)
                view.detailsName.text = language?.name
                view.detailsType.text = language?.type
                view.detailsScript.text = language?.type
                val popupWindow = PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,true)
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
                view.setOnTouchListener { _, _ -> popupWindow.dismiss();true }
            }
        }

        fun bind(language: mLanguage, adapter: CustomLanguagesAdapter){
            this.language = language
            view.charName.text = language.name
            view.charEditButton.setOnClickListener {
                ((view.context as MainActivity).supportFragmentManager.fragments.firstOrNull() as NavigationHost).navigateTo(
                    CustomLanguageFragment(language, false),true)
            }
            view.deleteItemButton6.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val req = async{ Abilities.deleteLanguage(Scenario.connectedScenario.scenario, language.name)}.await()
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