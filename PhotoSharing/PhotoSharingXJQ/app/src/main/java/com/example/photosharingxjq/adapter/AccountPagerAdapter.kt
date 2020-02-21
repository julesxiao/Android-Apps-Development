package com.example.photosharingxjq.adapter

import android.support.v4.app.FragmentPagerAdapter
import android.content.Context
import android.support.v4.app.FragmentManager
import com.example.photosharingxjq.R
import com.example.photosharingxjq.fragment.CreateAccountFragment
import com.example.photosharingxjq.fragment.SignInFragment

class AccountPagerAdapter(private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): android.support.v4.app.Fragment? {
        return if (p0 == 0) {
            CreateAccountFragment(context)
        } else {
            SignInFragment(context)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.create_account)
        } else {
            context.getString(R.string.sign_in)
        }
    }
}