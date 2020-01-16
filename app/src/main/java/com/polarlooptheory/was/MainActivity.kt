package com.polarlooptheory.was

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.polarlooptheory.was.apiCalls.Login
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.views.ScenarioFragment
import com.polarlooptheory.was.views.lists.ScenarioListFragment
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), NavigationHost, CoroutineScope by MainScope(){
    lateinit var toolbar: Toolbar
    lateinit var drwr: Drawer
    lateinit var header: AccountHeader
    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction().replace(R.id.main, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun getDrawer(): Drawer {
        return drwr
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        header = AccountHeaderBuilder().withActivity(this).withOnAccountHeaderListener(object : AccountHeader.OnAccountHeaderListener {
            override fun onProfileChanged(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
                if(supportFragmentManager.fragments.first() is ScenarioFragment){
                    Scenario.connectedScenario.chosenCharacter = Scenario.connectedScenario.charactersList[profile.name.toString()]
                }
                return false
            }
        }).build()
        drwr = DrawerBuilder().withActivity(this).withAccountHeader(header,true).buildForFragment()
        drwr.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        supportFragmentManager.beginTransaction().add(R.id.main,
            StartScreenFragment()
        ).commit()
    }

    fun makeToast(msg: String){
        runOnUiThread {
            Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        val sfm = supportFragmentManager
        val cfm = supportFragmentManager.fragments.firstOrNull()?.childFragmentManager
        if( cfm!=null && cfm.backStackEntryCount>0){
                cfm.popBackStackImmediate()
        }
        else if(supportFragmentManager.backStackEntryCount>0){
            sfm.popBackStackImmediate()
        }
        else if(supportFragmentManager.fragments.firstOrNull() is ScenarioFragment){
            Scenario.clear()
            navigateTo(ScenarioListFragment(),false)
        }
        else if(supportFragmentManager.fragments.firstOrNull() is ScenarioListFragment){
            Login.logout()
            navigateTo(StartScreenFragment(),false)
        }
        else{
            super.onBackPressed()
        }
    }
}
