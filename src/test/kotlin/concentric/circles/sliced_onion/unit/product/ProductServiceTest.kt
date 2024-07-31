package concentric.circles.sliced_onion.unit.product

import concentric.circles.sliced_onion.product.ProductCreated
import concentric.circles.sliced_onion.product.ProductDeleted
import concentric.circles.sliced_onion.product.internal.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.springframework.context.ApplicationEventPublisher
import java.util.*

class ProductServiceTest {

    private val eventPublisher: ApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher::class.java)
    private val productRepository: ProductRepository = Mockito.mock(ProductRepository::class.java)
    private val productService = ProductService(eventPublisher, productRepository)

    @Test
    fun `should create product`() {
        val productDto = ProductDto(null, "Product Name", Price(100.0))
        val product = Product(productDto)

        `when`(productRepository.save(any(Product::class.java)))
            .thenReturn(product)

        val result = productService.createProduct(productDto)

        assertEquals(product.productId, result?.productId)
        assertEquals(productDto.name, result?.name)
        assertEquals(productDto.price, result?.price)

        val eventCaptor = ArgumentCaptor.forClass(ProductCreated::class.java)
        verify(eventPublisher).publishEvent(eventCaptor.capture())
        assertEquals(product.productId, eventCaptor.value.productId)
    }

    @Test
    fun `should update product`() {
        val productId = UUID.randomUUID()
        val productDto = ProductDto(productId, "Updated Name", Price(150.0))
        val product = Product(productId, "Old Name", Price(100.0))

        `when`(productRepository.findByProductId(productId))
            .thenReturn(product)
        `when`(productRepository.save(any(Product::class.java)))
            .thenReturn(product.apply {
                name = productDto.name
                price = productDto.price
            })

        val result = productService.updateProduct(productId, productDto)

        assertEquals(productId, result?.productId)
        assertEquals("Updated Name", result?.name)
        assertEquals(Price(150.0).amount, result?.price?.amount)
    }

    @Test
    fun `should return null when updating non-existent product`() {
        val productId = UUID.randomUUID()
        val productDto = ProductDto(productId, "Updated Name", Price(150.0))

        `when`(productRepository.findByProductId(productId))
            .thenReturn(null)

        val result = productService.updateProduct(productId, productDto)

        assertEquals(null, result)
    }

    @Test
    fun `should delete product and publish event`() {
        val productId = UUID.randomUUID()
        val product = Product(productId, "Product Name", Price(100.0))

        `when`(productRepository.findByProductId(productId))
            .thenReturn(product)

        productService.deleteProduct(product)

        verify(productRepository).delete(product)
        val eventCaptor = ArgumentCaptor.forClass(ProductDeleted::class.java)
        verify(eventPublisher).publishEvent(eventCaptor.capture())
        assertEquals(productId, eventCaptor.value.productId)
    }
}
