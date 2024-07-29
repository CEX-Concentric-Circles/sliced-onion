package concentric.circles.sliced_onion

import concentric.circles.sliced_onion.product.internal.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SlicedOnionApplicationTests(private val productService: ProductService) {

    @Test
    fun crudProductTest() {
        val productDto = ProductDto(null, "TestProduct", Price(69.420, Currency.EUR))
        val product = productService.createProduct(productDto)

    }

}
