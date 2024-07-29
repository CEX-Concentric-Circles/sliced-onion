package concentric.circles.sliced_onion.order.internal

import concentric.circles.sliced_onion.order.OrderEvent
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
    fun createOrder(orderDto: OrderDto): Order? {
        val order = Order(orderDto.customerId, orderDto.productId)
        eventPublisher.publishEvent(OrderEvent(orderDto.customerId, orderDto.productId, order.status.toString()))
        return orderRepository.save(order)
    }

    @Transactional
    fun completeOrder(orderId: UUID): Order? {
        val order = orderRepository.findByOrderId(orderId) ?: return null
        if (order.status === OrderStatus.COMPLETED) return order

        order.status = OrderStatus.COMPLETED
        eventPublisher.publishEvent(OrderEvent(order.customerId, order.productId, order.status.toString()))
        return orderRepository.save(order)
    }

    @Transactional
    fun deleteOrder(order: Order) {
        orderRepository.delete(order)
    }
}