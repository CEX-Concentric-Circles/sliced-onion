package concentric.circles.sliced_onion.unit.inventory

import concentric.circles.sliced_onion.inventory.internal.Inventory
import concentric.circles.sliced_onion.inventory.internal.InventoryRepository
import concentric.circles.sliced_onion.inventory.internal.InventoryService
import concentric.circles.sliced_onion.order.OrderEvent
import concentric.circles.sliced_onion.product.ProductCreated
import concentric.circles.sliced_onion.product.ProductDeleted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@ExtendWith(MockitoExtension::class)
class InventoryServiceTest {
    private val eventPublisher: ApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher::class.java)
    private val inventoryRepository: InventoryRepository = Mockito.mock(InventoryRepository::class.java)
    private val inventoryService = InventoryService(eventPublisher, inventoryRepository)

    @Test
    fun `should return all inventories`() {
        val inventories = listOf(
            Inventory(UUID.randomUUID(), UUID.randomUUID(), 10),
            Inventory(UUID.randomUUID(), UUID.randomUUID(), 5)
        )
        Mockito.`when`(inventoryRepository.findAll()).thenReturn(inventories)

        val result = inventoryService.getInventories()

        assertEquals(inventories, result)
    }

    @Test
    fun `should return inventory by id`() {
        val inventoryId = UUID.randomUUID()
        val inventory = Inventory(inventoryId, UUID.randomUUID(), 10)
        Mockito.`when`(inventoryRepository.findByInventoryId(inventoryId)).thenReturn(inventory)

        val result = inventoryService.getInventory(inventoryId)

        assertEquals(inventory, result)
    }

    @Test
    fun `should increase inventory quantity`() {
        val inventoryId = UUID.randomUUID()
        val inventory = Inventory(inventoryId, UUID.randomUUID(), 10)
        Mockito.`when`(inventoryRepository.findByInventoryId(inventoryId)).thenReturn(inventory)
        Mockito.`when`(inventoryRepository.save(inventory)).thenReturn(inventory)

        val result = inventoryService.increaseInventoryQuantity(inventoryId, 5)

        assertEquals(15, result?.quantity)
    }

    @Test
    fun `should handle ProductCreated event`() {
        val productId = UUID.randomUUID()
        val event = ProductCreated(productId)
        Mockito.`when`(inventoryRepository.findByProductId(productId)).thenReturn(null)
        Mockito.`when`(inventoryRepository.save(Mockito.any())).thenReturn(Inventory(UUID.randomUUID(), productId, 0))

        inventoryService.on(event)

        Mockito.verify(inventoryRepository).save(Mockito.any())
    }

    @Test
    fun `should handle ProductDeleted event`() {
        val productId = UUID.randomUUID()
        val inventory = Inventory(UUID.randomUUID(), productId, 10)
        Mockito.`when`(inventoryRepository.findByProductId(productId)).thenReturn(inventory)
        Mockito.doNothing().`when`(inventoryRepository).delete(inventory)

        inventoryService.on(ProductDeleted(productId))

        Mockito.verify(inventoryRepository).delete(inventory)
    }

    @Test
    fun `should handle OrderEvent`() {
        val productId = UUID.randomUUID()
        val inventory = Inventory(UUID.randomUUID(), productId, 10)
        val orderEvent = OrderEvent(listOf(productId), "COMPLETED")
        Mockito.`when`(inventoryRepository.findByProductId(productId)).thenReturn(inventory)
        Mockito.`when`(inventoryRepository.save(inventory)).thenReturn(inventory)

        inventoryService.on(orderEvent)

        assertEquals(9, inventory.quantity)
        Mockito.verify(inventoryRepository).save(inventory)
    }
}
