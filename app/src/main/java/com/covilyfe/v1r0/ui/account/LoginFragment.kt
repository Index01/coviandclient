package com.covilyfe.v1r0.ui.account

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.covilyfe.v1r0.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.account_login.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Logger
import com.covilyfe.v1r0.Account
import com.covilyfe.v1r0.api.*
import kotlinx.android.synthetic.main.fragment_login.view.*


private lateinit var linearLayoutManager: LinearLayoutManager

class LoginFragment : Fragment() {
    @RequiresApi(26)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val logy = Logger.getLogger("LoginFragment")
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        val reqrAcctLogin = CoviApiAcctLogin()


        lateinit var username : String
        lateinit var email : String
        lateinit var pass : String

        logy.info("Login Fragment loaded")

        val dbAcct = DBHandlerAccount(context)
        val acct = dbAcct.retrieveAccount()

        root.imgAcct.setImageResource(R.drawable.rona2)
        if(acct.isNotEmpty()){
            val avatar = acct[0].avatar
            if(avatar != null && avatar!!.size > 1){
                val bmp = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                root.imgAcct.setImageBitmap(bmp)
            }
        }

        val card = root.findViewById<CardView>(R.id.card_holder)
        val acctInf = inflater.inflate(R.layout.account_login, null)
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


        val callbackAcctLogin = object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful){
                    val bod = response.body()!!.string()
                    val jmapper = jacksonObjectMapper()

                    data class ResponseAcctLogin(
                        val message: String,
                        val avatar: ByteArray?,
                        val jwt: String,
                        val email: String,
                        val user: String
                    )
                    data class ResponseAcctFail(
                        val protocol: Any?,
                        val message: String?,
                        val url: String?
                    )

                    if(response.code() != 200){
                        val jobj = jmapper.readValue<ResponseAcctFail>(bod)
                        acctInf.txtAcctLoginMsg.text = jobj.message
                        Toast.makeText(context, "Error: Login Error!", Toast.LENGTH_SHORT).show()
                    }

                    else {
                        dbAcct.deleteRows()
                        val jobj = jmapper.readValue<ResponseAcctLogin>(bod)
                        val account = Account(username = jobj.user.toString(),
                                              jwt = jobj.jwt,
                                              email = jobj.email,
                                              avatar = jobj.avatar
                        )
                        dbAcct.insertData(account = account)

                        //val sharedPreferences = activity?.getSharedPreferences("AccountStatus", Context.MODE_PRIVATE)
                        //sharedPreferences!!.edit().putBoolean("AccountLoggedIn", true)


                        //val int = activity!!.intent
                        //activity!!.finish()
                        //startActivity(int)


                        Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show()
                    }
                    val intent: Intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    view!!.findNavController().navigate(R.id.navigation_home)
                }

                else{
                    val errStr = response.errorBody()!!.string()
                    acctInf.txtAcctLoginMsg.text = "Failure:" + errStr
                    println("[-] Account create resp code != 200 " + errStr)
                    Toast.makeText(context, "Error: Issue with login", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Failure to login.", Toast.LENGTH_SHORT).show()
                acctInf.txtAcctLoginMsg.text = t.message.toString()
            }
        }


        root.btnAccountRecover?.setOnClickListener {
            flashFuncMasta(root.btnAccountRecover)
            Toast.makeText(context, "BleepBloop. Functionality not yet created. Email me.", Toast.LENGTH_SHORT).show()
        }

        root.btnAccountLogin?.setOnClickListener {
            flashFuncMasta(root.btnAccountLogin)
            println("[+] Send it")

            var accountReady = false

            val email: String = acctInf.etvLoginEmail.text.toString().replace("(", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("'", "")
            val pass: String = acctInf.etvLoginPassword.text.toString().replace("(", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("'", "")

            if (listOf<String>(email, pass).all { it.toString() != "" }) {
                accountReady = true
            } else {
                Toast.makeText(context, "All fields must be populated!", Toast.LENGTH_SHORT).show()
            }

            if(accountReady==true){
                val acctLoginReqr = RequestAcctLogin(email = email, password = pass)
                reqrAcctLogin.request(callbackAcctLogin, acctLoginReqr)

            }
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        return root
    }
}
