package com.covilyfe.v1r0.ui.avatar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.covilyfe.v1r0.*
import java.util.logging.Logger
import com.covilyfe.v1r0.api.*
import kotlinx.android.synthetic.main.fragment_avatar.*
import kotlinx.android.synthetic.main.fragment_avatar.view.*
import java.io.ByteArrayOutputStream
import java.net.SocketException
import java.util.*


private lateinit var linearLayoutManager: LinearLayoutManager

class EditSelector : Fragment() {
    lateinit var bmp : Bitmap
    @RequiresApi(26)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val logy = Logger.getLogger("AvatarEditor")
        val root = inflater.inflate(R.layout.fragment_avatar, container, false)
        val dbAcct = DBHandlerAccount(context)
        val acct = dbAcct.retrieveAccount()


        logy.info("Avatar Editor Loaded")



        val avatarPicker = Intent(Intent.ACTION_PICK)
        //avatarPicker.setAction(Intent.ACTION_GET_CONTENT)
        avatarPicker.setType("image/*")
        startActivityForResult(avatarPicker, 1)



        /**
        if(acct.isNotEmpty()){
            val avatar = acct[0].avatar
            if(avatar != null && avatar!!.size > 1){
                val bmp = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                //root.imgAcct.rotation = -90F
                root.imgAcct.setImageBitmap(bmp)
            }else{
                root.imgAcct.setImageResource(R.drawable.rona2)
            }
        }

        val card = root.findViewById<CardView>(R.id.card_holder)
        val acctCreate = root.findViewById<ConstraintLayout>(R.id.account_create_content)
        val acctInf = inflater.inflate(R.layout.account_login, null)
        card.removeAllViews()
        card.addView(acctInf)

         **/

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




        root.btnAvatarRotL.setOnClickListener {
            flashFuncMasta(root.btnAvatarRotL)
            val matrix = Matrix()
            matrix.postRotate(-90F)
            bmp = Bitmap.createBitmap(bmp,0, 0, bmp.width, bmp.height, matrix, false)
            imgAvatarSelect.setImageBitmap(bmp)



        }

        root.btnAvatarRotR.setOnClickListener {
            flashFuncMasta(root.btnAvatarRotR)
            val matrix = Matrix()
            matrix.postRotate(90F)
            bmp = Bitmap.createBitmap(bmp,0, 0, bmp.width, bmp.height, matrix, false)
            imgAvatarSelect.setImageBitmap(bmp)

        }

        root.btnAvatarCancel.setOnClickListener {
            flashFuncMasta(root.btnAvatarCancel)
            Toast.makeText(context, "Cancel avatar update", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_home)
        }

        root.btnAvatarAccept.setOnClickListener {
            flashFuncMasta(root.btnAvatarAccept)

            val scale_h = bmp.height / 15
            val scale_w = bmp.width / 15
            val scaledBmp = Bitmap.createScaledBitmap(bmp, scale_w.toInt(), scale_h.toInt(), false)
            val sbr = ByteArrayOutputStream()
            scaledBmp.compress(Bitmap.CompressFormat.PNG, 80, sbr)

            imgAvatarSelect.setImageBitmap(scaledBmp)

            val compBar = sbr.toByteArray()
            if(acct.isNotEmpty()){

                // post remote
                val reqrAvatarUpdate = CoviApiAcctUpdate()
                val benc = Base64.getEncoder().encode(compBar)

                val acctAvatarReqr = RequestAcctAvatarUpdate(
                    avatar = String(benc),
                    email = acct[0].email,
                    username = acct[0].username)
                try{
                    reqrAvatarUpdate.request(context, acctAvatarReqr, acct[0].jwt)
                }
                catch (e: SocketException){
                    println("[-] Socket exception")
                }
            }

            // update db
            dbAcct.updateAvatar(acct[0].id, compBar!!)

            val intent: Intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            findNavController().navigate(R.id.navigation_home)
        }

        return root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode==1){
            println("img result!")

            val bdat = requireContext().contentResolver.openInputStream(data?.data!!)!!.readBytes()
            val bun = Bundle()
            bun.putByteArray("bmpArr", bdat)

            bmp = BitmapFactory.decodeByteArray(bdat, 0, bdat.size)
            imgAvatarSelect.setImageBitmap(bmp)

        }
    }

}
