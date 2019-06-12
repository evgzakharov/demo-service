package demo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "url")
@Validated
data class DemoConfig(
    val auth: String,
    val card: String,
    val payment: String,
    val user: String
)
