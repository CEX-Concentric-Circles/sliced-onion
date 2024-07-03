package concentric.circles.sliced_onion.inventory

import java.util.*

class InventoryDto(
    val inventoryId: UUID?,
    val productId: UUID,
    val stockCount: Int
) {
    constructor(inventory: Inventory) : this(inventory.inventoryId, inventory.productId, inventory.stockCount)
}