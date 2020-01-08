package com.polarlooptheory.was.views.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.views.ScenarioFragment
import kotlinx.android.synthetic.main.start_screen.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class StartScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.start_screen, container, false)
        view.loginButton.setOnClickListener{
            GlobalScope.launch {
                (activity as NavigationHost).navigateTo(LoginFragment(),false)
            }
        }
        view.registerMain.setOnClickListener {
            GlobalScope.launch {
                (activity as NavigationHost).navigateTo(RegisterFragment(),false)
            }
        }
        view.button.setOnClickListener {
            GlobalScope.launch {
                User.username = "admin"
                Scenario.connectedScenario.scenario.gameMaster = "admin"
                (activity as NavigationHost).navigateTo(ScenarioFragment(),false)
            }
        }
        return view
    }
}
