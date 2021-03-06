package com.polarlooptheory.was.views

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.ChatAdapter
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
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
import kotlinx.android.synthetic.main.password_change.view.*
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
        navigateTo(CharacterListFragment(),false)
        makeDrawer(activity as Activity)
        initChat()
        Scenario.joinWebsocket(Scenario.connectedScenario.scenario,this)
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
                    drawer.removeAllItems()
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
                    Scenario.clear()
                    (activity as NavigationHost).navigateTo(ScenarioListFragment(), false)
                    return false
                }
            })
        val key = PrimaryDrawerItem().withIdentifier(34).withName("Show Key").withOnDrawerItemClickListener(
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
                })
        val charactersItem = PrimaryDrawerItem().withIdentifier(2).withName("Characters")
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
                        if(Scenario.connectedScenario.chosenCharacter!=null){
                            Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter
                            navigateTo(CharacterBaseInfoFragment(false), false)
                        }
                        else (activity as MainActivity).makeToast("Character is not chosen")
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
                        if(Scenario.connectedScenario.chosenCharacter!=null){
                            Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter
                            navigateTo(CharacterBackgroundFragment(false), false)
                        }
                        else (activity as MainActivity).makeToast("Character is not chosen")
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
                        if(Scenario.connectedScenario.chosenCharacter!=null){
                            Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter
                            navigateTo(CharacterStatsFragment(false), false)
                        }
                        else (activity as MainActivity).makeToast("Character is not chosen")
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
                        if(Scenario.connectedScenario.chosenCharacter!=null){
                            Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter
                            navigateTo(CharacterHealthFragment(false), false)
                        }
                        else (activity as MainActivity).makeToast("Character is not chosen")
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
                        if(Scenario.connectedScenario.chosenCharacter!=null){
                            Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter
                            navigateTo(CharacterProficiencyFragment(false), false)
                        }
                        else (activity as MainActivity).makeToast("Character is not chosen")
                        return false
                    }
                }),
            SecondaryDrawerItem().withIdentifier(666).withName("Magic").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        if(Scenario.connectedScenario.chosenCharacter!=null){
                            Scenario.dummyCharacter = Scenario.connectedScenario.chosenCharacter
                            navigateTo(CharacterMagicFragment(false), false)
                        }
                        else (activity as MainActivity).makeToast("Character is not chosen")
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
                        if(Scenario.connectedScenario.chosenCharacter!=null)
                            navigateTo(MoneyFragment(), false)
                        else (activity as MainActivity).makeToast("Character is not chosen")
                        return false
                    }
                })

        )
        val magic = PrimaryDrawerItem().withIdentifier(20).withName("Spells")
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
            SecondaryDrawerItem().withIdentifier(35).withName("Change Password").withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(
                        view: View?,
                        position: Int,
                        drawerItem: IDrawerItem<*>
                    ): Boolean {
                        val alert = AlertDialog.Builder(activity)
                        alert.setTitle("Change password")
                        val v = LayoutInflater.from(context).inflate(R.layout.password_change,null)
                        alert.setView(v)
                        alert.setPositiveButton("OK") { _, _ ->
                            val pass = v.editPassword1.text
                            val cpass = v.editPassword2.text
                            if (pass.toString() == cpass.toString()) {
                                val password = pass?.toString() ?: ""
                                GlobalScope.launch(Dispatchers.Main) {
                                    val req = async {
                                        Scenario.GM.changeScenarioPassword(
                                            Scenario.connectedScenario.scenario,
                                            password
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
                            DialogInterface.OnClickListener { dialogInterface: DialogInterface, _ -> dialogInterface.cancel() })
                        val dialog: AlertDialog = alert.create()
                        dialog.show()
                        return false
                    }
                })
        )

        GlobalScope.launch(Dispatchers.Main) {
            val req = async{Scenario.getCharacters(Scenario.connectedScenario.scenario)}.await()
            if(req){
                val hdr = (activity as MainActivity).header
                hdr.clear()
                Scenario.connectedScenario.chosenCharacter = Scenario.connectedScenario.charactersList.values.firstOrNull()
                Scenario.connectedScenario.charactersList.keys.forEach { hdr.addProfiles(ProfileDrawerItem().withName(it)) }
            }
            else{
                (activity as MainActivity).makeToast(Settings.error_message)
                Settings.error_message = ""
            }
        }
        drawer.removeAllItems()
        drawer.setToolbar(activity, (activity as MainActivity).toolbar)
        drawer.addItems(
            logout,
            leave,
            key,
            charactersItem,
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

    private fun initChat() {
        val list = mutableListOf<mMessage>()
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = ChatAdapter(list)
        chatRecyclerView = mainView.messageList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        GlobalScope.launch(Dispatchers.Main) {
            val req = async{Scenario.getMessages(Scenario.connectedScenario.scenario)}.await()
            if(req){
                list.addAll(Scenario.connectedScenario.messagesList.reversed())
                chatRecyclerView.adapter?.notifyDataSetChanged()
            }
            else {(activity as MainActivity).makeToast(Settings.error_message);Settings.error_message = ""}
        }
        chat = mainView.animatedLayout
        chat.post { chat.translationY = chat.chatLayout.height.toFloat() }
        chat.chatButton.setOnClickListener {
            if (!isUp) slideUp()
            else slideDown()
            isUp = !isUp
        }
        chat.buttonSend.setOnClickListener {
            val text = chat.messageText.text.toString().trim()
            val character = Scenario.connectedScenario.chosenCharacter
            GlobalScope.launch {
                val req = async {
                    Scenario.sendMessage(
                        Scenario.connectedScenario.scenario,
                        character,
                        text
                    )
                }.await()
                if (!req) {
                    (activity as MainActivity).makeToast(Settings.error_message)
                    Settings.error_message = ""
                }
            }
            chat.messageText.text?.clear()
            (context as MainActivity).hideKeyboard()
        }
    }

    private fun slideUp() {
        chat.animate().translationY(0f).setDuration(500).start()
    }

    private fun slideDown() {
        (context as MainActivity).hideKeyboard()
        val h = chat.chatLayout.height.toFloat()
        chat.animate().translationY(h).setDuration(500).start()
    }

    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = childFragmentManager.beginTransaction().replace(R.id.container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        else for(x in 1..childFragmentManager.backStackEntryCount) childFragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
        (context as MainActivity).hideKeyboard()
        transaction.commit()
    }

    override fun getDrawer(): Drawer {
        return (activity as NavigationHost).getDrawer()
    }

    override fun OnReloadPlayers(scenario: mScenario) {
        GlobalScope.launch(Dispatchers.Main) {
            val req = async{Scenario.getPlayers(Scenario.connectedScenario.scenario)}.await()
            if(req){
                if(childFragmentManager.fragments.first() is PlayerListFragment){
                    (childFragmentManager.fragments.first() as PlayerListFragment).adapter.notifyDataSetChanged()
                }
                chatRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun OnReloadCharacters(scenario: mScenario) {
        GlobalScope.launch(Dispatchers.Main) {
        val hdr = (activity as MainActivity).header
        hdr.clear()
        Scenario.connectedScenario.charactersList.keys.forEach { hdr.addProfiles(ProfileDrawerItem().withName(it)) }
        val chosenProfile = hdr.profiles?.find{it.name.toString() == Scenario.connectedScenario.chosenCharacter?.name}
        if(chosenProfile!=null)
            hdr.setActiveProfile(chosenProfile,false)
        else{
            Scenario.connectedScenario.chosenCharacter = Scenario.connectedScenario.charactersList.values.firstOrNull()
        }
            if(childFragmentManager.fragments.first() is CharacterListFragment){
                (childFragmentManager.fragments.first() as CharacterListFragment).adapter.notifyDataSetChanged()
            }
        }
    }

    override fun OnReloadGM(scenario: mScenario) {
        Scenario.clear()
        (activity as NavigationHost).navigateTo(ScenarioListFragment(),false)
    }

    override fun OnKick(scenario: mScenario) {
        Scenario.clear()
        (activity as NavigationHost).navigateTo(ScenarioListFragment(),false)
    }

    override fun OnPrivateMessageRecieved(scenario: mScenario, message: mMessage) {
        GlobalScope.launch(Dispatchers.Main) {
            (chatRecyclerView.adapter as ChatAdapter).messageList.add(message)
            chatRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun OnMessageRecieved(scenario: mScenario, message: mMessage) {
        GlobalScope.launch(Dispatchers.Main) {
        (chatRecyclerView.adapter as ChatAdapter).messageList.add(message)
        chatRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun OnErrorRecieved(scenario: mScenario) {
        (activity as MainActivity).makeToast(Settings.error_message)
        Settings.error_message = ""
    }
}
