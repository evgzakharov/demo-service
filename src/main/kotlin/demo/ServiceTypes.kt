package demo

import java.math.BigDecimal

/**
 * auth service
 */
data class AuthInfo(
    val userId: Long,
    val cardAccess: Boolean = false,
    val paymentAccess: Boolean = false,
    val userAccess: Boolean = false
)

/**
 * card service
 */
data class CardInfo(
    val cardId: Long,
    val cardNumber: String,
    val validTo: String
)

/**
 * payment service
 */
data class PaymentRequest(
    val cardIdFrom: Long,
    val cardIdTo: Long,
    val amount: BigDecimal
)

data class PaymentSuccessInfo(
    val status: Boolean = true
)

data class TransactionInfo(
    val transactionInfo: String,
    val historyCardAmount: BigDecimal
)

data class PaymentTransactionInfo(
    val currentAmount: BigDecimal,
    val transactions: List<TransactionInfo>
)

/**
 * user service
 */
data class UserInfo(
    val id: Long,
    val name: String,
    val surname: String,
    val age: Int
)
