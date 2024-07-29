package concentric.circles.sliced_onion.inventory.internal

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`inventory`")
class Inventory(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    val inventoryId: UUID,

    @Column
    val productId: UUID,

    @Column
    var quantity: Int = 0
) {
    constructor(inventoryDto: InventoryDto) : this(UUID.randomUUID(), inventoryDto.productId, inventoryDto.quantity)
}