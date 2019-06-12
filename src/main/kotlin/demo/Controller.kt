package demo

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.function.Tuple2
import java.math.BigDecimal

@RestController
class DemoController(
    private val demoConfig: DemoConfig
) {
    @PostMapping
    fun processRequest(@RequestBody serviceRequest: Mono<ServiceRequest>): Mono<Response> {
//        val authInfo = getAuthInfo(serviceRequest.authToken)
//
//        val userInfo = findUser(authInfo.userId)
//
//        val cardFromInfo = findCardInfo(serviceRequest.cardFrom)
//        val cardToInfo = findCardInfo(serviceRequest.cardTo)
//
//        sendMoney(cardFromInfo.cardId, cardToInfo.cardId, serviceRequest.amount)
//
//        val paymentInfo = getPaymentInfo(cardFromInfo.cardId)
//
//        return SuccessResponse(
//            amount = paymentInfo.currentAmount,
//            userName = userInfo.name,
//            userSurname = userInfo.surname,
//            userAge = userInfo.age
//        )

        TODO()
    }

    private fun getPaymentInfo(cardId: Long): Mono<PaymentTransactionInfo> {
        return WebClient.create().get()
            .uri("${demoConfig.payment}/$cardId")
            .retrieve()
            .bodyToMono(PaymentTransactionInfo::class.java)
    }

    private fun sendMoney(cardIdFrom: Long, cardIdTo: Long, amount: BigDecimal): Mono<Unit> {
        val paymentRequest = PaymentRequest(cardIdFrom, cardIdTo, amount)

        return WebClient.create().post()
            .uri(demoConfig.payment)
            .body(Mono.just(paymentRequest), PaymentRequest::class.java)
            .retrieve()
            .bodyToMono(PaymentSuccessInfo::class.java)
            .map { Unit }
    }

    private fun findCardInfo(cardNumber: String): Mono<CardInfo> {
        return WebClient.create().get()
            .uri("${demoConfig.card}/$cardNumber")
            .retrieve()
            .bodyToMono(CardInfo::class.java)
    }

    private fun findUser(userId: Long): Mono<UserInfo> {
        return WebClient.create().get()
            .uri("${demoConfig.user}/$userId")
            .retrieve()
            .bodyToMono(UserInfo::class.java)
    }

    private fun getAuthInfo(token: String): Mono<AuthInfo> {
        return WebClient.create().get()
            .uri("${demoConfig.auth}/$token")
            .retrieve()
            .bodyToMono(AuthInfo::class.java)
    }

    private operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1

    private operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2
}
