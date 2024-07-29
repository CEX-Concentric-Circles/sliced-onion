package concentric.circles.sliced_onion.order.internal

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
    val customerId: UUID,

    @Column
    val productId: UUID,

    @Column
    @Temporal(TemporalType.DATE)
    val created: Date = Date(),

    @Column
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.OPEN
) {
    constructor(customerId: UUID, productId: UUID) : this(
        UUID.randomUUID(),
        customerId,
        productId,
        Date(),
        OrderStatus.OPEN
    )

    constructor(orderDto: OrderDto) : this(
        UUID.randomUUID(),
        orderDto.customerId,
        orderDto.productId,
        orderDto.created!!,
        orderDto.status!!
    )
}