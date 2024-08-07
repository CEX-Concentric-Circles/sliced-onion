package concentric.circles.sliced_onion.inventory.internal

import concentric.circles.sliced_onion.inventory.InventoryEvent
import concentric.circles.sliced_onion.order.OrderEvent
import concentric.circles.sliced_onion.product.ProductCreated
import concentric.circles.sliced_onion.product.ProductDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class InventoryService(
    private val eventPublisher: ApplicationEventPublisher,
    private val inventoryRepository: InventoryRepository
) {

    fun getInventories(): List<Inventory> = inventoryRepository.findAll()

    fun getInventory(inventoryId: UUID) = inventoryRepository.findByInventoryId(inventoryId)

    @Transactional
    fun createInventory(inventoryDto: InventoryDto): Inventory? {
        val inventory = Inventory(inventoryDto)
        eventPublisher.publishEvent(InventoryEvent(inventory.productId))
        return inventoryRepository.save(inventory)
    }

    fun increaseInventoryQuantity(inventoryId: UUID, quantity: Int): Inventory? {
        val inventory = inventoryRepository.findByInventoryId(inventoryId) ?: return null
        inventory.quantity += quantity
        return inventoryRepository.save(inventory)
    }

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
        for (productId in event.productIds) {
            val inventory = inventoryRepository.findByProductId(productId)

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
}