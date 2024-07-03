package concentric.circles.sliced_onion.order

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val eventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository
) {

    fun getOrders() = orderRepository.findAll()

    @Transactional
    fun createOrder(orderDto: OrderDto): Order? {
        val order = Order(orderDto)
        return orderRepository.save(order)
    }

    @Transactional
    fun completeOrder(order: Order) {
        eventPublisher.publishEvent(OrderCompleted(order.orderId))
        order.status = OrderStatus.COMPLETED
        orderRepository.save(order)
    }
}