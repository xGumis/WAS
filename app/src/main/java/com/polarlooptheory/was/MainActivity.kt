package com.polarlooptheory.was

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.polarlooptheory.was.views.start.StartScreenFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), NavigationHost, CoroutineScope by MainScope(){
    lateinit var toolbar: Toolbar
    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction().replace(R.id.main, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().add(R.id.main,
            StartScreenFragment()
        ).commit()
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }
}
