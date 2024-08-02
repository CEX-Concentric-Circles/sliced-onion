package concentric.circles.sliced_onion.order.internal

import concentric.circles.sliced_onion.OrderEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OrderService(
    private val eventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository
) {

    fun getOrders() = orderRepository.findAll()

    fun getOrder(orderId: UUID) = orderRepository.findByOrderId(orderId)

    @Transactional
    fun createOrder(customerId: UUID, productIds: List<UUID>): Order? {
        val order = orderRepository.save(Order(customerId))
        eventPublisher.publishEvent(
            OrderEvent(
                customerId,
                productIds,
                order.status.toString()
            )
        )

        order.orderLineItems.addAll(productIds.map { productId: UUID -> OrderLineItem(order.orderId, productId) })
        return orderRepository.save(order)
    }

    @Transactional
    fun completeOrder(orderId: UUID): Order? {
        val order = orderRepository.findByOrderId(orderId) ?: return null
        if (order.status === OrderStatus.COMPLETED) return order

        order.status = OrderStatus.COMPLETED
        eventPublisher.publishEvent(
            OrderEvent(
                order.customerId,
                order.orderLineItems.map { orderLineItem: OrderLineItem -> orderLineItem.productId },
                order.status.toString()
            )
        )
        return orderRepository.save(order)
    }

    @Transactional
    fun deleteOrder(order: Order) {
        orderRepository.delete(order)
    }
}