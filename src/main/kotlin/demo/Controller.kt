package demo

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.math.BigDecimal

@RestController
class DemoController(
    private val demoConfig: DemoConfig
) {
    @PostMapping
    suspend fun processRequest(@RequestBody serviceRequest: ServiceRequest): Response = coroutineScope {
        val userInfoDeferred = async {
            val authInfo = getAuthInfo(serviceRequest.authToken).awaitFirst()
            findUser(authInfo.userId).awaitFirst()
        }

        val paymentInfoDeferred = async {
            val cardFromInfoDeferred = async { findCardInfo(serviceRequest.cardFrom).awaitFirst() }
            val cardToInfoDeferred = async { findCardInfo(serviceRequest.cardTo).awaitFirst() }

            val cardFromInfo = cardFromInfoDeferred.await()
            sendMoney(cardFromInfo.cardId, cardToInfoDeferred.await().cardId, serviceRequest.amount).awaitFirst()

            getPaymentInfo(cardFromInfo.cardId).awaitFirst()
        }

        val userInfo = userInfoDeferred.await()
        val paymentInfo = paymentInfoDeferred.await()

        SuccessResponse(
            amount = paymentInfo.currentAmount,
            userName = userInfo.name,
            userSurname = userInfo.surname,
            userAge = userInfo.age
        )
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
