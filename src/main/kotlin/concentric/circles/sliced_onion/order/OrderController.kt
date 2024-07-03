package concentric.circles.sliced_onion.order

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/order")
class OrderController(
    val orderService: OrderService
) {

    @GetMapping
    fun getOrders(): List<Order> {
        return orderService.getOrders()
    }

    @PostMapping
    fun postOrder(@RequestBody orderDto: OrderDto): ResponseEntity<Order> {
        val order = orderService.createOrder(orderDto) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(order)
    }
}