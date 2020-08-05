package com.example.gitusers.follow

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.gitusers.R

class FollowPagerAdapter(private val context: Context,
                         fm: FragmentManager,
                         private val followersUrl   : String,
                         private val followingUrl   : String,
                         private val followersCount : Int,
                         private val followingCount : Int):
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val tabTitles = intArrayOf(R.string.followers_template, R.string.following_template)

    override fun getItem(position: Int): Fragment {
        return if (position == 0)
            FollowFragment.newInstance(followersUrl, followersCount, true)
        else
            FollowFragment.newInstance(followingUrl, followingCount, false)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0)
            context.resources.getString(tabTitles[position], followersCount)
        else
            context.resources.getString(tabTitles[position], followingCount)

    }
    override fun getCount(): Int = tabTitles.size


}