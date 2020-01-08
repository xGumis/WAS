package com.polarlooptheory.was.views.lists

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.apiCalls.Scenario.scenariosList
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.model.mScenario
import com.polarlooptheory.was.views.adapters.ScenarioListAdapter
import com.polarlooptheory.was.views.adapters.TabListAdapter
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.android.synthetic.main.tab_list.view.*
import kotlinx.coroutines.*


class TabListFragment : Fragment() {

    private lateinit var adapter: TabListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tab_list, container, false)
        adapter = TabListAdapter(childFragmentManager)
        val vp = view.viewPager
        vp.adapter = adapter
        view.tabs.setupWithViewPager(vp)
        return view
        }

}
