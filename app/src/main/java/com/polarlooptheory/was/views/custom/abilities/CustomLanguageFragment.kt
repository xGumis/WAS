package com.polarlooptheory.was.views.custom.abilities

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Abilities
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.abilities.mLanguage
import com.polarlooptheory.was.views.lists.custom.abilities.CustomLanguageListFragment
import kotlinx.android.synthetic.main.custom_language.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CustomLanguageFragment(private val language: mLanguage?, private val isNew: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_language, container, false)
        if(language!=null){
            view.customName.setText(language.name)
            if (!isNew) {view.customName.inputType = InputType.TYPE_NULL; view.customName.isFocusable = false}
            view.customDescription.setText(language.type)
            view.customScript.setText(language.script)
            view.switchVisible.isChecked = language.visible
        }
        view.buttonSubmit.setOnClickListener {
            if (!view.customName.text.isNullOrEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val req = when (language) {
                        null -> async {
                            Abilities.createLanguage(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.customScript.text.toString(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                        else -> async {
                            Abilities.patchLanguage(
                                Scenario.connectedScenario.scenario,
                                view.customName.text.toString(),
                                view.customDescription.text.toString(),
                                view.customScript.text.toString(),
                                view.switchVisible.isChecked
                            )
                        }.await()
                    }
                    if (req) (parentFragment as NavigationHost).navigateTo(
                        CustomLanguageListFragment(),
                        false
                    )
                    else {
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
            }else
                (activity as MainActivity).makeToast("Name cannot be empty")
        }
        return view
    }
}
