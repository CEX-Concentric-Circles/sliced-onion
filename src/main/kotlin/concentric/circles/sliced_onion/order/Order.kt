package concentric.circles.sliced_onion.order

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`order`")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    val orderId: UUID,

    @Column
    val productId: UUID,

    @Column
    @Temporal(TemporalType.DATE)
    val created: Date,

    @Column
    @Enumerated(EnumType.STRING)
    var status: OrderStatus
) {
    constructor(orderDto: OrderDto) : this(UUID.randomUUID(), orderDto.productId, orderDto.created, orderDto.status)
}