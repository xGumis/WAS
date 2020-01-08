package com.polarlooptheory.was.views.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import kotlinx.android.synthetic.main.register_screen.view.*
import kotlinx.android.synthetic.main.start_screen.view.loginButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_screen, container, false)
        view.loginButton.setOnClickListener{
            GlobalScope.launch {
                (activity as NavigationHost).navigateTo(ScenarioListFragment(),false)
            }
        }
        view.textSignUp.setOnClickListener {
            GlobalScope.launch {
                (activity as NavigationHost).navigateTo(LoginFragment(),false)
            }
        }
        return view
    }
}
