package com.polarlooptheory.was.views.lists.custom.abilities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.polarlooptheory.was.NavigationHost
import com.polarlooptheory.was.R
import com.polarlooptheory.was.model.abilities.mFeature
import com.polarlooptheory.was.views.adapters.abilities.custom.CustomFeatureListAdapter
import com.polarlooptheory.was.views.custom.abilities.CustomFeatureFragment
import kotlinx.android.synthetic.main.characters.view.*

class CustomFeatureListFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomFeatureListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.characters, container, false)
        view.buttonAddChar.text = "ADD FEATURE"
        view.buttonAddChar.setOnClickListener {
            (parentFragment as NavigationHost).navigateTo(CustomFeatureFragment(),false)
        }
        val featureList = listOf(
            mFeature("Niestandardowy pojazd","Jeździ bardzo szybko",true,true)
        )
        linearLayoutManager = LinearLayoutManager(activity)
        view.char_list.layoutManager = linearLayoutManager
        adapter =
            CustomFeatureListAdapter(
                featureList
            )
        view.char_list.adapter = adapter
        return view
    }
}