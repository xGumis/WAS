package com.polarlooptheory.was.views.lists

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polarlooptheory.was.R
import com.polarlooptheory.was.views.adapters.app.TabListAdapter
import kotlinx.android.synthetic.main.tab_list.view.*


class TabListFragment(private val tabId: Int) : Fragment() {

    private lateinit var adapter: TabListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tab_list, container, false)
        adapter = TabListAdapter(
            childFragmentManager,
            tabId
        )
        val vp = view.viewPager
        vp.adapter = adapter
        view.tabs.setupWithViewPager(vp)
        return view
        }

}
