package concentric.circles.sliced_onion.customer.internal

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`customer`")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val customerId: UUID,

    @Column
    var firstName: String,

    @Column
    var lastName: String,

    @Column
    var email: String
) {
    constructor(customerDto: CustomerDto) : this(
        UUID.randomUUID(),
        customerDto.firstName,
        customerDto.lastName,
        customerDto.email
    )
}