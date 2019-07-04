package demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

interface ILogging {
    val log: Logger
}

class LoggingImp(
    override val log: Logger
) : ILogging {
    companion object {
        operator inline fun <reified T> invoke(): LoggingImp {
            return LoggingImp(LoggerFactory.getLogger(T::class.java))
        }
    }
}
