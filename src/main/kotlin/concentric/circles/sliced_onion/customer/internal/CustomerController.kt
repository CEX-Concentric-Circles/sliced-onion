package concentric.circles.sliced_onion.customer.internal

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*


@RestController
@RequestMapping("/customer")
class CustomerController(
    private val customerService: CustomerService
) {
    @GetMapping
    fun getCustomers(): ResponseEntity<List<CustomerDto>> =
        ResponseEntity.ok().body(customerService.getCustomers().map { customer: Customer -> CustomerDto(customer) })

    @PostMapping
    fun createCustomer(@RequestBody customerDto: CustomerDto, ucb: UriComponentsBuilder): ResponseEntity<CustomerDto?> {
        val customer = customerService.createCustomer(customerDto) ?: return ResponseEntity.badRequest().body(null)
        val uriComponents = ucb.path("/customer/{customerId}").buildAndExpand(customer.customerId)
        return ResponseEntity.created(uriComponents.toUri()).body(CustomerDto(customer))
    }

    @GetMapping("/{customerId}")
    fun getCustomer(@PathVariable customerId: UUID): ResponseEntity<CustomerDto?> {
        val customer = customerService.getCustomer(customerId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(CustomerDto(customer))
    }

    @PutMapping("/{customerId}")
    fun updateCustomer(
        @PathVariable customerId: UUID,
        @RequestBody customerDto: CustomerDto
    ): ResponseEntity<CustomerDto?> {
        val customer =
            customerService.updateCustomer(customerId, customerDto) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(CustomerDto(customer))
    }

    @DeleteMapping("/{customerId}")
    fun deleteCustomer(@PathVariable customerId: UUID): ResponseEntity<CustomerDto?> {
        val customer = customerService.getCustomer(customerId) ?: return ResponseEntity.notFound().build()
        customerService.deleteCustomer(customer)
        return ResponseEntity.noContent().build()
    }
}