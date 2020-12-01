package com.covilyfe.v1r0

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {
    var db = DBHandler(this)
    lateinit var sharedPreferences : SharedPreferences
    val themeKey = "dark"

    lateinit var drawerLayout : DrawerLayout
    lateinit var navViewSettings : NavigationView
    lateinit var actionBarToggle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //todo: dup code. combine theme toggle
        sharedPreferences = getSharedPreferences(
            "themePreff",
            Context.MODE_PRIVATE
        )
        when(sharedPreferences.getString(themeKey, "dark")){
            "dark" -> theme.applyStyle(R.style.themeOLDark, true)
            "light" -> theme.applyStyle(R.style.themeOLLight, true)
        }
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navViewMain: BottomNavigationView = findViewById(R.id.bot_nav_view)
        val navViewSettings: NavigationView = findViewById(R.id.nav_view_settings)
        val navControllerMain = findNavController(R.id.nav_host_fragment)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        navViewMain.setupWithNavController(navControllerMain)
        navViewSettings.setupWithNavController(navControllerMain)

        val actionBarToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()


        fun toggleTheme(){
            val themeKey = "dark"
            sharedPreferences = getSharedPreferences(
                "themePreff",
                Context.MODE_PRIVATE
            )

            if(sharedPreferences.getString(themeKey, "dark") == "light"){
                sharedPreferences.edit().putString(themeKey, "dark").apply()
                Toast.makeText(applicationContext, "Dark Mode", Toast.LENGTH_SHORT).show()
            }else {
                sharedPreferences.edit().putString(themeKey, "light").apply()
                Toast.makeText(applicationContext, "Light Mode", Toast.LENGTH_SHORT).show()
            }

            when(sharedPreferences.getString(themeKey, "dark")){
                "dark" -> theme.applyStyle(R.style.themeOLDark, true)
                "light" -> theme.applyStyle(R.style.themeOLLight, true)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            val intent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        //fun setAvatar(){
        //    val avatarPicker = Intent(Intent.ACTION_PICK)
        //    //avatarPicker.setAction(Intent.ACTION_GET_CONTENT)
        //    avatarPicker.setType("image/*")
        //    startActivityForResult(avatarPicker, 1)

        //}


        val navViewSetFrag : NavigationView = findViewById(R.id.nav_view_settings)
        navViewSetFrag.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.toString()){
                    "Login" -> {navControllerMain.navigate(R.id.navigation_account_create); drawerLayout.close()}
                    "Risk and Exposure" -> {navControllerMain.navigate(R.id.navigation_home); drawerLayout.close()}
                    "Risk Entry" -> {navControllerMain.navigate(R.id.navigation_dashboard); drawerLayout.close()}
                    "Local Stats" -> {navControllerMain.navigate(R.id.navigation_notifications); drawerLayout.close()}
                    "Toggle Theme" -> toggleTheme()
                    "Set Avatar" -> {navControllerMain.navigate(R.id.navigation_avatar); drawerLayout.close()}
                    //else -> logy.warning("[-] Unsupported menu item")
                    else -> println("clicky: " + item.toString())
                }
                return true
            }
        })

        sharedPreferences.edit().putString("acctUsername", "username").apply()
        sharedPreferences.edit().putString("acctEmail", "email").apply()

        println("[+] -------------- INITIALIZED ----------------")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(txtMenuUsername != null){
            txtMenuUsername.text = getString(R.string.nav_header_title, sharedPreferences.getString("acctUsername", "my_sweet_username"))
            txtMenuEmail.text = getString(R.string.nav_header_subtitle, sharedPreferences.getString("acctEmail", "my_sweet_email"))
        }

        val dbAcct = DBHandlerAccount(applicationContext)
        val acct = dbAcct.retrieveAccount()
        if(acct.isNotEmpty()){
            txtMenuUsername.text = acct[0].username
            txtMenuEmail.text = acct[0].email

            val avatar = acct[0].avatar
            if(avatar != null && avatar!!.size > 100){
                val bmp = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
                imgAvatarMenu.setImageBitmap(bmp)
            }
            else {
                imgAvatarMenu.setImageResource(R.drawable.rona2)
            }
        }
        return true
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode==1){

            val navViewSettings: NavigationView = findViewById(R.id.nav_view_settings)

            //val bdat = contentResolver.openInputStream(data?.data!!)!!.readBytes()
            //val navController = findNavController(R.id.nav_host_fragment)
            //val bun = Bundle()
            //bun.putByteArray("bmpArr", bdat)
            //navController.navigate(R.id.navigation_avatar, bun)

            navViewSettings.findNavController().navigate(R.id.navigation_avatar)


            /**

            println("never print me")


            val dbAcct = DBHandlerAccount(applicationContext)
            val acct = dbAcct.retrieveAccount()
            val bmp = BitmapFactory.decodeByteArray(bdat, 0, bdat.size)
            val scaledBmp = Bitmap.createScaledBitmap(bmp, 200, 200, false)

            val sbr = ByteArrayOutputStream()

            scaledBmp.compress(Bitmap.CompressFormat.PNG, 90, sbr)
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
                    reqrAvatarUpdate.request(applicationContext, acctAvatarReqr, acct[0].jwt)
                }
                catch (e: SocketException){
                    println("[-] Socket exception")
                }

                // set img views
                imgAvatarMenu.setImageURI(data?.data)

                // update db
                dbAcct.updateAvatar(acct[0].id, compBar!!)

            }
            **/
        }
    }
}
