package com.covilyfe.v1r0

import kotlin.math.abs
import kotlin.math.sqrt

class StatusChecker(db:DBHandler) {
    val db = db
    fun calcRiskExposure(): Map<String, Int?>{
        /**
         * Reducing risk and exposure to a single value through statistical methods
         *
         * We could correlate the risk and exposure Z-values.
         * Currently returning median, upper quartile and max
         */
        val riskVals = db.risk_sum
        val exposureVals = db.exposure_sum

        fun calcMeanDeviation(list: List<Int>) : List<Int?>{
            if (list.size != 0){
                // calc avg of all vals
                val avg = list.sum()/list.size
                // calc difference of avg and each val
                val meanDistances = list.mapIndexed { idx, value -> abs(avg - idx)  }
                // calc mean deviation, return (mean, deviation)
                return listOf(avg.toInt(), meanDistances.sum()/list.size.toInt())
            }else{
                return listOf(0, 0)
            }
        }
        fun calcMedian(list: List<Int>) : Int{
            val sortLin = list.sorted()
            if (sortLin.size % 2 == 0) {
                // even num elemss, avg mid two
                return (sortLin.get(sortLin.size/2 - 1) + sortLin.get(sortLin.size/2)) / 2
            }else{
                // odd num elems, take middle one
                return sortLin.get(sortLin.size/2.toInt())
            }
        }
        fun calcUpperQuartile(list : List<Int>) : Int {
            val sortLin = list.sorted().reversed()
            val chunkLin = sortLin.chunked(list.size/2 + 1).reversed()
            if (chunkLin.size>1) {
                return calcMedian(chunkLin[1])
            }else{
                return calcMedian(chunkLin[0])
            }
        }

        fun sumSquares(list1: List<Int>, list2: List<Int>) : List<Int>{
            /**
             * We care about high risk high exposure, medium risk high exposure and to a less degree
             * low risk low exposure. Maybe other things too but that is what we are focused on here.
             * To that end, squaring a small number makes a small number, squaring a big number makes
             * a big number. Sum these squares to create the largest values for 100% risk and exposure vals.
             */

            if(list1.isNullOrEmpty()){
                return listOf(0)
            }
            // square each value in the list, return list in same order
            val sqrVals1 = list1.map{n: Int -> n * n}
            val sqrVals2 = list2.map{n: Int -> n * n}
            // sum the indexed values from both lists, return single list
            /** Note: multiplied to scale 0-100 */
            return sqrVals1.zip(sqrVals2) {a, b -> ((a+b)*.60).toInt()}

        }
        val combSumSq = sumSquares(riskVals, exposureVals)
        val combSqrt = combSumSq.map { x -> sqrt(x + 0.0).toInt() }

        return mapOf("median" to calcMedian(combSqrt),
                     "upQt" to calcUpperQuartile(combSqrt),
                     "max" to combSqrt.max())
    }

    fun statusHeader(statusScalar: Int) : String{
        val statusStr = when(statusScalar){
            in 0..1 -> "Create some risk entries!"
            in 2..10 -> "Cool cool, keepin it safe"
            in 11..20 -> "Stay safe out there, starfighter"
            in 21..30 -> "Protect ya neck"
            in 31..40 -> "Maybe slow down a bit"
            in 41..50 -> "U risky, yo"
            in 51..60 -> "Your health is basically a coin flip"
            in 61..70 -> "Consider finding a test location."
            in 71..80 -> "70%+ chance you encountered the 'Rona. Life choices."
            else -> "Get tested, brah"
        }
        return statusStr
    }
}