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
    @OneToMany(mappedBy = "orderId", cascade = [(CascadeType.ALL)])
    val orderLineItems: MutableList<OrderLineItem>,

    @Column
    @Temporal(TemporalType.DATE)
    val created: Date = Date(),

    @Column
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.OPEN
) {
    constructor(customerId: UUID) : this(UUID.randomUUID(), customerId, mutableListOf(), Date(), OrderStatus.OPEN)
}