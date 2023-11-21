package com.example.courseer2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Favorites : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_favorites, container, false)
        viewPager = rootView.findViewById(R.id.viewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout)

        val adapter = CombinedFavorites(childFragmentManager, lifecycle)
        adapter.addFragment(SavedProgram.newInstance2(true), "Programs")
        adapter.addFragment(SavedScholarship.newInstance2(false), "Scholarships")

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getFragmentTitle(position)
        }.attach()

        return rootView
    }
}
