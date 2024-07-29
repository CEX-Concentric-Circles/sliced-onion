package concentric.circles.sliced_onion.order.internal

import jakarta.persistence.*
import java.util.*

@Entity
class OrderLineItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    val orderLineItemId: UUID,

    @Column
    val orderId: UUID,

    @Column
    val productId: UUID
) {
    constructor(orderId: UUID, productId: UUID) : this(UUID.randomUUID(), orderId, productId)
}