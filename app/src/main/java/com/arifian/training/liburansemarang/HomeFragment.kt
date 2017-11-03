package com.arifian.training.liburansemarang


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.arifian.training.liburansemarang.Utils.Constants
import com.arifian.training.liburansemarang.Utils.Constants.Companion.KEY_WISATA
import com.arifian.training.liburansemarang.Utils.PreferenceUtils
import com.arifian.training.liburansemarang.adapters.WisataAdapter
import com.arifian.training.liburansemarang.databinding.FragmentHomeBinding
import com.arifian.training.liburansemarang.models.Wisata
import com.arifian.training.liburansemarang.models.remote.SimpleRetrofitCallback
import com.arifian.training.liburansemarang.models.remote.responses.WisataResponse
import org.parceler.Parcels
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var pref: PreferenceUtils

    internal lateinit var mBinding: FragmentHomeBinding

    internal var wisataArrayList: MutableList<Wisata> = ArrayList()
    internal lateinit var adapter: WisataAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater!!, container, false)
        setHasOptionsMenu(true)

        pref = PreferenceUtils(activity)

        adapter = WisataAdapter(wisataArrayList, object : WisataAdapter.OnWisataClickListener {
            override fun onItemClick(wisata: Wisata) {
                val intent = Intent(activity, DetailWisataActivity::class.java)
                intent.putExtra(KEY_WISATA, Parcels.wrap(wisata))
                startActivity(intent)
            }
        })

        mBinding.recyclerView.adapter = adapter

        if(savedInstanceState != null){
            wisataArrayList = Parcels.unwrap(savedInstanceState.getParcelable(Constants.KEY_WISATA))
        }else {
            getWisata()
        }

        return mBinding.root
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
                    getWisata()
                }
            }
            R.id.action_sort_latest -> {
                if (pref.sort() != PreferenceUtils.SORT_LATEST) {
                    getWisata()
                }
            }
            R.id.action_sort_visited -> {
                if (pref.sort() != PreferenceUtils.SORT_VISITED) {
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
        wisataArrayList.clear()
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
                        wisataArrayList.addAll(response.wisata!!)
                        adapter.swapData(wisataArrayList)
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(Constants.KEY_WISATA, Parcels.wrap(wisataArrayList))
    }
}
