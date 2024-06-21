package concentric.circles.sliced_onion

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModularityTests {

    private var modules = ApplicationModules.of(SlicedOnionApplication::class.java)

    @Test
    fun verifyModularity() {
        modules.forEach{ println(it) }
    }
}