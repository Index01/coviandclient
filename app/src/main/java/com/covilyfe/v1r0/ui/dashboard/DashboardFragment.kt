package com.covilyfe.v1r0.ui.dashboard

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.covilyfe.v1r0.DBHandler
import com.covilyfe.v1r0.Person
import com.covilyfe.v1r0.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.util.*


class DashboardFragment : Fragment() {
    //todo: Risk tolerance averse settings

    private lateinit var dashboardViewModel: DashboardViewModel
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val view: View = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        val context = context

        view.btnRandName.setOnClickListener{
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.restrictDuration(2500)
            val td = TransitionDrawable(arrayOf(
                ColorDrawable(Color.TRANSPARENT), ColorDrawable(
                    Color.WHITE), ColorDrawable(Color.TRANSPARENT)
            ))
            view.btnRandName.foreground = td
            td.startTransition(500)
            td.reverseTransition(500)
            anim.startNow()
            view.btnRandName.startAnimation(anim)

            val randFirst = resources.getStringArray(R.array.names_first).random()
            val randLast = resources.getStringArray(R.array.names_last).random()
            view.etvName.setText(getString(R.string.rand_name, randFirst, randLast))

        }

        view.seekRiskPerceived.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                view.txtRiskPro.text = getString(R.string.risk_per_str, seekBar!!.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                val imm =
                    context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val strProgRisk = when (seekBar!!.progress) {
                    in 0..10 -> "Not that risk, they live on an island or somethin."
                    in 11..30 -> "Probably ok, they seem responsible."
                    in 31..50 -> "Sketchy, brah"
                    in 51..69 -> "Sketchy, brah"
                    in 70..89 -> "Might have been near positive cases recently."
                    in 90..100 -> "They mos def have the 'rona."
                    else -> "Off the charts."
                }
                Toast.makeText(context, "idk, like: " + strProgRisk, Toast.LENGTH_SHORT).show()
            }
        })
        view.seekExposurePerceived.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                view.txtExposurePro.text = getString(R.string.exposure_per_str, seekBar!!.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                val imm =
                    context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                var strProgRisk = when (seekBar!!.progress) {
                    in 0..10 -> "They were far away, good air flow"
                    in 11..30 -> "Little longer than comfortable"
                    in 31..50 -> "Less than 6ft!"
                    in 51..69 -> "Confined space for a while, low air flow."
                    in 70..89 -> "IF they had it you might have gotten it"
                    in 90..100 -> "They stuck their tongue in your mouth, or somethin."
                    else -> "Off the charts."
                }
                Toast.makeText(context, "idk, like: " + strProgRisk, Toast.LENGTH_SHORT).show()
            }
        })


        val spinStr = resources.getStringArray(R.array.spinVectorOptions)
        val spinner = view.findViewById<Spinner>(R.id.spinVector)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item, spinStr
            )
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val cal = Calendar.getInstance()
        view.etxtDate.text = getString(
            R.string.date_format,
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.YEAR)
        )
        val dateListener = object : DatePickerDialog.OnDateSetListener {
            val localView = view
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val strDate = getString(R.string.date_format, month, dayOfMonth, year)
                localView!!.etxtDate.setText(strDate)
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_YEAR, dayOfMonth)
            }
        }
        view.radDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(
                    view.context,
                    dateListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        view.etxtDate.setTextIsSelectable(false)
        view.etxtDate.setOnClickListener {
            DatePickerDialog(
                view.context,
                dateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        view.btnimg_insert.setOnClickListener {
            if (etvName.text.toString().length > 0 &&
                etvDescription.text.toString().length > 0 &&
                seekRiskPerceived.progress > 0 &&
                seekExposurePerceived.progress > 0
            ) {
                val person = Person(
                    view.etxtDate.text.toString(),
                    etvName.text.toString(),
                    etvDescription.text.toString(),
                    view.seekRiskPerceived.progress.toInt(),
                    view.seekExposurePerceived.progress.toInt(),
                    view.spinVector.selectedItem.toString()
                )
                val db = DBHandler(context)
                db.insertData(person)
                Toast.makeText(context, "New risk vector, I mean human, created!", Toast.LENGTH_SHORT).show()
                view.findNavController().navigate(R.id.navigation_home)
            } else {
                Toast.makeText(context, "Create the fields, loser.", Toast.LENGTH_SHORT).show()
            }
        }


        view.btnGotTested.setOnClickListener {
            println("GOOOT TESTTEEEDDD")

            val person = Person()


            val des = etvDescription.text.toString()
            if(des.length < 5){
                person.description = "I got tested today!"
            }else{
                person.description = des
            }
            person.date = view.etxtDate.text.toString()
            person.img_type = "Tested"

            val db = DBHandler(context)
            db.insertData(person)
            Toast.makeText(context, "Yay testing!", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.navigation_home)

        }

        return view
    }
}
