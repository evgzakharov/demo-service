package demo

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@RestController
class DemoController(
    private val demoConfig: DemoConfig,
    restTemplateBuilder: RestTemplateBuilder
) {
    private val restTemplate = restTemplateBuilder.build()

    @PostMapping
    fun processRequest(@RequestBody serviceRequest: ServiceRequest): Response {
        val authInfoFuture = CompletableFuture.supplyAsync {  getAuthInfo(serviceRequest.authToken) }
        val userInfoFuture = authInfoFuture.thenApplyAsync { findUser(it.userId) }

        val cardFromInfo = CompletableFuture.supplyAsync { findCardInfo(serviceRequest.cardFrom) }
        val cardToInfo = CompletableFuture.supplyAsync { findCardInfo(serviceRequest.cardTo) }

        val waitAll = CompletableFuture.allOf(cardFromInfo, cardToInfo)

        val paymentInfoFuture = waitAll
            .thenApplyAsync {
                sendMoney(cardFromInfo.get().cardId, cardToInfo.get().cardId, serviceRequest.amount)
            }
            .thenApplyAsync {
                getPaymentInfo(cardFromInfo.get().cardId)
            }

        val paymentInfo = paymentInfoFuture.get()
        val userInfo = userInfoFuture.get()

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
