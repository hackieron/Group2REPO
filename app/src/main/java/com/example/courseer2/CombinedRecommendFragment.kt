package com.example.courseer2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CombinedRecommendFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_combined_recommend, container, false)
        viewPager = rootView.findViewById(R.id.viewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout)

        val adapter = CombinedTabAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(Recommend.newInstance(true), "Strand Based")
        adapter.addFragment(Recommend2.newInstance(false), "Non-Strand Based")

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getFragmentTitle(position)
        }.attach()

        return rootView
    }
}
