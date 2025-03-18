package com.twt.launcheros.ui.home.homeScreenFragment

import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.koai.base.main.action.router.BaseRouter
import com.koai.base.main.extension.safeClick
import com.twt.launcheros.R
import com.twt.launcheros.databinding.FragmentHomeScreenBinding
import com.twt.launcheros.ui.IScreen
import com.twt.launcheros.utils.openAlarmApp
import com.twt.launcheros.utils.openCalendar
import com.twt.launcheros.utils.openSearch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeScreenFragment :
    IScreen<FragmentHomeScreenBinding, BaseRouter>(R.layout.fragment_home_screen) {
    override fun initView(
        savedInstanceState: Bundle?,
        binding: FragmentHomeScreenBinding,
    ) {
        onClick()
    }

    override fun onResume() {
        super.onResume()
        genDate()
    }

    private fun genDate() {
        try {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
            val dateText = dateFormat.format(Date())
            binding.date.text = dateText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onClick() {
        binding.clock.safeClick {
            openAlarmApp(activity)
        }
        binding.date.safeClick {
            openCalendar(activity)
        }
        binding.searchBarContainer.safeClick {
            openSearch(activity)
        }
    }

    companion object {
        fun newInstance(items: List<ApplicationInfo>): HomeScreenFragment {
            val args = Bundle()
            val fragment = HomeScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
