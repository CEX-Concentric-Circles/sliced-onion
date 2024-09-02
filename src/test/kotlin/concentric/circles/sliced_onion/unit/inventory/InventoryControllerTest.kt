package concentric.circles.sliced_onion.unit.inventory

import concentric.circles.sliced_onion.inventory.internal.Inventory
import concentric.circles.sliced_onion.inventory.internal.InventoryController
import concentric.circles.sliced_onion.inventory.internal.InventoryDto
import concentric.circles.sliced_onion.inventory.internal.InventoryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockitoExtension::class)
class InventoryControllerTest {

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var inventoryService: InventoryService

    @InjectMocks
    private lateinit var inventoryController: InventoryController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build()
    }

    @Test
    fun `should return all inventories`() {
        val inventories = listOf(
            Inventory(UUID.randomUUID(), UUID.randomUUID(), 10),
            Inventory(UUID.randomUUID(), UUID.randomUUID(), 5)
        )
        `when`(inventoryService.getInventories()).thenReturn(inventories)

        mockMvc.perform(get("/inventory"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }


    @Test
    fun `should create inventory with valid data`() {
        val inventoryDto = """ { "productId": "${UUID.randomUUID()}", "quantity": 10 } """
        val inventory = Inventory(UUID.randomUUID(), UUID.randomUUID(), 10)

        `when`(inventoryService.createInventory(any(InventoryDto::class.java))).thenReturn(inventory)

        mockMvc.perform(
            post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inventoryDto)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.inventoryId").value(inventory.inventoryId.toString()))
            .andExpect(jsonPath("$.quantity").value(inventory.quantity))
    }

    @Test
    fun `should return 400 when trying to create inventory with invalid data`() {
        val invalidInventoryDto = """ { "productId": "not-a-uuid", "quantity": -5 } """

        mockMvc.perform(
            post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidInventoryDto)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when trying to create inventory with missing inventoryId`() {
        val incompleteInventoryDto = """ { "quantity": 5 } """

        mockMvc.perform(
            post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(incompleteInventoryDto)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return inventory by id`() {
        val inventoryId = UUID.randomUUID()
        val inventory = Inventory(inventoryId, UUID.randomUUID(), 10)
        `when`(inventoryService.getInventory(inventoryId)).thenReturn(inventory)

        mockMvc.perform(get("/inventory/$inventoryId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.inventoryId").value(inventoryId.toString()))
            .andExpect(jsonPath("$.quantity").value(inventory.quantity))
    }

    @Test
    fun `should return 404 when inventory not found`() {
        val inventoryId = UUID.randomUUID()
        `when`(inventoryService.getInventory(inventoryId)).thenReturn(null)

        mockMvc.perform(get("/inventory/$inventoryId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should increase inventory quantity`() {
        val inventoryId = UUID.randomUUID()
        val inventory = Inventory(inventoryId, UUID.randomUUID(), 10)
        val quantityToAdd = 5
        `when`(inventoryService.increaseInventoryQuantity(inventoryId, quantityToAdd))
            .thenReturn(inventory.apply { this.quantity += quantityToAdd })

        mockMvc.perform(
            put("/inventory/$inventoryId/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quantityToAdd.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.inventoryId").value(inventoryId.toString()))
            .andExpect(jsonPath("$.quantity").value(inventory.quantity))
    }

    @Test
    fun `should return 400 when trying to restock with non-numeric quantity`() {
        val inventoryId = UUID.randomUUID()
        val invalidQuantity = "not-a-number"

        mockMvc.perform(
            put("/inventory/$inventoryId/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidQuantity)
        )
            .andExpect(status().isBadRequest)
    }
}