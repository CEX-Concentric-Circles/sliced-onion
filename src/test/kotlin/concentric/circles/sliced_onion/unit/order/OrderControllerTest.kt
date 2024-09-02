package concentric.circles.sliced_onion.unit.order

import concentric.circles.sliced_onion.order.internal.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockitoExtension::class)
class OrderControllerTest {

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var orderService: OrderService

    @InjectMocks
    private lateinit var orderController: OrderController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build()
    }

    @Test
    fun `should return all orders`() {
        val orders = listOf(Order(), Order())
        `when`(orderService.getOrders()).thenReturn(orders)

        mockMvc.perform(get("/order"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `should create order`() {
        val productIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val orderDto = OrderDto(null, productIds, null, null)
        val order = Order()

        `when`(orderService.createOrder(orderDto.productIds)).thenReturn(order)

        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productIds\":[\"${productIds[0]}\",\"${productIds[1]}\"]}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.orderId").value(order.orderId.toString()))
    }

    @Test
    fun `should return 400 when creating order with invalid data`() {
        val invalidOrderDto = "{ \"productIds\": [] }"

        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidOrderDto)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return order by id`() {
        val order = Order()
        val orderId = order.orderId
        `when`(orderService.getOrder(orderId)).thenReturn(order)

        mockMvc.perform(get("/order/$orderId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.orderId").value(orderId.toString()))
    }

    @Test
    fun `should return 404 when order not found`() {
        val orderId = UUID.randomUUID()
        `when`(orderService.getOrder(orderId)).thenReturn(null)

        mockMvc.perform(get("/order/$orderId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should delete order`() {
        val order = Order()
        val orderId = order.orderId
        `when`(orderService.getOrder(orderId)).thenReturn(order)

        mockMvc.perform(delete("/order/$orderId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should return 404 when deleting non-existing order`() {
        val orderId = UUID.randomUUID()
        `when`(orderService.getOrder(orderId)).thenReturn(null)

        mockMvc.perform(delete("/order/$orderId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should complete order`() {
        val order = Order()
        val orderId = order.orderId
        `when`(orderService.completeOrder(orderId)).thenAnswer {
            order.status = OrderStatus.COMPLETED
            order
        }

        mockMvc.perform(put("/order/$orderId/complete"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.orderId").value(orderId.toString()))
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.toString()))
    }

    @Test
    fun `should return 400 when completing non-existing order`() {
        val orderId = UUID.randomUUID()
        `when`(orderService.completeOrder(orderId)).thenReturn(null)

        mockMvc.perform(put("/order/$orderId/complete"))
            .andExpect(status().isBadRequest)
    }
}
