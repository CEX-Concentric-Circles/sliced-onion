package concentric.circles.sliced_onion

import concentric.circles.sliced_onion.order.OrderEvent
import concentric.circles.sliced_onion.product.ProductCreated
import concentric.circles.sliced_onion.product.ProductDeleted
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.http.*
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class InventoryAdapter {

    @Value("\${inventory.service.url}")
    lateinit var externalServiceUrl: String

    private val restTemplate = RestTemplate()

    @EventListener
    fun on(event: OrderEvent) {
        val completed: Boolean = (event.status == "COMPLETED")
        try {
            val requestBody = mapOf("productIds" to event.productIds, "decreaseStocks" to completed)

            val response: ResponseEntity<String> = restTemplate.exchange(
                "$externalServiceUrl/inventory/verify",
                HttpMethod.POST,
                HttpEntity(requestBody, HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }),
                String::class.java
            )

            if (response.statusCode != HttpStatus.OK) {
                throw Exception(response.statusCode.toString())
            }
        } catch (ex: Exception) {
            throw Exception("Error during inventory verification: ${ex.message}", ex)
        }
    }


    @ApplicationModuleListener
    fun on(event: ProductCreated) {
    }

    @EventListener
    fun on(event: ProductDeleted) {
    }
}