package com.covilyfe.v1r0.ui.home

import android.content.Context
import android.database.DatabaseUtils
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.covilyfe.v1r0.*
import com.covilyfe.v1r0.api.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.onboard_dialog.*
import kotlinx.android.synthetic.main.onboard_dialog.view.*


private lateinit var linearLayoutManager: LinearLayoutManager

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?


    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val db = DBHandler(context)
        val persons = db.retrievePersons()
        var riskAverage = 0
        var exposureAverage = 0
        var riskMax = 0
        var exposureMax = 0

        lateinit var recyclerView: RecyclerView
        lateinit var viewAdapter: RecyclerView.Adapter<*>
        lateinit var viewManager: RecyclerView.LayoutManager


        val sharedPreferences = activity?.getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
        val ob = sharedPreferences!!.getBoolean("onboarded", false)

        viewAdapter = RecyclerAdapter(persons, context)
        viewManager = LinearLayoutManager(context)
        recyclerView = root.findViewById<RecyclerView>(R.id.rvResult)

        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        val nav = findNavController()
        fun statusSelect(){
            // Get a single val representing the total potential of exposure and risk.
            val statusChecker = StatusChecker(db)
            val scalarStatus = statusChecker.calcRiskExposure()
            val statLevel = scalarStatus.get("upQt")

            //root.txtStatus.text = statusChecker.statusHeader(statLevel!!.toInt())
            root.txtStatus.text = requireContext().getString(
                R.string.status_header, statusChecker.statusHeader(statLevel!!.toInt()))
        }

        //sharedPreferences!!.edit().putBoolean("onboarded", false).apply()
        val lhome = root.findViewById<RelativeLayout>(R.id.layHome)
        val infOb = inflater.inflate(R.layout.onboard_dialog, container, false)

        if (ob){
            lhome.removeView(infOb)
        }else{
            lhome.addView(infOb)
        }

        fun calcMaxAvg () {
            if (persons.size != 0){
                riskAverage = db.getRiskSum() / persons.size
                exposureAverage = db.getExposureSum() / persons.size
                riskMax = db.getRiskMax()
                exposureMax = db.getExposureMax()
            }
            root.risk_avg.text = getString(R.string.risk_average, riskAverage)
            root.exposure_avg.text = getString(R.string.exposure_average, exposureAverage)
            root.risk_max.text = getString(R.string.risk_max, riskMax)
            root.exposure_max.text = getString(R.string.exposure_max, exposureMax)
            val dbread = db.readableDatabase
            val rowCount = DatabaseUtils.queryNumEntries(dbread, DBHandler.TABLE_NAME)
            root.total_encounters.text = getString(R.string.total_encounters, rowCount)
            dbread.close()
        }

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()){
            var localAdapter = RecyclerAdapter(persons, context)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                localAdapter.removeAt(viewHolder.adapterPosition)
                db.deleteRow(viewHolder)
                viewAdapter.notifyDataSetChanged()
                calcMaxAvg()
                statusSelect()
            }
        }

        root.btnPublish.setOnClickListener {
            println("[+] Publishin")
            var actRight = false
            val dbPersons = DBHandler(context)
            val pubPersons = dbPersons.retrievePersons()
            val dbAcct = DBHandlerAccount(context)
            val account = dbAcct.retrieveAccount()
            actRight = account.size == 1

            //val reqrPublish = StrataApiRequest.StrataPublishRisks()
            //val reqrPublish = StrataPublishRisks()


            val reqrPublish = CoviApiStrataPublish()
            val pubVectors = ArrayList<Vectorpub>()

            //todo: get location data from user entry
            val pubEntry = PublishEntry(
                location_data = 94608.toString(),
                percent_risk = riskAverage,
                react_tested = false,
                percent_exposure = exposureAverage
            )

            pubPersons.map {
                if("Tested" in it.img_type){
                    pubEntry.react_tested = true
                }
                pubVectors.add(
                    Vectorpub(
                        vector_description = it.description,
                        vector_type = it.img_type,
                        percent_risk = it.risk_perceived,
                        percent_exposure = it.exposure_perceived
                    )
                )
            }

            if(actRight && pubVectors.size > 0){
                val publication =
                    RequestPublishRisks(
                        pubEntry,
                        pubVectors
                    )
                if(reqrPublish.request(context, publication, account[0].jwt)){
                    requireView().findNavController().navigate(R.id.navigation_notifications)
                }
            }else{
                Toast.makeText(context, "Must has account and entries to publish!", Toast.LENGTH_SHORT).show()
            }


        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        infOb.btnOnboardingAcctY.setOnClickListener {
            cvOnboardingHolder.visibility = View.GONE
            val sharedPreferences = activity?.getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
            //val onboarded = sharedPreferences!!.getBoolean("onboarded", true)
            sharedPreferences!!.edit().putBoolean("onboarded", true).apply()
            nav.navigate(R.id.navigation_account_create)
        }

        infOb.btnOnboardingAcctN.setOnClickListener {
            cvOnboardingHolder.visibility = View.GONE
            val sharedPreferences = activity?.getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
            //val onboarded = sharedPreferences!!.getBoolean("onboarded", true)
            sharedPreferences!!.edit().putBoolean("onboarded", true).apply()
        }



        calcMaxAvg()
        statusSelect()
        return root
    }
}