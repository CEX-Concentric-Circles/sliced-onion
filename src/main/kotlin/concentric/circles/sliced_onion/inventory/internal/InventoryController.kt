package concentric.circles.sliced_onion.inventory.internal

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryService: InventoryService) {

    @GetMapping
    fun getInventories(): ResponseEntity<List<InventoryDto>> = ResponseEntity.ok()
        .body(inventoryService.getInventories().map { inventory: Inventory -> InventoryDto(inventory) })

    @GetMapping("/{inventoryId}")
    fun getInventory(@PathVariable inventoryId: UUID): ResponseEntity<InventoryDto?> {
        val inventory = inventoryService.getInventory(inventoryId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(InventoryDto(inventory))
    }

    @PutMapping("/{inventoryId}/restock")
    fun increaseInventoryQuantity(
        @PathVariable inventoryId: UUID,
        @RequestBody quantity: Int
    ): ResponseEntity<InventoryDto?> {
        return ResponseEntity.ok().body(null)
    }
}