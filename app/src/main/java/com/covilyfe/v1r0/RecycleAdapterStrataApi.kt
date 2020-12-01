package com.covilyfe.v1r0

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.covilyfe.v1r0.api.*
import com.covilyfe.v1r0.api.Vector
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.recyc_expand_row.view.*
import kotlinx.android.synthetic.main.recyclerview_strataapi.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class RecyclerAdapterStrataApi(val jobj : ResponseAppStrata, private val dataset: CoviApiStrataOut, val context: Context?) : RecyclerView.Adapter<RecyclerAdapterStrataApi.PublishedRiskHolder>()  {

    var btnExpClicked : Boolean = false
    class PublishedRiskHolder(val textView: View?, val wectorz: List<Vector>) : RecyclerView.ViewHolder(textView!!){
        val txtId = textView!!.rootView.txtPubId
        val txtHandle = textView!!.rootView.txtHandle
        val imgAvatar = textView!!.rootView.imgAvatarPublisher
        val txtDate = textView!!.rootView.txtDatePub
        val barRisk = textView!!.rootView.barPubRisk
        val barExp = textView!!.rootView.barPubExp
        val txtReactPos = textView!!.rootView.txtReactSmile
        val txtReactNeg = textView!!.rootView.txtReactMask
        val layGotTested = textView!!.rootView.layTested
        val txtGotTested = textView!!.rootView.txtGotTested
        val imgTested = textView!!.rootView.imgTestedStar
        val txtVectors = textView!!.rootView.txtVectors
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapterStrataApi.PublishedRiskHolder {
        val textView : View? = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_strataapi, parent, false)
        println("[+] Creatin view")


        var reactPosClicked : Boolean = false
        var reactNegClicked : Boolean = false

        val db = DBHandlerAccount(context)
        val account = db.retrieveAccount()


        fun flashFuncMasta(button: Button){
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.restrictDuration(2500)
            val td = TransitionDrawable(arrayOf(ColorDrawable(Color.TRANSPARENT), ColorDrawable(Color.WHITE), ColorDrawable(Color.TRANSPARENT)))
            button.foreground = td
            td.startTransition(500)
            td.reverseTransition(500)
            anim.startNow()

            button.startAnimation(anim)
        }

        val callbackReact = object : Callback<ResponseBody> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println("[-] React publish success")
                val bod = response.body()!!.string()
                val jmapper = jacksonObjectMapper()

                data class ResponseReact(
                    val react_pos : Int?,
                    val react_neg : Int?
                )
                val jobj = jmapper.readValue<ResponseReact>(bod)
                val js = bod.split(":")

                if (js[0].contains("react_pos")){
                    textView!!.txtReactSmile.text = jobj.react_pos.toString()
                }
                else if(js[0].contains("react_neg")){
                    textView!!.txtReactMask.text = jobj.react_neg.toString()
                }
                else{
                    println("wrong reaction response")
                    Toast.makeText(context, "Failed to react! May need login", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("[-] React publish failure")
                Toast.makeText(context, "Failed to react! May need login", Toast.LENGTH_SHORT).show()

            }

        }
        textView!!.btnRecycExpand.setOnClickListener {
            println("[+] Expansion")
            flashFuncMasta(textView.btnRecycExpand)
            val rlay = textView.findViewById<LinearLayout>(R.id.layRecycRowHolder)

            if (btnExpClicked == false){
                val layRV = textView.findViewById<LinearLayout>(R.id.layoutRiskVectors)
                val layParam = layRV.layoutParams
                layParam.height = 1
                layRV.layoutParams = layParam

                val tvs = textView.txtVectors.text.toString()
                val jmapper = jacksonObjectMapper()

                val jobj = listOf<vectorz>(jmapper.readValue<vectorz>(tvs))

                for(vect in jobj[0][0].vectors){
                    val inf = LayoutInflater.from(context).inflate(R.layout.recyc_expand_row, rlay, false)
                    val lhold : LinearLayout = inf.findViewById(R.id.RecycRowLay)

                    lhold.txtRowz.text = vect.vector_description
                    lhold.barRExpand.progress = vect.percent_risk
                    lhold.barEExpand.progress = vect.percent_exposure
                    rlay.addView(inf)
                }
                btnExpClicked = true
            }
            else {
                rlay.removeAllViews()
                textView.layoutRiskVectors.layoutParams.height = 150
                btnExpClicked = false
            }
        }


        textView!!.btnSmile.setOnClickListener {
            println("+ Clicky smile")
            flashFuncMasta(textView.btnSmile)
            //val anim = AlphaAnimation(1.0f, 0.0f)
            //anim.restrictDuration(2500)
            //val td = TransitionDrawable(arrayOf(ColorDrawable(Color.TRANSPARENT), ColorDrawable(Color.WHITE), ColorDrawable(Color.TRANSPARENT)))
            //textView.btnSmile.foreground = td
            //td.startTransition(500)
            //td.reverseTransition(500)
            //anim.startNow()
            //textView.btnSmile.startAnimation(anim)

            //val db = DBHandlerAccount(context)
            //val account = db.retrieveAccount()

            var react : Int = 0
            if (reactPosClicked == true){
                react = -1
                reactPosClicked = false
            }else{
                react = 1
                reactPosClicked = true
            }
            val reqrReact = CoviApiReact()

            val entryReactReqr = RequestEntryReact(entryId = Integer.parseInt(textView.txtPubId.text.toString()),
                pubentry = React(react_pos = react)
            )
            if(account.isNullOrEmpty()){
                Toast.makeText(context, "Failed to react! May need login", Toast.LENGTH_SHORT).show()
            }
            else{
                reqrReact.request(callbackReact, entryReactReqr, account[0].jwt)
            }
        }

        textView!!.btnMask.setOnClickListener {
            println("+ Clicky mask")
            flashFuncMasta(textView.btnMask)
            //val anim = AlphaAnimation(1.0f, 0.0f)
            //anim.restrictDuration(2500)
            //val td = TransitionDrawable(arrayOf(ColorDrawable(Color.TRANSPARENT), ColorDrawable(Color.WHITE), ColorDrawable(Color.TRANSPARENT)))
            //textView.btnMask.foreground = td
            //td.startTransition(500)
            //td.reverseTransition(500)
            //anim.startNow()
            //textView.btnMask.startAnimation(anim)



            val db = DBHandlerAccount(context)
            val account = db.retrieveAccount()

            var react : Int = 0
            if (reactNegClicked == true){
                react = -1
                reactNegClicked = false
            }else{
                react = 1
                reactNegClicked = true
            }
            val reqrReact = CoviApiReact()


            val entryReactReqr = RequestEntryReact(entryId = Integer.parseInt(textView.txtPubId.text.toString()),
                pubentry = React(react_neg = react)
            )
            if(account.isNullOrEmpty()){
                Toast.makeText(context, "Failed to react! May need login", Toast.LENGTH_SHORT).show()
            }
            else{
                reqrReact.request(callbackReact, entryReactReqr, account[0].jwt)
            }

        }

        return PublishedRiskHolder(textView, jobj[0].vectors)
    }

    override fun getItemCount(): Int {
        return dataset.size()
    }


    //@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(26)
    override fun onBindViewHolder(holder: RecyclerAdapterStrataApi.PublishedRiskHolder, position: Int) {
        //println("jobj: " + jobj)
        if(jobj.isNotEmpty() && jobj.size >0){

            //println("name: " + jobj[0].user.publisher_handle.toString())

            val jmapper = jacksonObjectMapper()
            val jstr = jmapper.writeValueAsString(jobj[0].vectors)
            val vset = context!!.getString(R.string.vform, jstr)

            if (holder.txtHandle.text.length == 0){
                val dt = jobj[0].entry.date
                val dfmt = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss.SSSSSS")
                val dtfmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                val dateTimeF: LocalDate = LocalDate.parse(dt, dfmt)
                val dateTime = dtfmt.format(dateTimeF)

                val avBarr = jobj[0].user.publisher_avatar!!.toByteArray()
                if (avBarr.isNotEmpty()){
                    val base = Base64.getDecoder().decode(avBarr)
                    val bmp = BitmapFactory.decodeByteArray(base, 0, base.size)
                    holder.imgAvatar.setImageBitmap(bmp)
                }else{
                    holder.imgAvatar.setImageResource(R.drawable.rona2)
                }

                var rpos = jobj[0].entry.react_pos.toString()
                if("null" in rpos){
                    rpos= 0.toString()
                }
                var rneg = jobj[0].entry.react_neg.toString()
                if("null" in rneg){
                    rneg= 0.toString()
                }

                val tested = jobj[0].entry.react_tested
                if (tested != null && tested as Boolean == true){
                    holder.layGotTested.visibility = View.VISIBLE
                    holder.imgTested.visibility = View.VISIBLE
                    holder.txtGotTested.visibility = View.VISIBLE
                }


                println("len: " + holder.txtHandle.text.length + " name: " + holder.txtHandle.text)

                holder.txtId.text = jobj[0].entry.id.toString()
                holder.txtHandle.text = context!!.getString(R.string.handle_str, jobj[0].user.publisher_handle.toString())
                holder.txtDate.text = dateTime.toString()
                holder.barRisk.progress = jobj[0].entry.percent_risk
                holder.barExp.progress = jobj[0].entry.percent_exposure
                holder.txtReactPos.text = rpos
                holder.txtReactNeg.text = rneg
                holder.txtVectors.text = vset

                holder.itemView.layoutRiskVectors.removeAllViews()
                jobj[0].vectors.forEach {
                    val imgV = ImageView(context)
                    imgV.setImageResource(R.drawable.person)
                    imgV.adjustViewBounds = true
                    imgV.maxWidth = 85
                    imgV.setPadding(5, 0, 5, 0)

                    holder.itemView.layoutRiskVectors.addView(imgV)
                }

                jobj.removeAt(0)
            }
            /**
            if(holder.txtVectors.text.length == 0 ){

            holder.itemView.layoutRiskVectors.removeAllViews()
            jobj[0].vectors.forEach {
            val imgV = ImageView(context)
            imgV.setImageResource(R.drawable.person)
            imgV.adjustViewBounds = true
            imgV.maxWidth = 85
            imgV.setPadding(5, 0, 5, 0)

            holder.itemView.layoutRiskVectors.addView(imgV)
            }
            }else {
            val vobj = listOf<vectorz>(jmapper.readValue<vectorz>(holder.txtVectors.text.toString()))
            holder.itemView.layoutRiskVectors.removeAllViews()
            for (v in vobj[0][0].vectors){
            //TODO: dup dup
            val imgV = ImageView(context)
            imgV.setImageResource(R.drawable.person)
            imgV.adjustViewBounds = true
            imgV.maxWidth = 85
            imgV.setPadding(5, 0, 5, 0)
            holder.itemView.layoutRiskVectors.addView(imgV)

            }
            }
             **/
        }
    }
}


