package com.covilyfe.v1r0.api

import android.content.Context
import android.view.View
import android.widget.Toast
import retrofit2.Callback
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class CTDSRequest(){
    companion object {
        val BASE_URL = "https://api.covidtracking.com/"
        val logi = HttpLoggingInterceptor()
    }

    val client = OkHttpClient.Builder()
        //.addInterceptor(logi.setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val service = retrofit.create(CovidTrackingDirectService::class.java)

    fun request(callback: Callback<ResponseBody>, state: String){
        val call = service.requestByState(state)
        call.enqueue(callback)
    }

}


open class CoviApiBase(){
    companion object {
        //val BASE_URL = "https://192.168.1.81:5000"
        //val BASE_URL = "https://10.0.2.2:5000"
        val BASE_URL = "https://3.21.92.196:5000"
        val logi = HttpLoggingInterceptor()
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logi.setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    open val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

}


class CoviApiAcctCreate : CoviApiBase(){
    val service = retrofit.create(AppAcctCreate::class.java)

    fun request(callback: Callback<ResponseBody>, acctCreateReqr : RequestAcctCreate){
        val call = service.requestAcctCreate(acctCreateReqr)
        call.enqueue(callback)
    }

}

class CoviApiAcctLogin : CoviApiBase(){
    val service = retrofit.create(AppAcctLogin::class.java)

    fun request(callback: Callback<ResponseBody>, acctLoginReqr : RequestAcctLogin){
        val call = service.requestAcctLogin(acctLoginReqr)
        call.enqueue(callback)
    }

}

class CoviApiAcctUpdate : CoviApiBase(){
    val service = retrofit.create(AppAcctAvatarUpdate::class.java)

    fun request(context: Context?,
                acctAvatarUpdateReqr : RequestAcctAvatarUpdate,
                jwt: String){
        val headers = mapOf<String, String>("Authorization" to jwt)
        val call = service.requestAcctAvatarUpdate(acctAvatarUpdateReqr, headers = headers)
        //call.enqueue(callback)

        try {
            val response = call.execute()
            if (response.isSuccessful) {
                println("[+] Avatar update")
                Toast.makeText(context, "Avatar update success", Toast.LENGTH_SHORT).show()
            } else {
                val code = response.raw().code()
                val msg = response.raw().message()
                println("[-] Publish error: " + code + msg)
                Toast.makeText(context, "Avatar update failed", Toast.LENGTH_SHORT).show()
            }
        } catch (ste: SocketTimeoutException) {
            println("[-] Avatar failed")
        }
    }
}

class CoviApiStrataPublish : CoviApiBase() {
    val service = retrofit.create(PublishStrataAll::class.java)

    fun request(
        context: Context?,
        publishStrataReqr: RequestPublishRisks,
        jwt: String
    ): Boolean {
        val headers = mapOf<String, String>("Authorization" to jwt)
        val call = service.requestPublish(publishStrataReqr, headers = headers)
        //call.enqueue(callback)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                println("[+] Publish Success")
                Toast.makeText(context, "Publish Success", Toast.LENGTH_SHORT).show()
                return true

            } else {
                val code = response.raw().code()
                val msg = response.raw().message()
                println("[-] Publish error: " + code + msg)
                Toast.makeText(context, "Error while publishing!", Toast.LENGTH_SHORT).show()
            }
        } catch (ste: SocketTimeoutException) {
            println("[-] Request failure")
        }
        return false
    }
}

class CoviApiStrataOut : CoviApiBase(){
    var respSize: Int = 5
    val service = super.retrofit.create(GetStrataAll::class.java)

    fun request(callback: Callback<ResponseBody>, root: View) {
        val call = service.requestStrataAll()
        call.enqueue(callback)

    }

    fun setSize(size: Int) {
        respSize = size
    }

    fun size(): Int {
        return respSize
    }
}

class CoviApiStrataOutIncrement : CoviApiBase(){
    var respSize: Int = 5
    val service = super.retrofit.create(GetStrataInc ::class.java)

    fun request(
        callback: Callback<ResponseBody>,
        reqrStrataInc: RequestStrataInc
    ): Boolean {
        val call = service.requestIncremental(reqrStrataInc)
        call.enqueue(callback)
        return true
    }

    fun setSize(size: Int) {
        respSize = size
    }

    fun size(): Int {
        return respSize
    }
}

class CoviApiReact : CoviApiBase(){
    val service = retrofit.create(EntryReact::class.java)

    fun request(callback: Callback<ResponseBody>,
                entryReactReqr : RequestEntryReact,
                jwt: String ){
        val headers = mapOf<String, String>("Authorization" to jwt)
        val call = service.requestEntryReact(entryReactReqr, headers = headers)
        call.enqueue(callback)
    }

}
