package com.arifian.training.liburansemarang

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.arifian.training.liburansemarang.Utils.PreferenceUtils
import com.arifian.training.liburansemarang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding

    lateinit var pref: PreferenceUtils
    var doubleBackToExitPressedOnce: Boolean = false

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

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
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
