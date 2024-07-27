package concentric.circles.sliced_onion.product.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : JpaRepository<Product, UUID> {
    fun findByProductId(productId: UUID): Product?
}