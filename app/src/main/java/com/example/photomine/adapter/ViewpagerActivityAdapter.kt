package com.example.photomine.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.photomine.fragment.CollectionFragment
import com.example.photomine.fragment.SettingFragment

class ViewpagerActivityAdapter (
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private var collectionFragment: CollectionFragment? = null
    private var settingFragment: SettingFragment? = null

    fun setFragments(
        collectionFragment: CollectionFragment, settingFragment: SettingFragment
    ) {
        this.collectionFragment = collectionFragment
        this.settingFragment = settingFragment
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> collectionFragment!!
            1 -> settingFragment!!
            else -> collectionFragment!!
        }

    }
}