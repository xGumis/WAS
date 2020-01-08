package com.polarlooptheory.was.views

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
import kotlinx.android.synthetic.main.money.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MoneyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.money, container, false)
        view.buttonSave.setOnClickListener {
        }
        return view
    }



}
