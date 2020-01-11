package com.polarlooptheory.was

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), NavigationHost, CoroutineScope by MainScope(){
    lateinit var toolbar: Toolbar
    lateinit var drwr: Drawer
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
        drwr = DrawerBuilder().withActivity(this).build()
        drwr.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        supportFragmentManager.beginTransaction().add(R.id.main,
            StartScreenFragment()
        ).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
