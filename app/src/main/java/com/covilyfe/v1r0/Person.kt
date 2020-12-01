package com.covilyfe.v1r0

class Person{

    var id : Int = 0
    var date : String = ""
    var name : String = ""
    var description : String = ""
    var risk_perceived : Int = 0
    var exposure_perceived : Int = 0
    var img_type : String = ""

    constructor(date:String, name:String, description:String, risk_perceived:Int, exposure_perceived:Int, image_type:String){
        this.date = date
        this.name = name
        this.description = description
        this.risk_perceived = risk_perceived
        this.exposure_perceived = exposure_perceived
        this.img_type = image_type
    }

    constructor()

}