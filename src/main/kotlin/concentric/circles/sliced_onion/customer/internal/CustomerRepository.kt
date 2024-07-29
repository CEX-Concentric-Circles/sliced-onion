package concentric.circles.sliced_onion.customer.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerRepository : JpaRepository<Customer, UUID> {
    fun findByCustomerId(customerId: UUID): Customer?
}