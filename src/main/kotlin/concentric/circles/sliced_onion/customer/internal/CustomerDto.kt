package concentric.circles.sliced_onion.customer.internal

import java.util.*

class CustomerDto(val customerId: UUID?, val firstName: String, val lastName: String, val email: String) {
    constructor(customer: Customer) : this(customer.customerId, customer.firstName, customer.lastName, customer.email)
}