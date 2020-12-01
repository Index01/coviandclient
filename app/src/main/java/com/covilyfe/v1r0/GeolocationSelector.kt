package com.covilyfe.v1r0

class GeolocationSelector {
    companion object{
        val Country: String = ""
        val State: String = ""
        val County: String = ""
        val City: String = ""
    }

    fun lookupAbbreviationByState(key: String): String? {
        val stateMap = mapOf<String, String>(
            "ALABAMA" to "AL",
            "ALASKA" to "AK",
            "ARIZONA" to "AZ",
            "ARKANSAS" to "AR",
            "CALIFORNIA" to "CA",
            "COLORADO" to "CO",
            "CONNECTICUT" to "CT",
            "DELAWARE" to "DE",
            "FLORIDA" to "FL",
            "GEORGIA" to "GA",
            "HAWAII" to "HI",
            "IDAHO" to "ID",
            "ILLINOIS" to "IL",
            "INDIANA" to "IN",
            "IOWA" to "IA",
            "KANSAS" to "KS",
            "KENTUCKY" to "KY",
            "LOUISIANA" to "LA",
            "MAINE" to "ME",
            "MARYLAND" to "MD",
            "MASSACHUSETTS" to "MA",
            "MICHIGAN" to "MI",
            "MINNESOTA" to "MN",
            "MISSISSIPPI" to "MS",
            "MISSOURI" to "MO",
            "MONTANA" to "MT",
            "NEBRASKA" to "NE",
            "NEVADA" to "NV",
            "NEW HAMPSHIRE" to "NH",
            "NEW JERSEY" to "NJ",
            "NEW MEXICO" to "NM",
            "NEW YORK" to "NY",
            "NORTH CAROLINA" to "NC",
            "NORTH DAKOTA" to "ND",
            "OHIO" to "OH",
            "OKLAHOMA" to "OK",
            "OREGON" to "OR",
            "PENNSYLVANIA" to "PA",
            "RHODE ISLAND" to "RI",
            "SOUTH CAROLINA" to "SC",
            "SOUTH DAKOTA" to "SD",
            "TENNESSEE" to "TN",
            "TEXAS" to "TX",
            "UTAH" to "UT",
            "VERMONT" to "VT",
            "VIRGINIA" to "VA",
            "WASHINGTON" to "WA",
            "WEST VIRGINIA" to "WV",
            "WISCONSIN" to "WI",
            "WYOMING" to "WY",

            "GUAM" to "GU",
            "PUERTO RICO" to "PR",
            "VIRGIN ISLANDS" to "VI"
        )
        return stateMap.get(key)
    }
}
