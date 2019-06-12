package demo

import java.math.BigDecimal

interface Response {
    val status: Boolean
}

data class ErrorResponse(
    val error: String,
    override val status: Boolean = false
): Response

data class SuccessResponse(
    val amount: BigDecimal,
    val userName: String,
    val userSurname: String,
    val userAge: Int,
    override val status: Boolean = true
) : Response
