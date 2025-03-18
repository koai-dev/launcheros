package com.twt.launcheros.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.twt.launcheros.ui.IScreen

class HomePagerAdapter(fragment: Fragment, private val listFragment: List<IScreen<*, *>>) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = listFragment.size

    override fun createFragment(position: Int) = listFragment[position]
}
