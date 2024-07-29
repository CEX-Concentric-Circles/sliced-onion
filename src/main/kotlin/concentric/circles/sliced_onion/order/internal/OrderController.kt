package concentric.circles.sliced_onion.order.internal

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/order")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getOrders(): ResponseEntity<List<OrderDto>> =
        ResponseEntity.ok().body(orderService.getOrders().map { order: Order -> OrderDto(order) })

    @PostMapping
    fun createOrder(@RequestBody orderDto: OrderDto): ResponseEntity<OrderDto?> {
        val order = orderService.createOrder(orderDto.productIds) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(OrderDto(order))
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: UUID): ResponseEntity<OrderDto?> {
        val order = orderService.getOrder(orderId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(OrderDto(order))
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrder(@PathVariable orderId: UUID): ResponseEntity<OrderDto> {
        val order = orderService.getOrder(orderId) ?: return ResponseEntity.notFound().build()
        orderService.deleteOrder(order)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{orderId}/complete")
    fun completeOrder(@PathVariable orderId: UUID): ResponseEntity<OrderDto> {
        val order: Order = orderService.completeOrder(orderId) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(OrderDto((order)))
    }
}