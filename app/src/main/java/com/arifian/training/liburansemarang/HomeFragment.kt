package com.arifian.training.liburansemarang


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import com.arifian.training.liburansemarang.Utils.Constants.Companion.KEY_WISATA
import com.arifian.training.liburansemarang.Utils.PreferenceUtils
import com.arifian.training.liburansemarang.Utils.PreferenceUtils.Companion.SORT_FAVORITE
import com.arifian.training.liburansemarang.Utils.PreferenceUtils.Companion.SORT_LATEST
import com.arifian.training.liburansemarang.Utils.PreferenceUtils.Companion.SORT_VISITED
import com.arifian.training.liburansemarang.adapters.WisataAdapter
import com.arifian.training.liburansemarang.database.DBHelper
import com.arifian.training.liburansemarang.databinding.FragmentHomeBinding
import com.arifian.training.liburansemarang.models.Header
import com.arifian.training.liburansemarang.models.Item
import com.arifian.training.liburansemarang.models.Wisata
import com.arifian.training.liburansemarang.models.remote.SimpleRetrofitCallback
import com.arifian.training.liburansemarang.models.remote.responses.WisataResponse
import org.parceler.Parcels


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var pref: PreferenceUtils

    internal lateinit var mBinding: FragmentHomeBinding

//    internal var wisataArrayList: MutableList<Wisata> = ArrayList()
    var list: ArrayList<Item> = ArrayList()
    internal lateinit var adapter: WisataAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater!!, container, false)
        setHasOptionsMenu(true)

        pref = PreferenceUtils(activity)

        adapter = WisataAdapter(list, object : WisataAdapter.OnWisataClickListener {
            override fun onItemClick(wisata: Wisata) {
                val intent = Intent(activity, DetailWisataActivity::class.java)
                intent.putExtra(KEY_WISATA, Parcels.wrap(wisata))
                startActivity(intent)
            }
        })

        var layoutManager = GridLayoutManager(activity, activity.resources.getInteger(R.integer.wisata_list_column))
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (adapter.getItemViewType(position)) {
                    Item.TYPE_HEADER -> return activity.resources.getInteger(R.integer.wisata_list_column)
                    Item.TYPE_ITEM -> return 1
                    else -> return -1
                }
            }
        }

        mBinding.recyclerView.layoutManager = layoutManager
        mBinding.recyclerView.adapter = adapter

//        if(savedInstanceState != null){
//            wisataArrayList = Parcels.unwrap(savedInstanceState.getParcelable(Constants.KEY_WISATA))
//        }

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
//        if(wisataArrayList.size <= 0){
            getWisata()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        Log.e("sort", pref.sort())

        when (id) {
            R.id.action_sort_favorite -> {
                if (pref.sort() != PreferenceUtils.SORT_FAVORITE) {
                    pref.sort(SORT_FAVORITE)
                    getWisata()
                }
            }
            R.id.action_sort_latest -> {
                if (pref.sort() != PreferenceUtils.SORT_LATEST) {
                    pref.sort(SORT_LATEST)
                    getWisata()
                }
            }
            R.id.action_sort_visited -> {
                if (pref.sort() != PreferenceUtils.SORT_VISITED) {
                    pref.sort(SORT_VISITED)
                    getWisata()
                }
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun getWisata() {
        list.clear()
//        wisataArrayList.clear()
        val progress = ProgressDialog(activity)
        progress.isIndeterminate = true
        progress.setMessage("Loading")
        progress.setCancelable(false)
        progress.show()

        WisataApplication.get(activity)
                .getService(activity)
                .wisata(pref.sort())
                .enqueue(object : SimpleRetrofitCallback<WisataResponse>(activity, progress) {
                    override fun onSuccess(response: WisataResponse) {
                        val db = DBHelper(activity)
                        val wisataDB = db.query()
                        list.add(Header("Favorite ("+wisataDB.size+")"))
                        for(wisata: Wisata in wisataDB){
                            list.add(wisata)
                        }


                        val wisataRemote = response.wisata!!
                        val wisataNonFavorite = ArrayList<Wisata>()
                        for(wisata: Wisata in wisataRemote){
                            if(pref.isFavorite(wisata.idWisata!!))
                                continue
                            wisataNonFavorite.add(wisata)
                        }
                        list.add(Header("Tempat Wisata ("+wisataNonFavorite.size+")"))
                        list.addAll(wisataNonFavorite)
//                        wisataArrayList.addAll(response.wisata!!)
                        adapter.swapData(list)

                        if(list.size <= 0){
                            mBinding.empty.visibility = View.VISIBLE
                        }else{
                            mBinding.empty.visibility = View.GONE
                        }
                    }
                })
    }

    companion object {

        fun newInstance(): HomeFragment {

            val args = Bundle()

            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

//    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
//        outState!!.putParcelable(Constants.KEY_WISATA, Parcels.wrap(list))
//    }
}
