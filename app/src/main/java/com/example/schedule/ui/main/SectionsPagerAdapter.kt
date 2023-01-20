package com.example.schedule.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.schedule.R

private val TAB_TITLES = arrayOf(
    R.string.day_1,
    R.string.day_2,
    R.string.day_3,
    R.string.day_4,
    R.string.day_5,
    R.string.day_6
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return CreateLessons.newInstance(position + 1);
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total  pages.
        return TAB_TITLES.size;
    }
}