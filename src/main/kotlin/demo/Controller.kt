package demo

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class DemoController(
    private val demoConfig: DemoConfig,
    restTemplateBuilder: RestTemplateBuilder
) {
    private val restTemplate = restTemplateBuilder.build()

    @PostMapping
    fun processRequest(@RequestBody serviceRequest: ServiceRequest): Response {
        val authInfo = getAuthInfo(serviceRequest.authToken)

        val userInfo = findUser(authInfo.userId)

        val cardFromInfo = findCardInfo(serviceRequest.cardFrom)
        val cardToInfo = findCardInfo(serviceRequest.cardTo)

        sendMoney(cardFromInfo.cardId, cardToInfo.cardId, serviceRequest.amount)

        val paymentInfo = getPaymentInfo(cardFromInfo.cardId)

        return SuccessResponse(
            amount = paymentInfo.currentAmount,
            userName = userInfo.name,
            userSurname = userInfo.surname,
            userAge = userInfo.age
        )
    }

    private fun getPaymentInfo(cardId: Long): PaymentTransactionInfo {
        return restTemplate.getForEntity("${demoConfig.payment}/{cardId}", PaymentTransactionInfo::class.java, cardId)
            .body ?: throw RuntimeException("couldn't find card info with cardId='$cardId'")
    }

    private fun sendMoney(cardIdFrom: Long, cardIdTo: Long, amount: BigDecimal) {
        val paymentRequest = PaymentRequest(cardIdFrom, cardIdTo, amount)

        restTemplate.postForEntity(demoConfig.payment, paymentRequest, PaymentSuccessInfo::class.java)
            .body ?: throw RuntimeException("error while send payment request")
    }

    private fun findCardInfo(cardNumber: String): CardInfo {
        return restTemplate.getForEntity("${demoConfig.card}/{cardNumber}", CardInfo::class.java, cardNumber)
            .body ?: throw RuntimeException("couldn't find card with number='$cardNumber'")
    }

    private fun findUser(userId: Long): UserInfo {
        return restTemplate.getForEntity("${demoConfig.user}/{userId}", UserInfo::class.java, userId)
            .body ?: throw RuntimeException("couldn't find user by userId='$userId'")
    }

    private fun getAuthInfo(token: String): AuthInfo {
        return restTemplate.getForEntity("${demoConfig.auth}/{token}", AuthInfo::class.java, token)
            .body ?: throw RuntimeException("couldn't find user by token='$token'")
    }


}
