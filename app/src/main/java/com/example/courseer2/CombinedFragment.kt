package com.example.courseer2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CombinedFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_combined, container, false)

        viewPager = rootView.findViewById(R.id.viewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout)

        val fragmentList = listOf(AdminFragment(), AdminProgram(), AddTagFragment())
        val adapter = CombinedPagerAdapter(this, fragmentList)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Scholarships"
                1 -> tab.text = "Programs"
                2 -> tab.text = "Add Keywords"
            }
        }.attach()

        return rootView
    }
}
