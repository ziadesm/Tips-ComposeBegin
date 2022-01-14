package com.bawp.jettip_test.utils

fun calculateTotalTips(totalBill: String, sliderPercent: Int): Double {
    return if (totalBill.isNotEmpty() && totalBill.toDouble() > 1)
        (totalBill.toDouble() * sliderPercent) / 100
    else 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int
): Double {
    val bill = calculateTotalTips(totalBill.toString(), tipPercentage) + totalBill
    return bill / splitBy
}