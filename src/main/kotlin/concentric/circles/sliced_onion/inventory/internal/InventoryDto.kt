package concentric.circles.sliced_onion.inventory.internal

import java.util.*

class InventoryDto(
    val inventoryId: UUID?,
    val productId: UUID,
    val quantity: Int
) {
    constructor(inventory: Inventory) : this(inventory.inventoryId, inventory.productId, inventory.quantity)
}