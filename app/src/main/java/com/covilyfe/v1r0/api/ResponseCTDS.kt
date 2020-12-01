package com.covilyfe.v1r0.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CTDSResponse(val results: List<ResponseCTDS>){}

class ResponseCTDS {

    @SerializedName("date")
    @Expose
    var date: Int? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("positive")
    @Expose
    var positive: Int? = null

    @SerializedName("probableCases")
    @Expose
    var probableCases: Any? = null

    @SerializedName("negative")
    @Expose
    var negative: Int? = null

    @SerializedName("pending")
    @Expose
    var pending: Any? = null

    @SerializedName("totalTestResults")
    @Expose
    var totalTestResults: Int? = null

    @SerializedName("hospitalizedCurrently")
    @Expose
    var hospitalizedCurrently: Int? = null

    @SerializedName("hospitalizedCumulative")
    @Expose
    var hospitalizedCumulative: Any? = null

    @SerializedName("inIcuCurrently")
    @Expose
    var inIcuCurrently: Int? = null

    @SerializedName("inIcuCumulative")
    @Expose
    var inIcuCumulative: Any? = null

    @SerializedName("onVentilatorCurrently")
    @Expose
    var onVentilatorCurrently: Any? = null

    @SerializedName("onVentilatorCumulative")
    @Expose
    var onVentilatorCumulative: Any? = null

    @SerializedName("recovered")
    @Expose
    var recovered: Any? = null

    @SerializedName("dataQualityGrade")
    @Expose
    var dataQualityGrade: String? = null

    @SerializedName("lastUpdateEt")
    @Expose
    var lastUpdateEt: String? = null

    @SerializedName("dateModified")
    @Expose
    var dateModified: String? = null

    @SerializedName("checkTimeEt")
    @Expose
    var checkTimeEt: String? = null

    @SerializedName("death")
    @Expose
    var death: Int? = null

    @SerializedName("hospitalized")
    @Expose
    var hospitalized: Any? = null

    @SerializedName("dateChecked")
    @Expose
    var dateChecked: String? = null

    @SerializedName("totalTestsViral")
    @Expose
    var totalTestsViral: Int? = null

    @SerializedName("positiveTestsViral")
    @Expose
    var positiveTestsViral: Any? = null

    @SerializedName("negativeTestsViral")
    @Expose
    var negativeTestsViral: Any? = null

    @SerializedName("positiveCasesViral")
    @Expose
    var positiveCasesViral: Int? = null

    @SerializedName("deathConfirmed")
    @Expose
    var deathConfirmed: Any? = null

    @SerializedName("deathProbable")
    @Expose
    var deathProbable: Any? = null

    @SerializedName("totalTestEncountersViral")
    @Expose
    var totalTestEncountersViral: Any? = null

    @SerializedName("totalTestsPeopleViral")
    @Expose
    var totalTestsPeopleViral: Any? = null

    @SerializedName("totalTestsAntibody")
    @Expose
    var totalTestsAntibody: Any? = null

    @SerializedName("positiveTestsAntibody")
    @Expose
    var positiveTestsAntibody: Any? = null

    @SerializedName("negativeTestsAntibody")
    @Expose
    var negativeTestsAntibody: Any? = null

    @SerializedName("totalTestsPeopleAntibody")
    @Expose
    var totalTestsPeopleAntibody: Any? = null

    @SerializedName("positiveTestsPeopleAntibody")
    @Expose
    var positiveTestsPeopleAntibody: Any? = null

    @SerializedName("negativeTestsPeopleAntibody")
    @Expose
    var negativeTestsPeopleAntibody: Any? = null

    @SerializedName("totalTestsPeopleAntigen")
    @Expose
    var totalTestsPeopleAntigen: Any? = null

    @SerializedName("positiveTestsPeopleAntigen")
    @Expose
    var positiveTestsPeopleAntigen: Any? = null

    @SerializedName("totalTestsAntigen")
    @Expose
    var totalTestsAntigen: Any? = null

    @SerializedName("positiveTestsAntigen")
    @Expose
    var positiveTestsAntigen: Any? = null

    @SerializedName("fips")
    @Expose
    var fips: String? = null

    @SerializedName("positiveIncrease")
    @Expose
    var positiveIncrease: Int? = null

    @SerializedName("negativeIncrease")
    @Expose
    var negativeIncrease: Int? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("totalTestResultsSource")
    @Expose
    var totalTestResultsSource: String? = null

    @SerializedName("totalTestResultsIncrease")
    @Expose
    var totalTestResultsIncrease: Int? = null

    @SerializedName("posNeg")
    @Expose
    var posNeg: Int? = null

    @SerializedName("deathIncrease")
    @Expose
    var deathIncrease: Int? = null

    @SerializedName("hospitalizedIncrease")
    @Expose
    var hospitalizedIncrease: Int? = null

    @SerializedName("hash")
    @Expose
    var hash: String? = null

    @SerializedName("commercialScore")
    @Expose
    var commercialScore: Int? = null

    @SerializedName("negativeRegularScore")
    @Expose
    var negativeRegularScore: Int? = null

    @SerializedName("negativeScore")
    @Expose
    var negativeScore: Int? = null

    @SerializedName("positiveScore")
    @Expose
    var positiveScore: Int? = null

    @SerializedName("score")
    @Expose
    var score: Int? = null

    @SerializedName("grade")
    @Expose
    var grade: String? = null

}