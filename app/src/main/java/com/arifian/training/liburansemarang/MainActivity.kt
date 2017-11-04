package com.arifian.training.liburansemarang

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.arifian.training.liburansemarang.Utils.PreferenceUtils
import com.arifian.training.liburansemarang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding

    lateinit var pref: PreferenceUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = PreferenceUtils(this)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.toolbar)

        mBinding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)

        mBinding.fab.setOnClickListener{
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    class ViewPagerAdapter(val fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager){
        var fragments = arrayOf<Fragment>()
        init{
            fragments = arrayOf(
                    HomeFragment.newInstance(),
                    MapFragment.newInstance()
            )
        }

        override fun getPageTitle(position: Int): CharSequence {
            return if(position == 1)
                "Map"
            else
                "Wisata"
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
}
