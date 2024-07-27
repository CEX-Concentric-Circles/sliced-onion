package concentric.circles.sliced_onion

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ModularityTests {

    private var modules = ApplicationModules.of(SlicedOnionApplication::class.java)

    @Test
    fun verifyModularity() {
        ApplicationModules.of(SlicedOnionApplication::class.java).verify()

    }


    @Test
    fun writeDocumentationSnippets() {
        Documenter(modules)
            .writeModulesAsPlantUml()
//            .writeIndividualModulesAsPlantUml();
    }
}