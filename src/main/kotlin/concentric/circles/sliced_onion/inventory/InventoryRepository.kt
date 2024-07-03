package concentric.circles.sliced_onion.inventory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InventoryRepository : JpaRepository<Inventory, UUID> {
    fun findByProductId(productId: UUID): Inventory?
}