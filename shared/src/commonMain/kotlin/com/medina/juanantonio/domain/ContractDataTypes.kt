package com.medina.juanantonio.domain

object ContractDataTypes {
    val activityTypes = listOf("Swim", "Bike", "Run", "Walk")

    val defaultPriceOptions = listOf(
        Pair("₱10", 10),
        Pair("₱20", 20),
        Pair("₱50", 50),
        Pair("₱100", 100)
    )

    val defaultContractExpiration = listOf(
        Pair("7 Days", 7),
        Pair("14 Days", 14),
        Pair("30 Days", 30),
        Pair("60 Days", 60),
        Pair("No Limit", -1)
    )
}
