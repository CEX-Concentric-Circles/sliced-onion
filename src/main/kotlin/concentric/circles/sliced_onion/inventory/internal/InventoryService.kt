package concentric.circles.sliced_onion.inventory.internal

import concentric.circles.sliced_onion.order.OrderEvent
import concentric.circles.sliced_onion.product.ProductCreated
import concentric.circles.sliced_onion.product.ProductDeleted
import org.springframework.context.event.EventListener
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository
) {

    fun getInventories(): List<Inventory> = inventoryRepository.findAll()

    fun getInventory(inventoryId: UUID) = inventoryRepository.findByInventoryId(inventoryId)

    @ApplicationModuleListener
    fun on(event: ProductCreated) {
        if (inventoryRepository.findByProductId(event.productId) == null) {
            val inventory = Inventory(
                UUID.randomUUID(), event.productId, 0
            )
            inventoryRepository.save(inventory)
        }
    }

    @EventListener
    fun on(event: ProductDeleted) {
        val inventory = inventoryRepository.findByProductId(event.productId)

        if (inventory != null) {
            inventoryRepository.delete(inventory)
        }
    }

    @EventListener
    fun on(event: OrderEvent) {
        val inventory = inventoryRepository.findByProductId(event.productId)

        if (inventory === null) throw Exception("No Inventory found for this Product ID")

        if (inventory.quantity <= 0) throw Exception("Product already out of stock")

        when (event.status) {
            "COMPLETED" -> {
                inventory.quantity -= 1
                inventoryRepository.save(inventory)
            }
        }
    }
}