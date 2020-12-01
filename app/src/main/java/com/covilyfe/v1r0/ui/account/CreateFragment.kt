package com.covilyfe.v1r0.ui.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.covilyfe.v1r0.*
import com.covilyfe.v1r0.api.RequestAcctCreate
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.account_create.*
import kotlinx.android.synthetic.main.account_create.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Logger
import com.covilyfe.v1r0.Account
import com.covilyfe.v1r0.api.CoviApiAcctCreate
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*


private lateinit var linearLayoutManager: LinearLayoutManager

class CreateFragment : Fragment() {

    @RequiresApi(26)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val logy = Logger.getLogger("AcctCreateFragment")
        logy.info("Acct Create Fragment loaded")

        val root = inflater.inflate(R.layout.fragment_login, container, false)
        val mainRoot = inflater.inflate(R.layout.activity_main,container, false)
        val reqrAcctCreate = CoviApiAcctCreate()
        val dbAcct = DBHandlerAccount(context)
        val acct = dbAcct.retrieveAccount()
        root.imgAcct.setImageResource(R.drawable.rona2)
        if(acct.isNotEmpty()){
            val avatar = acct[0].avatar
            if(avatar != null && avatar!!.size > 100){
                val bmp = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                root.imgAcct.setImageBitmap(bmp)
            }
        }



        val card = root.findViewById<CardView>(R.id.card_holder)
        val acctInf = inflater.inflate(R.layout.account_create, null)
        card.removeAllViews()
        card.addView(acctInf)



        fun flashFuncMasta(button: Button){
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.restrictDuration(2500)
            val td = TransitionDrawable(arrayOf(
                ColorDrawable(Color.TRANSPARENT), ColorDrawable(
                    Color.WHITE), ColorDrawable(Color.TRANSPARENT)
            ))
            button.foreground = td
            td.startTransition(500)
            td.reverseTransition(500)
            anim.startNow()

            button.startAnimation(anim)
        }


        val callbackAcctCreate = object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) =
                if(response.isSuccessful){
                    //val head = response.headers()
                    val bod = response.body()!!.string()
                    val jmapper = jacksonObjectMapper()

                    data class ResponseAcctCreate(
                        val email: String,
                        val jwt: String,
                        val user: String
                    )
                    data class ResponseAcctMsg2(
                        val protocol: Any?,
                        val message: String?,
                        val url: String?
                    )

                    dbAcct.deleteRows()
                    if(response.code() != 200){
                        println(bod)
                        val jobj = jmapper.readValue<ResponseAcctMsg2>(bod)
                        txtAcctRespMMsg.text = jobj.message
                        println("[-] Account create resp code != 200 " + bod)
                        Toast.makeText(context, "Error: Issue creating account", Toast.LENGTH_SHORT).show()
                    }else {
                        val jobj = jmapper.readValue<ResponseAcctCreate>(bod)
                        val account = Account(username = jobj.user,
                            jwt = jobj.jwt,
                            email = jobj.email.toString() ,
                            avatar = null
                        )
                        dbAcct.insertData(account = account)
                        println("[+] Acct created!")
                        //Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                        //val sharedPreferences = activity?.getSharedPreferences("AccountStatus", Context.MODE_PRIVATE)
                        //sharedPreferences!!.edit().putBoolean("AccountLoggedIn", true)
                    }
                    val intent: Intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    view!!.findNavController().navigate(R.id.navigation_home)
                }

                else{
                    val errStr = response.errorBody()!!.string()
                    txtAcctRespMMsg.text = "Failure:" + errStr
                    println("[-] Account create resp code != 200 " + errStr)
                    Toast.makeText(context, "Error: Issue creating account", Toast.LENGTH_SHORT).show()
                }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("[-] Acct create failure." + t.message.toString())
                Toast.makeText(context, "Failure to create account.", Toast.LENGTH_SHORT).show()
                txtAcctRespMMsg.text = t.message.toString()
            }

        }



        root.btnAccountCreate.setOnClickListener {
            flashFuncMasta(root.btnAccountCreate)
            logy.info("[+] Engage")
            var accountReady: Boolean = false

            var username: String = acctInf.etvAcctCreateName.text.toString().replace("(", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("'", "")
            val email: String = acctInf.etvAcctCreateEmail.text.toString().replace("(", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("'", "")
            val pass: String = acctInf.etvAcctCreatePassword.text.toString().replace("(", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("'", "")



            if (listOf<String>(username, email, pass).all { it.toString() != "" }) {
                accountReady = true
            } else {
                Toast.makeText(context, "All fields must be populated!", Toast.LENGTH_SHORT).show()
            }

            val bad_wordz = resources.getStringArray(R.array.WordsWeDontAllow)
            val shady_unamez = resources.getStringArray(R.array.UserNamesWeDontAllow)
            if (username.contains(" ")){
                txtAcctRespMMsg.text = "Failure: Name must not contain spaces."
                accountReady = false
            }
            for(i in bad_wordz){
                if(i in username){
                    txtAcctRespMMsg.text = "Failure: Is that foul language, brah?"
                    accountReady = false
                }
            }
            for(i in shady_unamez){
                if(username.contains(i)){
                    txtAcctRespMMsg.text = "Failure: Username seems kinda shady. Try again"
                    accountReady = false
                }
            }
            if(username.length > 20){
                txtAcctRespMMsg.text = "Failure: Username too long. 20 char max"
                accountReady = false
            }

            if(accountReady == true){
                val acctCreateReqr = RequestAcctCreate(email = email, username = username, password = pass)
                println(acctCreateReqr)
                reqrAcctCreate.request(callbackAcctCreate, acctCreateReqr)
                val imm =
                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
            else {
                Toast.makeText(context, "Account is missing something.", Toast.LENGTH_SHORT).show()
            }

        }



        root.btnAccountLoginNav.setOnClickListener {

            flashFuncMasta(root.btnAccountLoginNav)
            findNavController().navigate(R.id.navigation_account_login)
        }



        return root
    }
}
