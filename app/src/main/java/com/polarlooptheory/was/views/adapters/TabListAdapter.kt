package com.polarlooptheory.was.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.polarlooptheory.was.views.lists.BaseVehicleListFragment
import com.polarlooptheory.was.views.lists.VehicleListFragment

class TabListAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> VehicleListFragment()
            else -> BaseVehicleListFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "STASH"
            else -> "ALL"
        }
    }
}