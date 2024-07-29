package concentric.circles.sliced_onion.order.internal

import java.util.*

class OrderDto(
    val orderId: UUID?, val productIds: List<UUID>, val created: Date?, val status: OrderStatus?
) {
    constructor(order: Order) : this(
        order.orderId,
        order.orderLineItems.map { orderLineItem: OrderLineItem -> orderLineItem.productId },
        order.created,
        order.status
    )
}