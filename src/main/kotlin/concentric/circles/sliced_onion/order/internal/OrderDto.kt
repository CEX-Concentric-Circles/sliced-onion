package concentric.circles.sliced_onion.order.internal

import java.util.*

class OrderDto(
    val orderId: UUID?,
    val customerId: UUID,
    val productId: UUID,
    val created: Date?,
    val status: OrderStatus?
) {
    constructor(order: Order) : this(order.orderId, order.customerId, order.productId, order.created, order.status)
}