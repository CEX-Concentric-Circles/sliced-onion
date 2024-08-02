package concentric.circles.sliced_onion.unit.order

import concentric.circles.sliced_onion.OrderEvent
import concentric.circles.sliced_onion.order.internal.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@ExtendWith(MockitoExtension::class)
class OrderServiceTest {

    @Mock
    lateinit var eventPublisher: ApplicationEventPublisher

    @Mock
    lateinit var orderRepository: OrderRepository

    @InjectMocks
    lateinit var orderService: OrderService

    @Captor
    lateinit var orderEventCaptor: ArgumentCaptor<OrderEvent>

    @Test
    fun `should create order and publish event`() {
        val productIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val order = Order()

        `when`(orderRepository.save(any(Order::class.java))).thenReturn(order)

        val createdOrder = orderService.createOrder(productIds)

        assertNotNull(createdOrder)
        verify(orderRepository, times(2)).save(any(Order::class.java))
        verify(eventPublisher).publishEvent(orderEventCaptor.capture())

        val capturedEvent = orderEventCaptor.value
        assertEquals(productIds, capturedEvent.productIds)
        assertEquals(OrderStatus.OPEN.toString(), capturedEvent.status)
    }

    @Test
    fun `should complete order and publish event`() {
        val order = Order()
        val orderId = order.orderId
        order.orderLineItems.add(OrderLineItem(orderId, UUID.randomUUID()))

        `when`(orderRepository.findByOrderId(orderId)).thenReturn(order)
        `when`(orderRepository.save(order)).thenReturn(order)

        val completedOrder = orderService.completeOrder(orderId)

        assertNotNull(completedOrder)
        assertEquals(OrderStatus.COMPLETED, completedOrder!!.status)
        verify(orderRepository).findByOrderId(orderId)
        verify(orderRepository).save(order)
        verify(eventPublisher).publishEvent(orderEventCaptor.capture())

        val capturedEvent = orderEventCaptor.value
        assertEquals(order.orderLineItems.map { it.productId }, capturedEvent.productIds)
        assertEquals(OrderStatus.COMPLETED.toString(), capturedEvent.status)
    }

    @Test
    fun `should delete order`() {
        val order = Order()

        doNothing().`when`(orderRepository).delete(order)

        orderService.deleteOrder(order)

        verify(orderRepository).delete(order)
    }
}
