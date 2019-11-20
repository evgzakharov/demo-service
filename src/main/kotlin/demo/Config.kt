package demo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "url")
data class DemoConfig(
    val auth: String,
    val card: String,
    val payment: String,
    val user: String
)
