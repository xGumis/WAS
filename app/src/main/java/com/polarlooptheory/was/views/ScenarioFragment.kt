package com.polarlooptheory.was.views

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.ChatAdapter
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.model.mMessage
import com.polarlooptheory.was.views.lists.*
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.android.synthetic.main.scenario.view.*

class ScenarioFragment : Fragment(),NavigationHost {
    private lateinit var chatRecyclerView: RecyclerView
    private var isUp = false
    private lateinit var chat : LinearLayout
    private lateinit var  mainView : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario, container, false)
        mainView = view
        makeDrawer(activity as Activity)
        initChat(context)
        return view
    }
    private fun makeDrawer(activity: Activity){
        val drawer = (activity as NavigationHost).getDrawer()
        val logout = PrimaryDrawerItem().withIdentifier(1).withName("Logout").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                drawer.resetDrawerContent()
                drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
                drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                Login.logout()
                (activity as NavigationHost).navigateTo(StartScreenFragment(),false)
                return false
            }
        })

        val leave = PrimaryDrawerItem().withIdentifier(33).withName("Return to list").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                Login.logout()
                (activity as NavigationHost).navigateTo(StartScreenFragment(),false)
                return false
            }
        })
        val characters = PrimaryDrawerItem().withIdentifier(2).withName("Characters").withDescription("Choose character...").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                navigateTo(CharacterListFragment(),false)
                return false
            }
        })
        val roll = PrimaryDrawerItem().withIdentifier(3).withName("Roll").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                navigateTo(RollFragment(),false)
                return false
            }
        })
        val notes = PrimaryDrawerItem().withIdentifier(4).withName("Notes").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                navigateTo(NoteListFragment(),false)
                return false
            }
        })
        val statistics = ExpandableDrawerItem().withName("Statistics").withSubItems(
            SecondaryDrawerItem().withIdentifier(5).withName("Base Info"),
            SecondaryDrawerItem().withIdentifier(6).withName("Background"),
            SecondaryDrawerItem().withIdentifier(7).withName("Stats"),
            SecondaryDrawerItem().withIdentifier(8).withName("Health"),
            SecondaryDrawerItem().withIdentifier(9).withName("Proficiencies")
        )
        val abilities = ExpandableDrawerItem().withName("Abilities").withSubItems(
            SecondaryDrawerItem().withIdentifier(10).withName("Features"),
            SecondaryDrawerItem().withIdentifier(11).withName("Languages"),
            SecondaryDrawerItem().withIdentifier(12).withName("Traits"),
            SecondaryDrawerItem().withIdentifier(13).withName("Proficiencies")
        )
        val equipment = ExpandableDrawerItem().withName("Equipment").withSubItems(
            SecondaryDrawerItem().withIdentifier(14).withName("Weapons").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(WeaponListFragment(),false)
                    return false
                }
            }),
            SecondaryDrawerItem().withIdentifier(15).withName("Armors").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(ArmorListFragment(),false)
                    return false
                }
            }),
            SecondaryDrawerItem().withIdentifier(16).withName("Gear").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(GearListFragment(),false)
                    return false
                }
            }),
            SecondaryDrawerItem().withIdentifier(17).withName("Tools").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(ToolListFragment(),false)
                    return false
                }
            }),
            SecondaryDrawerItem().withIdentifier(18).withName("Vehicles").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(TabListFragment(),false)
                    return false
                }
            }),
            SecondaryDrawerItem().withIdentifier(19).withName("Money").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(MoneyFragment(),false)
                    return false
                }
            })

        )
        val magic = PrimaryDrawerItem().withIdentifier(20).withName("Magic")
        val management = ExpandableDrawerItem().withName("Game Management").withSubItems(
            ExpandableDrawerItem().withName("Custom Abilities").withSubItems(
                SecondaryDrawerItem().withIdentifier(21).withName("Features"),
                SecondaryDrawerItem().withIdentifier(22).withName("Languages"),
                SecondaryDrawerItem().withIdentifier(23).withName("Traits"),
                SecondaryDrawerItem().withIdentifier(24).withName("Proficiencies")
            ),
            ExpandableDrawerItem().withName("Custom Equipment").withSubItems(
                SecondaryDrawerItem().withIdentifier(25).withName("Weapons"),
                SecondaryDrawerItem().withIdentifier(26).withName("Armors"),
                SecondaryDrawerItem().withIdentifier(27).withName("Gear"),
                SecondaryDrawerItem().withIdentifier(28).withName("Tools"),
                SecondaryDrawerItem().withIdentifier(29).withName("Vehicles").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                        navigateTo(CustomVehicleListFragment(),false)
                        return false
                    }
                })
            ),
            ExpandableDrawerItem().withName("Custom Magic").withSubItems(
                SecondaryDrawerItem().withIdentifier(30).withName("Spells"),
                SecondaryDrawerItem().withIdentifier(31).withName("Magic Schools")
            ),
            SecondaryDrawerItem().withIdentifier(32).withName("Players").withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    navigateTo(PlayerListFragment(),false)
                    return false
                }
            }),
            SecondaryDrawerItem().withIdentifier(34).withName("Show Key"),
            SecondaryDrawerItem().withIdentifier(35).withName("Change Password"),
            SecondaryDrawerItem().withIdentifier(36).withName("Delete scenario")
        )


        val headerResult = AccountHeaderBuilder()
            .withActivity(activity)
            .addProfiles(
                ProfileDrawerItem().withName(User.username)
            )
            .build()
        drawer.resetDrawerContent()
        drawer.setHeader(headerResult.view,true)
        drawer.setToolbar(activity,(activity as MainActivity).toolbar)
        drawer.addItems(
            logout,
            leave,
            characters,
            DividerDrawerItem(),
            statistics,
            abilities,
            equipment,
            magic,
            DividerDrawerItem(),
            roll,
            notes

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
        if(User.username == Scenario.connectedScenario.scenario.gameMaster)
            drawer.addItemAtPosition(management,9)
    }
    private fun initChat(context: Context?){
        val dataset = arrayOf(
            mMessage(content="testowa wiadomość",sender = "admin",type = mMessage.Type.OOC),
            mMessage(content = "wiadomość systemowa",type = mMessage.Type.SYSTEM),
            mMessage(content = "wiadomość gracza",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec lacus ex, dapibus et ipsum ut, pellentesque convallis augue. Proin at.",sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość postaci",sender = "Postać",type = mMessage.Type.CHARACTER),
            mMessage(content = "wiadomość prywatna",sender = "Postać",type = mMessage.Type.WHISPER)
        )
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = ChatAdapter(dataset)
        chatRecyclerView = mainView.messageList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        chat = mainView.animatedLayout
        chat.post { chat.translationY = chat.chatLayout.height.toFloat() }
        chat.chatButton.setOnClickListener {
            if(!isUp) slideUp()
            else slideDown()
            isUp = !isUp
        }
    }
    private fun slideUp() {
        chat.animate().translationY(0f).setDuration(500).start()
    }
    private fun slideDown() {
        val h = chat.chatLayout.height.toFloat()
        chat.animate().translationY(h).setDuration(500).start()
    }
    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = childFragmentManager.beginTransaction().replace(R.id.container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun getDrawer(): Drawer {
        return (activity as NavigationHost).getDrawer()
    }

}
