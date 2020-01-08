package com.polarlooptheory.was.views.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import kotlinx.android.synthetic.main.login_screen.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_screen, container, false)
        view.loginButton.setOnClickListener {
            GlobalScope.launch {
                val login = async{Login.getToken(view.loginUsername.text.toString(), view.loginPassword.text.toString())}
                if(login.await()){
                    (activity as NavigationHost).navigateTo(ScenarioListFragment(),false)
            }
        }
        }
        view.textSignUp.setOnClickListener {
            GlobalScope.launch {
                (activity as NavigationHost).navigateTo(RegisterFragment(), false)
            }
        }
        return view
    }



}
