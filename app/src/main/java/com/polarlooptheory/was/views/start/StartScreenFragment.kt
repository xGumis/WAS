package com.polarlooptheory.was.views.start

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import com.polarlooptheory.was.MainActivity
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
        val drawer = (activity as NavigationHost).getDrawer()
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        view.loginButton.setOnClickListener{
                (activity as NavigationHost).navigateTo(LoginFragment(),false)
        }
        view.registerMain.setOnClickListener {
                (activity as NavigationHost).navigateTo(RegisterFragment(),false)
        }
        return view
    }
}
