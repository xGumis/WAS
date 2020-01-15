package com.polarlooptheory.was.views

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.ChatAdapter
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.polarlooptheory.was.MainActivity
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.model.mMessage
import com.polarlooptheory.was.model.mScenario
import com.polarlooptheory.was.views.character.equipment.MoneyFragment
import com.polarlooptheory.was.views.character.statistics.*
import com.polarlooptheory.was.views.lists.*
import com.polarlooptheory.was.views.lists.custom.abilities.CustomFeatureListFragment
import com.polarlooptheory.was.views.lists.custom.abilities.CustomLanguageListFragment
import com.polarlooptheory.was.views.lists.custom.abilities.CustomProficienciesListFragment
import com.polarlooptheory.was.views.lists.custom.abilities.CustomTraitsListFragment
import com.polarlooptheory.was.views.lists.custom.equipment.*
import com.polarlooptheory.was.views.lists.custom.magic.CustomMagicSchoolsListFragment
import com.polarlooptheory.was.views.lists.custom.magic.CustomSpellsListFragment
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.android.synthetic.main.password_change.*
import kotlinx.android.synthetic.main.scenario.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScenarioFragment : Fragment(), NavigationHost, Scenario.ISocketListener {

    private lateinit var chatRecyclerView: RecyclerView
    private var isUp = false
    private lateinit var chat: LinearLayout
    private lateinit var mainView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scenario, container, false)
        mainView = view
        Scenario.joinWebsocket(Scenario.connectedScenario.scenario,this)
        makeDrawer(activity as Activity)
        initChat(context)
        return view
    }

    private fun makeDrawer(activity: Activity) {
        val drawer = (activity as NavigationHost).getDrawer()
        val logout = PrimaryDrawerItem().withIdentifier(1).withName("Logout")
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    drawer.resetDrawerContent()
                    drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
                    drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    Login.logout()
                    (activity as NavigationHost).navigateTo(StartScreenFragment(), false)
                    return false
                }
            })

        val leave = PrimaryDrawerItem().withIdentifier(33).withName("Return to list")
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    Login.logout()
                    (activity as NavigationHost).navigateTo(StartScreenFragment(), false)
                    return false
                }
            })
        val characters = PrimaryDrawerItem().withIdentifier(2).withName("Characters")
            .withDescription("Choose character...")
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    navigateTo(CharacterListFragment(), false)
                    return false
                }
            })
        val roll = PrimaryDrawerItem().withIdentifier(3).withName("Roll")
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    navigateTo(RollFragment(), false)
                    return false
                }
            })
        val notes = PrimaryDrawerItem().withIdentifier(4).withName("Notes")
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    navigateTo(NoteListFragment(), false)
                    return false
                }
            })
        val statistics = ExpandableDrawerItem().withName("Statistics").withSubItems(
            SecondaryDrawerItem().withIdentifier(5).withName("Base Info").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(CharacterBaseInfoFragment(), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(6).withName("Background").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(CharacterBackgroundFragment(), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(7).withName("Stats").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(CharacterStatsFragment(), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(8).withName("Health").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(CharacterHealthFragment(), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(9).withName("Proficiencies").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(CharacterProficiencyFragment(), false)
                        return false
                    }
                })
        )
        val abilities = ExpandableDrawerItem().withName("Abilities").withSubItems(
            SecondaryDrawerItem().withIdentifier(10).withName("Features").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(10), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(11).withName("Languages").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(11), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(12).withName("Traits").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(12), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(13).withName("Proficiencies").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(13), false)
                        return false
                    }
                })
        )
        val equipment = ExpandableDrawerItem().withName("Equipment").withSubItems(
            SecondaryDrawerItem().withIdentifier(14).withName("Weapons").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(14), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(15).withName("Armors").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(15), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(16).withName("Gear").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(16), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(17).withName("Tools").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(17), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(18).withName("Vehicles").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(TabListFragment(18), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(19).withName("Money").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(MoneyFragment(), false)
                        return false
                    }
                })

        )
        val magic = PrimaryDrawerItem().withIdentifier(20).withName("Magic")
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    navigateTo(TabListFragment(20), false)
                    return false
                }
            })

        val management = ExpandableDrawerItem().withName("Game Management").withSubItems(
            ExpandableDrawerItem().withName("Custom Abilities").withSubItems(
                SecondaryDrawerItem().withIdentifier(21).withName("Features").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomFeatureListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(22).withName("Languages").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomLanguageListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(23).withName("Traits").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomTraitsListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(24).withName("Proficiencies").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomProficienciesListFragment(), false)
                            return false
                        }
                    })
            ),
            ExpandableDrawerItem().withName("Custom Equipment").withSubItems(
                SecondaryDrawerItem().withIdentifier(25).withName("Weapons").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomWeaponsListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(26).withName("Armors").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomArmorsListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(27).withName("Gear").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomGearListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(28).withName("Tools").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomToolsListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(29).withName("Vehicles").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomVehicleListFragment(), false)
                            return false
                        }
                    })
            ),
            ExpandableDrawerItem().withName("Custom Magic").withSubItems(
                SecondaryDrawerItem().withIdentifier(30).withName("Spells").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomSpellsListFragment(), false)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withIdentifier(31).withName("Magic Schools").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            navigateTo(CustomMagicSchoolsListFragment(), false)
                            return false
                        }
                    })
            ),
            SecondaryDrawerItem().withIdentifier(32).withName("Players").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        navigateTo(PlayerListFragment(), false)
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(34).withName("Show Key").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        val alert = AlertDialog.Builder(activity)
                        alert.setTitle("Scenario code")
                        alert.setMessage(Scenario.connectedScenario.scenario.scenarioKey)
                        alert.setNeutralButton("OK", null)
                        val dialog: AlertDialog = alert.create()
                        dialog.show()
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(35).withName("Change Password").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        val alert = AlertDialog.Builder(activity)
                        alert.setTitle("Scenario code")
                        alert.setView(View(activity).findViewById<LinearLayout>(R.id.passwordChange))
                        alert.setPositiveButton("OK") { _, _ ->
                            if (editPassword1.text.toString() == editPassword2.text.toString()) {
                                GlobalScope.launch(Dispatchers.Main) {
                                    val req = async {
                                        Scenario.GM.changeScenarioPassword(
                                            Scenario.connectedScenario.scenario,
                                            editPassword1.text.toString()
                                        )
                                    }.await()
                                    if (!req) {
                                        (activity as MainActivity).makeToast(Settings.error_message)
                                        Settings.error_message = ""
                                    }
                                }
                            }
                        }
                        val dialog: AlertDialog = alert.create()
                        dialog.show()
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(36).withName("Delete Scenario").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        val alert = AlertDialog.Builder(activity)
                        alert.setTitle("Delete scenario?")
                        alert.setMessage("Are you sure you want to delete this scenario?")
                        alert.setPositiveButton("Yes") { _, _ ->
                            GlobalScope.launch(Dispatchers.Main) {
                                val req =
                                    async { Scenario.GM.deleteScenario(Scenario.connectedScenario.scenario) }.await()
                                if (req) {
                                    Scenario.clear()
                                    (activity as NavigationHost).navigateTo(
                                        ScenarioListFragment(),
                                        false
                                    )
                                }
                            }
                        }
                        alert.setNegativeButton(
                            "No",
                            DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() })
                        val dialog: AlertDialog = alert.create()
                        dialog.show()
                        return false
                    }
                })
        )


        val headerResult = AccountHeaderBuilder()
            .withActivity(activity)
            .addProfiles(
                ProfileDrawerItem().withName(User.username)
            )
            .build()
        drawer.resetDrawerContent()
        drawer.setHeader(headerResult.view, true)
        drawer.setToolbar(activity, (activity as MainActivity).toolbar)
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
        drawer.onDrawerItemClickListener = object : Drawer.OnDrawerItemClickListener {
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
        if (User.username == Scenario.connectedScenario.scenario.gameMaster)
            drawer.addItemAtPosition(management, 9)
    }

    private fun initChat(context: Context?) {
        val dataset = arrayOf(
            mMessage(content = "testowa wiadomość", sender = "admin", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość systemowa", type = mMessage.Type.SYSTEM),
            mMessage(content = "wiadomość gracza", sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza", sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza", sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza", sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza", sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(content = "wiadomość gracza", sender = "Gracz", type = mMessage.Type.OOC),
            mMessage(
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec lacus ex, dapibus et ipsum ut, pellentesque convallis augue. Proin at.",
                sender = "Gracz",
                type = mMessage.Type.OOC
            ),
            mMessage(
                content = "wiadomość postaci",
                sender = "Postać",
                type = mMessage.Type.CHARACTER
            ),
            mMessage(
                content = "wiadomość prywatna",
                sender = "Postać",
                type = mMessage.Type.WHISPER
            )
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
            if (!isUp) slideUp()
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

    override fun OnReloadPlayers(scenario: mScenario) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnReloadCharacters(scenario: mScenario) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnReloadGM(scenario: mScenario) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnKick(scenario: mScenario) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnPrivateMessageRecieved(scenario: mScenario, message: mMessage) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnMessageRecieved(scenario: mScenario, message: mMessage) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnErrorRecieved(scenario: mScenario) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
