package concentric.circles.sliced_onion.order.internal

import java.util.*

class OrderDto(
    val orderId: UUID?, val productId: UUID, val created: Date?, val status: OrderStatus?
) {
    constructor(order: Order) : this(order.orderId, order.productId, order.created, order.status)
}