package concentric.circles.sliced_onion.customer.internal

import concentric.circles.sliced_onion.order.OrderEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) {
    fun getCustomers(): List<Customer> = customerRepository.findAll()

    fun getCustomer(customerId: UUID): Customer? = customerRepository.findByCustomerId(customerId)

    fun createCustomer(customerDto: CustomerDto): Customer? {
        val customer = customerRepository.save(Customer(customerDto))
        return customer
    }

    fun updateCustomer(customerId: UUID, customerDto: CustomerDto): Customer? {
        val customer = customerRepository.findByCustomerId(customerId) ?: return null
        customer.firstName = customerDto.firstName
        customer.lastName = customerDto.lastName
        customer.email = customerDto.email
        customerRepository.save(customer)
        return customer
    }

    fun deleteCustomer(customer: Customer) {
        customerRepository.delete(customer)
    }

    @EventListener
    fun on(event: OrderEvent) {
        val customer = customerRepository.findByCustomerId(event.customerId)
        if (customer === null) throw Exception("No Customer found for this Customer ID")
    }
}