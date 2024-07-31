package concentric.circles.sliced_onion.unit.product

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import java.util.*
import concentric.circles.sliced_onion.product.internal.*
import org.junit.jupiter.api.Assertions.assertEquals

class ProductControllerTest {

    private val productService: ProductService = Mockito.mock(ProductService::class.java)
    private val productController = ProductController(productService)

    @Test
    fun `should return product`() {
        val productId = UUID.randomUUID()
        val product = Product(productId, "Product Name", Price(100.0))

        `when`(productService.getProduct(productId))
            .thenReturn(product)

        val response = productController.getProduct(productId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(productId, response.body?.productId)
        assertEquals("Product Name", response.body?.name)
        assertEquals(Price(100.0).amount, response.body?.price?.amount)
        assertEquals(Price(100.0).currency, response.body?.price?.currency)
    }

    @Test
    fun `should return not found when product does not exist`() {
        val productId = UUID.randomUUID()

        `when`(productService.getProduct(productId))
            .thenReturn(null)

        val response = productController.getProduct(productId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should update product`() {
        val productDto = ProductDto(UUID.randomUUID(), "Updated Name", Price(150.0))
        val updatedProduct = Product(productDto)
        val productId = updatedProduct.productId

        `when`(productService.updateProduct(productId, productDto))
            .thenReturn(updatedProduct)

        val response = productController.updateProduct(productId, productDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(productId, response.body?.productId)
        assertEquals(productDto.name, response.body?.name)
        assertEquals(productDto.price, response.body?.price)
    }

    @Test
    fun `should return bad request when update fails`() {
        val productId = UUID.randomUUID()
        val productDto = ProductDto(productId, "Updated Name", Price(150.0))

        `when`(productService.updateProduct(productId, productDto))
            .thenReturn(null)

        val response = productController.updateProduct(productId, productDto)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should delete product`() {
        val productId = UUID.randomUUID()
        val product = Product(productId, "Product Name", Price(100.0))

        `when`(productService.getProduct(productId))
            .thenReturn(product)

        val response = productController.deleteProduct(productId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(productService).deleteProduct(product)
    }

    @Test
    fun `should return not found when deleting non-existent product`() {
        val productId = UUID.randomUUID()

        `when`(productService.getProduct(productId))
            .thenReturn(null)

        val response = productController.deleteProduct(productId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
