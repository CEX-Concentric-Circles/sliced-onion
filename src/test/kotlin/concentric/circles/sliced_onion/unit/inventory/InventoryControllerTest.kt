package concentric.circles.sliced_onion.unit.inventory

import concentric.circles.sliced_onion.inventory.internal.Inventory
import concentric.circles.sliced_onion.inventory.internal.InventoryController
import concentric.circles.sliced_onion.inventory.internal.InventoryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*


@ExtendWith(MockitoExtension::class)
class InventoryControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var inventoryService: InventoryService

    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

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
}
