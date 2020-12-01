package com.covilyfe.v1r0.api

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CovidTrackingDirectService {
    @GET("/v1/states/{state}/current.json")
    fun requestByState(@Path("state")state:String): Call<ResponseBody>
}


data class RequestAcctCreate(
    @SerializedName("username") var username : String,
    @SerializedName("email") var email : String,
    @SerializedName("password") var password : String
)
interface AppAcctCreate {
    @Headers("Content-Type: application/json")
    @POST("/api/auth/create")
    fun requestAcctCreate(@Body body : RequestAcctCreate): Call<ResponseBody>
}


data class RequestAcctAvatarUpdate(
    @SerializedName("avatar") var avatar: String,
    @SerializedName("email") var email: String?,
    @SerializedName("username") var username: String
)
interface AppAcctAvatarUpdate {
    @Headers("Content-Type: application/json")
    @POST("/api/updateaccount")
    fun requestAcctAvatarUpdate(@Body body : RequestAcctAvatarUpdate,
                                @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>
}


data class RequestAcctLogin(
    @SerializedName("email") var email : String,
    @SerializedName("password") var password : String
)

interface AppAcctLogin {
    @POST("/api/auth/login")
    fun requestAcctLogin(@Body body : RequestAcctLogin): Call<ResponseBody>
}


interface GetStrataAll {
    @GET("/api/strataout")
    fun requestStrataAll(): Call<ResponseBody>
}


data class RequestStrataInc(
    @SerializedName("entryId") var entryId: Int
)
interface GetStrataInc {
    @Headers("Content-Type: application/json")
    @POST("/api/strataout/incremental")
    fun requestIncremental(@Body body: RequestStrataInc): Call<ResponseBody>
}


interface PublishStrataAll {
    @Headers("Content-Type: application/json")
    @POST("/api/stratain")
    fun requestPublish(@Body body: RequestPublishRisks,
                       @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>
}


data class React (
    val react_neg: Int? = null,
    val react_pos: Int? = null
)
data class RequestEntryReact(
    @SerializedName("entryId") var entryId : Int,
    @SerializedName("reaction") var pubentry : React
)
interface EntryReact {
    @Headers("Content-Type: application/json")
    @POST("/api/react")
    fun requestEntryReact(@Body body : RequestEntryReact,
                          @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>
}


