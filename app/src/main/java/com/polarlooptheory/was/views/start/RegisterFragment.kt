package com.polarlooptheory.was.views.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Register
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import kotlinx.android.synthetic.main.register_screen.*
import kotlinx.android.synthetic.main.register_screen.view.*
import kotlinx.android.synthetic.main.start_screen.view.loginButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_screen, container, false)
        view.loginButton.setOnClickListener{
            var correct = true
            val username = loginUsername.text.trim().toString()
            val email = editText.text.trim().toString()
            val password = loginPassword.text.toString()
            val confirm = loginPasswordConfirm.text.toString()
            val err = Register.checkFields(username,email,password)
            if(err.contains(Register.Field.USERNAME)){
                correct = false
                loginUsername.error = "Username must be at least 4 characters long"
            }
            if(err.contains(Register.Field.EMAIL)){
                correct = false
                editText.error = "Email is not valid"
            }
            if(err.contains(Register.Field.PASSWORD)){
                correct = false
                loginPassword.error = "Password must be at least 6 characters long"
            }
            if(password!=confirm){
                correct = false
                loginPasswordConfirm.error = "Passwords don't match"
            }
            if(correct){
                GlobalScope.launch {
                    val req = async{Register.register(username,email, password)}.await()
                    if(req)
                        (activity as NavigationHost).navigateTo(LoginFragment(),false)
                    else {
                        (activity as MainActivity).makeToast(Settings.error_message)
                        Settings.error_message = ""
                    }
                }
            }
        }
        view.textSignUp.setOnClickListener {
            (activity as NavigationHost).navigateTo(LoginFragment(), false)
        }
        return view
    }
}
