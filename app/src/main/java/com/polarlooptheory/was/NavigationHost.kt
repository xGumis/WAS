package com.polarlooptheory.was

import androidx.fragment.app.Fragment
import com.mikepenz.materialdrawer.Drawer

interface NavigationHost {
    fun navigateTo(fragment: Fragment, addToBackStack: Boolean)
    fun getDrawer(): Drawer
}