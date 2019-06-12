package demo

import java.math.BigDecimal

data class ServiceRequest(
    val authToken: String,
    val cardFrom: String,
    val cardTo: String,
    val amount: BigDecimal
)
