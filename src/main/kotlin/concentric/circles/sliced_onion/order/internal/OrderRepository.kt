package concentric.circles.sliced_onion.order.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : JpaRepository<Order, UUID> {

    fun findByOrderId(orderId: UUID): Order?

    override fun findAll(): List<Order>
}