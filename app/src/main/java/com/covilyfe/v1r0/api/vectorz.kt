package com.covilyfe.v1r0.api

import java.util.ArrayList

//[{"vectors": [{"percent_exposure":9,"percent_risk":9,"vector_description":"in the place where i put that thing that one time","vector_type":"Person"},{"percent_exposure":16,"percent_risk":16,"vector_description":"sneezed near me ","vector_type":"Person"},{"percent_exposure":26,"percent_risk":5,"vector_description":"bleep bloop","vector_type":"Person"},{"percent_exposure":0,"percent_risk":0,"vector_description":"I got tested today!","vector_type":"Tested"},{"percent_exposure":47,"percent_risk":22,"vector_description":"at coffe place sneezed","vector_type":"Person"},{"percent_exposure":4,"percent_risk":36,"vector_description":"zoom","vector_type":"Person"}]}]

class vectorz : ArrayList<vectorzItem>()

data class vectorzItem(
    val vectors: List<Vect>
)

data class Vect(
    val percent_exposure: Int,
    val percent_risk: Int,
    val vector_description: String,
    val vector_type: String
)

