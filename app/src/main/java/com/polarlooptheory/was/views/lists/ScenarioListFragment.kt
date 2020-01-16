package com.polarlooptheory.was.views.lists

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.views.adapters.app.ScenarioListAdapter
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.android.synthetic.main.scenario_list.view.*
import kotlinx.android.synthetic.main.scenario_login.view.*
import kotlinx.coroutines.*


class ScenarioListFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ScenarioListAdapter
    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario_list, container, false)
        refreshAdapter()
        makeDrawer(activity as Activity)
        linearLayoutManager = LinearLayoutManager(activity)
        view.scenarioListRec.layoutManager = linearLayoutManager
        adapter =
            ScenarioListAdapter()
        view.scenarioListRec.adapter = adapter
        return view
        }
    private fun makeDrawer(activity: Activity){
        val drawer = (activity as NavigationHost).getDrawer()
        val logout = PrimaryDrawerItem().withIdentifier(1).withName("Logout").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                drawer.removeAllItems()
                Login.logout()
                (activity as NavigationHost).navigateTo(StartScreenFragment(),false)
                return false
            }
        })
        val join = PrimaryDrawerItem().withIdentifier(2).withName("Join scenario").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                val alert = AlertDialog.Builder(activity)
                alert.setTitle("Join scenario")
                val v = LayoutInflater.from(context).inflate(R.layout.scenario_login,null)
                alert.setView(v)
                alert.setPositiveButton("OK") { dialogInterface, _ ->
                    GlobalScope.launch(Dispatchers.Main) {
                        val req =
                            async { Scenario.joinScenario(v.scenarioCode.text.toString(),v.editText5.text.toString()) }.await()
                        if (req) {
                            refreshAdapter()
                        }
                        else {
                            (activity as MainActivity).makeToast(Settings.error_message)
                            Settings.error_message = ""
                        }
                    }
                    dialogInterface.dismiss()
                }
                alert.setNegativeButton("Cancel"){
                        dialogInterface: DialogInterface, _ -> dialogInterface.cancel()
                }
                val dialog: AlertDialog = alert.create()
                dialog.show()
                return false
            }
        })
        val create = PrimaryDrawerItem().withIdentifier(3).withName("Create scenario").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                val alert = AlertDialog.Builder(activity)
                alert.setTitle("Create scenario")
                //val v = View(context).findViewById<LinearLayout>()
                //alert.setView(v)
                alert.setPositiveButton("OK") { _, _ ->
                    /*GlobalScope.launch(Dispatchers.Main) {
                        val req =
                            async { Scenario.createScenario() }.await()
                        if (!req.isNullOrEmpty()) {
                        }
                    }*/
                }
                alert.setNegativeButton("Cancel"){
                        dialogInterface: DialogInterface, _ -> dialogInterface.cancel()
                }
                val dialog: AlertDialog = alert.create()
                dialog.show()
                return false
            }
        })

        val hdr = (activity as MainActivity).header
        hdr.clear()
        hdr.addProfiles(ProfileDrawerItem().withName(User.username))
        drawer.removeAllItems()
        drawer.setToolbar(activity,(activity as MainActivity).toolbar)
        drawer.addItems(
            logout,
            DividerDrawerItem(),
            join,
            create
        )
        drawer.onDrawerItemClickListener = object : Drawer.OnDrawerItemClickListener{
            override fun onItemClick(
                view: View?,
                position: Int,
                drawerItem: IDrawerItem<*>
            ): Boolean {
                return false
            }
        }
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
    fun refreshAdapter(){
        GlobalScope.launch(Dispatchers.Main) {
            val req = async{Scenario.getScenarios()}.await()
            if(req) adapter.notifyDataSetChanged()
        }
    }
}
