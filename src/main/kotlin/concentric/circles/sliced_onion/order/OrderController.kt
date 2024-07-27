package concentric.circles.sliced_onion.order

import concentric.circles.sliced_onion.order.internal.Order
import concentric.circles.sliced_onion.order.internal.OrderDto
import concentric.circles.sliced_onion.order.internal.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/order")
class OrderController(
    val orderService: OrderService
) {

    @GetMapping
    fun getOrders(): ResponseEntity<List<OrderDto>> =
        ResponseEntity.ok().body(orderService.getOrders().map { order: Order -> OrderDto(order) })

    @PostMapping
    fun postOrder(@RequestBody orderDto: OrderDto): ResponseEntity<OrderDto?> {
        val order = orderService.createOrder(orderDto.productId) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(OrderDto(order))
    }

    @PutMapping("/{orderId}/complete")
    fun completeOrder(@PathVariable orderId: UUID): ResponseEntity<OrderDto> {
        val order: Order = orderService.completeOrder(orderId) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(OrderDto((order)))
    }
}