package concentric.circles.sliced_onion.integration

import org.json.JSONArray
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.beans.factory.annotation.Autowired
import org.json.JSONObject
import org.junit.jupiter.api.*
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private var productId: String? = null
    private var inventoryId: String? = null
    private var orderId: String? = null
    private var customerId: UUID? = null

    @Test
    @Order(0)
    fun `should create customer`() {
        val createCustomerResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/customer")
                .contentType("application/json")
                .content("{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\" }")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"))
            .andReturn()

        val responseContent = createCustomerResponse.response.contentAsString
        println("Create Customer Response: $responseContent")

        val jsonResponse = JSONObject(responseContent)
        customerId = UUID.fromString(jsonResponse.optString("customerId", null))
        assertNotNull(customerId, "Customer ID should not be null")
        println("Customer ID set to: $customerId")
    }

    @Test
    @Order(1)
    fun `should create product`() {
        val createProductResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/product")
                .contentType("application/json")
                .content("{ \"name\": \"Sample Product\", \"price\": { \"amount\": 100.0, \"currency\": \"EUR\" } }")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.productId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Sample Product"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.price.amount").value(100.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.price.currency").value("EUR"))
            .andReturn()

        val responseContent = createProductResponse.response.contentAsString
        println("Create Product Response: $responseContent")

        val jsonResponse = JSONObject(responseContent)
        productId = jsonResponse.optString("productId", null)
        assertNotNull(productId, "Product ID should not be null")
        println("Product ID set to: $productId")

        val inventoryResponse = mockMvc.perform(
            MockMvcRequestBuilders.get("/inventory")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val inventoryContent = inventoryResponse.response.contentAsString
        println("Inventory Response: $inventoryContent")

        val inventoryArray = JSONArray(inventoryContent)
        var foundInventoryId: String? = null

        for (i in 0 until inventoryArray.length()) {
            val inventory = inventoryArray.getJSONObject(i)
            if (inventory.optString("productId") == productId) {
                foundInventoryId = inventory.optString("inventoryId", null)
                break
            }
        }

        inventoryId = foundInventoryId
        assertNotNull(inventoryId, "Inventory ID should not be null for productId $productId")
        println("Inventory ID set to: $inventoryId")
    }

    @Test
    @Order(2)
    fun `should restock inventory`() {
        val validProductId = productId ?: throw IllegalStateException("Product ID is not initialized")
        val validInventoryId = inventoryId ?: throw IllegalStateException("Inventory ID is not initialized")

        println("Restocking Inventory for Product ID: $validProductId and Inventory ID: $validInventoryId")

        val restockResponse = mockMvc.perform(
            MockMvcRequestBuilders.put("/inventory/$validInventoryId/restock")
                .contentType("application/json")
                .content("20")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(20))
            .andReturn()

        val updatedInventory = restockResponse.response.contentAsString
        println("Restock Inventory Response: $updatedInventory")
        assertNotNull(updatedInventory)
    }

    @Test
    @Order(3)
    fun `should place an order`() {
        val validProductId = productId ?: throw IllegalStateException("Product ID is not initialized")
        val validCustomerId = customerId ?: throw IllegalStateException("Customer ID is not initialized")

        val createOrderResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/order")
                .contentType("application/json")
                .content("{\"customerId\": \"${validCustomerId}\", \"productIds\": [\"$validProductId\"]}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OPEN"))
            .andReturn()

        val responseContent = createOrderResponse.response.contentAsString
        val jsonResponse = JSONObject(responseContent)
        orderId = jsonResponse.optString("orderId", null)
        assertNotNull(orderId, "Order ID should not be null")
        println("Order ID set to: $orderId")
    }

    @Test
    @Order(4)
    fun `should complete the order`() {
        val validOrderId = orderId ?: throw IllegalStateException("Order ID is not initialized")

        mockMvc.perform(
            MockMvcRequestBuilders.put("/order/$validOrderId/complete")
                .contentType("application/json")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("COMPLETED"))
    }

    @Test
    @Order(5)
    fun `should delete product`() {
        val validProductId = productId ?: throw IllegalStateException("Product ID is not initialized")

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/product/$validProductId")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/product/$validProductId")
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}
