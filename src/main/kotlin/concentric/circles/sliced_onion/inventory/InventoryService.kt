package concentric.circles.sliced_onion.inventory

import concentric.circles.sliced_onion.order.OrderCompleted
import concentric.circles.sliced_onion.product.ProductCreated
import org.springframework.context.event.EventListener
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository
) {

    fun getInventories(): List<Inventory> = inventoryRepository.findAll()

    @ApplicationModuleListener
    fun on(event: ProductCreated) {
        if (inventoryRepository.findByProductId(event.productId) == null) {
            val inventory = Inventory(
                UUID.randomUUID(),
                event.productId,
                0
            )
            inventoryRepository.save(inventory)
        }
    }

    @EventListener
    fun on(event: OrderCompleted) {
        val inventory = inventoryRepository.findByProductId(event.productId)

        if (inventory === null) {
            throw Exception("No Inventory found for this Product ID")
        }

        if (inventory.stockCount <= 0) {
            throw Exception("Product already out of stock")
        }

        inventory.stockCount -= 1

        inventoryRepository.save(inventory)
    }
}