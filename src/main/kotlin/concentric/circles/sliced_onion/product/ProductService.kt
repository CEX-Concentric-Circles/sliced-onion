package concentric.circles.sliced_onion.product

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val eventPublisher: ApplicationEventPublisher,
    private val productRepository: ProductRepository
) {

    fun getProducts(): List<Product> = productRepository.findAll()

    @Transactional
    fun createProduct(productDto: ProductDto): Product? {
        val product = Product(productDto)
        productRepository.save(product)
        eventPublisher.publishEvent(ProductCreated(product.productId))
        return product
    }
}