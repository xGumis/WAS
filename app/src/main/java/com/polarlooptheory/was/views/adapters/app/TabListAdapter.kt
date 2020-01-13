package com.polarlooptheory.was.views.adapters.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.polarlooptheory.was.views.character.abilities.CharacterFeaturesFragment
import com.polarlooptheory.was.views.character.abilities.CharacterLanguagesFragment
import com.polarlooptheory.was.views.character.abilities.CharacterProficienciesFragment
import com.polarlooptheory.was.views.character.abilities.CharacterTraitsFragment
import com.polarlooptheory.was.views.character.equipment.*
import com.polarlooptheory.was.views.character.spells.SpellListFragment
import com.polarlooptheory.was.views.lists.database.BaseVehicleListFragment

class TabListAdapter(fm:FragmentManager, private val id: Int) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (id) {
            10 -> when (position) {
                0 -> CharacterFeaturesFragment()
                else -> BaseVehicleListFragment()
            }
            11 -> when (position) {
                0 -> CharacterLanguagesFragment()
                else -> BaseVehicleListFragment()
            }
            12 -> when (position) {
                0 -> CharacterTraitsFragment()
                else -> BaseVehicleListFragment()
            }
            13 -> when (position) {
                0 -> CharacterProficienciesFragment()
                else -> BaseVehicleListFragment()
            }
            14 -> when (position) {
                0 -> WeaponListFragment()
                else -> BaseVehicleListFragment()
            }
            15 -> when (position) {
                0 -> ArmorListFragment()
                else -> BaseVehicleListFragment()
            }
            16 -> when (position) {
                0 -> GearListFragment()
                else -> BaseVehicleListFragment()
            }
            17 -> when (position) {
                0 -> ToolListFragment()
                else -> BaseVehicleListFragment()
            }
            18 -> when (position) {
                0 -> VehicleListFragment()
                else -> BaseVehicleListFragment()
                }

            else -> when (position) {
                0 -> SpellListFragment()
                else -> BaseVehicleListFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "CHARACTER"
            else -> "ALL"
        }
    }
}