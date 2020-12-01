package com.covilyfe.v1r0.api

data class RequestPublishRisks(
    var pubentry : PublishEntry,
    var vectors : ArrayList<Vectorpub>
)

data class PublishEntry(
    var location_data : String?,
    var react_tested : Boolean?,
    var percent_risk: Int?,
    var percent_exposure: Int?
)

data class Vectorpub(
    val  vector_description : String,
    val  vector_type : String,
    var percent_exposure : Int,
    val  percent_risk : Int
)


