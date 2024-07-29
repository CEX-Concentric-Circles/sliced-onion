package concentric.circles.sliced_onion.inventory.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InventoryRepository : JpaRepository<Inventory, UUID> {
    fun findByInventoryId(inventoryId: UUID): Inventory?

    fun findByProductId(productId: UUID): Inventory?
}