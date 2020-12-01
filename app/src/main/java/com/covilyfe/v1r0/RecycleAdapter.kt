package com.covilyfe.v1r0

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_person.view.*
import java.lang.Exception


class RecyclerAdapter(private val dataset: MutableList<Person>, val context: Context?) : RecyclerView.Adapter<RecyclerAdapter.PersonHolder>()  {
    val risk_sum = mutableListOf<Int>()
    val exposure_sum = mutableListOf<Int>()
    class PersonHolder(val textView: View?) : RecyclerView.ViewHolder(textView!!){
        val txtID = textView!!.rootView.txtID
        val txtDate = textView!!.rootView.txtDate
        val txtName = textView!!.rootView.txtName
        val txtDescription = textView!!.rootView.txtDescription
        val txtRisk = textView!!.rootView.txtRisk
        val txtExposure = textView!!.rootView.txtExposure
        val barRisk = textView!!.rootView.barRisk
        val barExposure = textView!!.rootView.barExposure
        val imgTypeVector = textView!!.rootView.imgTypeVector
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapter.PersonHolder {
        val textView : View? = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_person, parent, false)
        return PersonHolder(textView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerAdapter.PersonHolder, position: Int) {
        val person : Person = dataset[position]
        /** Must match spinner stringaray in strings.xml with drawable file name**/
        val imgMap = mapOf("Person" to "person",
            "Crowd Indoor" to "crowd_indoor",
            "Crowd Outdoor" to "crowd_outdoor",
            "Tested" to "tested",
            "Surface" to "surface")
        val imgStr = imgMap.get(person.img_type)
        holder.txtID.text = person.id.toString()
        holder.txtDate.text  = person.date
        holder.txtName.text = person.name
        holder.txtDescription.text = person.description.toString()
        holder.txtRisk.text = context!!.getString(R.string.risk_per_str, person.risk_perceived)
        holder.txtExposure.text = context!!.getString(R.string.exposure_per_str, person.exposure_perceived)
        holder.barRisk.progress = person.risk_perceived
        holder.barExposure.progress = person.exposure_perceived
        println("imgstrrrrr: "  + imgStr)
        try{
            holder.imgTypeVector.setImageResource(
                context.resources.getIdentifier(imgStr, "drawable", context.packageName)
            )
        }
        catch (NullPointerException: Exception){
            println("Bloop")
        }

        // todo: duplicate code?!
        risk_sum.add(person.risk_perceived)
        exposure_sum.add(person.exposure_perceived)
    }

    fun removeAt(position: Int) {
        dataset.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

}


