package com.covilyfe.v1r0.ui.notifications

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.covilyfe.v1r0.*
import com.covilyfe.v1r0.api.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.lang.IllegalStateException
import java.util.logging.Logger

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager
    var logy = Logger.getLogger("NotificationFragment")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        var lastEntId: Int = 0
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val geoSelect = GeolocationSelector()

        val reqrCtds = CTDSRequest()
        val reqrStrata = CoviApiStrataOut()
        val reqrStrataInc = CoviApiStrataOutIncrement()
        val jmapper = jacksonObjectMapper()

        lateinit var sharedPreferences : SharedPreferences

        sharedPreferences = requireContext().getSharedPreferences(
            "spinAmericaPref",
            Context.MODE_PRIVATE
        )

        root.progressBarNotificationsFrag.visibility = View.VISIBLE


        val recyclerView = root.findViewById<RecyclerView>(R.id.rvPublished)
        //viewManager = LinearLayoutManager(context)
        //viewAdapter = RecyclerAdapterStrataApi(ResponseAppStrata(), reqrStrata, context)
        //recyclerView.layoutManager = viewManager
        //recyclerView.adapter = viewAdapter

        //todo: Clean it up
        val spinAmerica = root.findViewById<Spinner>(R.id.spinnerAmericanState)
        val spinAmPrefIndex = resources.getStringArray(R.array.spinVectorOptionsAmericanStates)
            .indexOf(sharedPreferences.getString("spinAmericaPref", "Alabama"))

        if (spinAmerica != null) {
            val adapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.spinVectorOptionsAmericanStates)
            )
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinAmerica.adapter = adapter
            spinAmerica.setSelection(spinAmPrefIndex)
        }

        val callback = object : Callback<ResponseBody>{
            /** This us such nonsense. okhttp3 > retrofit > gson should be doing all this for us.
             * Instead we are getting a string and deserializing in the response callback.
             */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val bod = response.body()!!.string()
                val jobj = jmapper.readValue<ResponseCTDS>(bod)
                val pos = jobj.positive!! + 0.0
                val neg = jobj.negative!! + 0.0
                val positivity = pos / (pos+neg)

                try{
                    txtStatsReportDate.text = jobj.date.toString()
                    txtStatsTestsAdministered.text = jobj.totalTestResults.toString()
                    txtStatsTotalPositive.text = jobj.positive.toString()
                    txtStatsPositivty.text = getString(R.string.positivity_perc, positivity * 100)
                }catch (e:IllegalStateException){
                    logy.warning("[-] Null NotificationsFragment in response callback.")

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                logy.warning("[-] Response callback failure")
                Toast.makeText(context, "Failed to load state data", Toast.LENGTH_SHORT).show()
            }
        }

        spinAmerica.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Select a state, brah", Toast.LENGTH_SHORT).show()
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinSelected = root.spinnerAmericanState.selectedItem.toString()
                val spinSelectedAbbrev =  geoSelect.lookupAbbreviationByState(spinSelected.toUpperCase())

                sharedPreferences.edit().putString("spinAmericaPref", spinSelected).apply()
                if (spinSelectedAbbrev != null) {
                    //root.progressBarNotificationsFrag.visibility = View.VISIBLE
                    reqrCtds.request(callback, spinSelectedAbbrev)
                }
            }
        }


        val callbackStrataApi = object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println("---------------- Strata API Response --------------------")

                if(response.isSuccessful){
                    root.progressBarNotificationsFrag.visibility = View.GONE
                }else{
                    root.progressBarNotificationsFrag.visibility = View.GONE
                    Toast.makeText(context, "The bits are not flowing.", Toast.LENGTH_SHORT).show()
                    return
                }


                val bod = response.body()!!.string()
                val jmapper = jacksonObjectMapper()

                val jobj = jmapper.readValue<ResponseAppStrata>(bod)

                if(jobj.size >= 1){
                    println(bod)
                    //jobj.reverse()
                    lastEntId = jobj[jobj.size-1].entry.id
                    reqrStrata.setSize(jobj.size)
                    viewManager = LinearLayoutManager(context)
                    viewAdapter = RecyclerAdapterStrataApi(jobj, reqrStrata, context)
                    recyclerView.layoutManager = viewManager
                    recyclerView.adapter = viewAdapter
                }else{
                    return
                }

                //val recyclerView = root.findViewById<RecyclerView>(R.id.rvPublished)
                //recyclerView.layoutManager = viewManager
                //recyclerView.adapter = viewAdapter

/**
                //TODO: use for swipe to add. Functionality on pause.
                val swipeHandler = object : SwipeToFollowCallback(requireContext()){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        viewAdapter.notifyDataSetChanged()
                    }
                }

                val itemTouchHelper = ItemTouchHelper(swipeHandler)
                itemTouchHelper.attachToRecyclerView(recyclerView)
 **/




                root.progressBarNotificationsFrag.visibility = View.GONE
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                logy.warning("[-] Strata API Response callback failure")
                println(t)

                if(context != null){
                    Toast.makeText(requireContext(), "Failed to load published risks", Toast.LENGTH_SHORT).show()
                    root.progressBarNotificationsFrag.visibility = View.GONE
                }
            }
        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    //Toast.makeText(requireContext(), "Rawk bottom", Toast.LENGTH_SHORT).show()

                    val lastReq = RequestStrataInc(entryId = lastEntId)
                    reqrStrataInc.request(callbackStrataApi, lastReq)
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })


        reqrStrata.request(callbackStrataApi, root)
        root.progressBarNotificationsFrag.visibility = View.GONE
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarNotificationsFrag.visibility = View.VISIBLE
    }


}