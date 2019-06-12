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

    /**
    {
     "authToken": "auth-token1",
     "cardFrom": "55593478",
     "cardTo": "55592020",
     "amount": "10.1"
    }

     p.s. start amount for all cards is 1000
     */
    @PostMapping
    fun processRequest(@RequestBody serviceRequest: ServiceRequest): Response {
        //1) get auth info from service by token -> userId

        //2) find user info by userId from 1.

        //3) 4) find cards info for each card in serviceRequest

        // 5) make transaction for known cards by calling sendMoney(id1, id2, amount)

        // 6) after payment get payment info by fromCardId

        TODO("return SuccessResponse")
//        SuccessResponse(
//            amount = ,
//            userName = ,
//            userSurname = ,
//            userAge =
//        )
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
