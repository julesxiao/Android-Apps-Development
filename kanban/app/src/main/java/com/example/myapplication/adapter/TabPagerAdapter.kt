package com.example.myapplication.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.myapplication.R
import com.example.myapplication.fragment.*

class TabPagerAdapter (private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment {
        return if (p0 == 0) {
            TodoFragment(context)
        } else if(p0==1){
            DoingFragment(context)
        }else{
            DoneFragment(context)
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            "TO DO"
        } else if(position == 1) {
            "DOING"
        } else{
            "DONE"
        }
    }
}