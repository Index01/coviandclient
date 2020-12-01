package com.covilyfe.v1r0.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SAResponse(val results: List<ResponseStrataApi >){}

class ResponseStrataApi {

    @SerializedName("user")
    @Expose
    val user: String? = null

    @SerializedName("entry")
    @Expose
    val entry: String? = null

    @SerializedName("vectors")
    @Expose
    val vectors: String? = null

}
