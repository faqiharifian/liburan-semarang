package com.arifian.training.liburansemarang

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.arifian.training.liburansemarang.Utils.PreferenceUtils
import com.arifian.training.liburansemarang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mBinding: ActivityMainBinding

    lateinit var pref: PreferenceUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = PreferenceUtils(this)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.toolbar)

//        val toggle = ActionBarDrawerToggle(
//                this, mBinding.drawerLayout, mBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        mBinding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

//        mBinding.navView.setNavigationItemSelectedListener(this)
//        mBinding.navView.setCheckedItem(R.id.nav_home)

        mBinding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)

        mBinding.fab.setOnClickListener{
            startActivity(Intent(this, AddActivity::class.java))
        }

//        supportFragmentManager.beginTransaction()
//                .replace(mBinding.container.id, HomeFragment.newInstance())
//                .commit()
    }

    override fun onBackPressed() {
//        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START)
//        } else {
//            supportActionBar!!.title = getString(R.string.app_name)
//            mBinding.navView.setCheckedItem(R.id.nav_home)
            super.onBackPressed()
//        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        val id = item.itemId

        var fragment: Fragment? = null
        when (id) {
            R.id.nav_home -> {
                fragment = HomeFragment.newInstance()
                supportActionBar!!.title = getString(R.string.app_name)
            }
            R.id.nav_favorite -> {
                fragment = FavoriteFragment.newInstance()
                supportActionBar!!.title = "Favorite"
            }
            R.id.nav_map -> {
                fragment = MapFragment.newInstance()
                supportActionBar!!.title = "Map"
            }
            R.id.nav_add -> {
                fragment = CreateFragment.newInstance()
                supportActionBar!!.title = "Tambah"
            }
        }

//        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
//        drawer.closeDrawer(GravityCompat.START)

        if (fragment != null) {
//            supportFragmentManager.beginTransaction()
//                    .replace(mBinding.container.id, fragment)
//                    .addToBackStack(null)
//                    .commit()
        }
        return true
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
