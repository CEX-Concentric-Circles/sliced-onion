package concentric.circles.sliced_onion.product.internal

import concentric.circles.sliced_onion.product.ProductCreated
import concentric.circles.sliced_onion.product.ProductDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProductService(
    private val eventPublisher: ApplicationEventPublisher,
    private val productRepository: ProductRepository
) {

    fun getProducts(): List<Product> = productRepository.findAll()

    fun getProduct(productId: UUID): Product? = productRepository.findByProductId(productId)

    @Transactional
    fun createProduct(productDto: ProductDto): Product? {
        val product = productRepository.save(Product(productDto))
        eventPublisher.publishEvent(ProductCreated(product.productId))
        return product
    }

    fun updateProduct(productId: UUID, productDto: ProductDto): Product? {
        val product: Product = productRepository.findByProductId(productId) ?: return null
        product.name = productDto.name
        product.price = productDto.price
        productRepository.save(product)
        return product
    }

    @Transactional
    fun deleteProduct(product: Product) {
        productRepository.delete(product)
        eventPublisher.publishEvent(ProductDeleted(product.productId))
    }
}