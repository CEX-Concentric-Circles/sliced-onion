package concentric.circles.sliced_onion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SlicedOnionApplication

fun main(args: Array<String>) {
    runApplication<SlicedOnionApplication>(*args)
}
