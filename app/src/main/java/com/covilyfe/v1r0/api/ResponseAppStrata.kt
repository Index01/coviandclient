package com.covilyfe.v1r0.api

class ResponseAppStrata  : ArrayList<ResponseASAItem>()

data class ResponseASAItem(
    val entry: Entry,
    val user: User,
    val vectors: List<Vector>,
    val message: String?
)

data class Entry(
    val id: Int,
    val date: String,
    val percent_risk: Int,
    val percent_exposure: Int,
    val react_neg: Any?,
    val react_pos: Any?,
    val react_tested: Any?
)

data class User(
    val publisher_avatar: String?,
    val publisher_handle: String
)

data class Vector(
    val percent_exposure: Int,
    val percent_risk: Int,
    val vector_description: String,
    val vector_type: String
)