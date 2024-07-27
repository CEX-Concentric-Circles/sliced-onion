package concentric.circles.sliced_onion.inventory

import concentric.circles.sliced_onion.inventory.internal.Inventory
import concentric.circles.sliced_onion.inventory.internal.InventoryDto
import concentric.circles.sliced_onion.inventory.internal.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {

    @GetMapping
    fun getInventories(): ResponseEntity<List<InventoryDto>> = ResponseEntity.ok()
        .body(inventoryService.getInventories().map { inventory: Inventory -> InventoryDto(inventory) })

    @PutMapping("/{inventoryId}/restock")
    fun increaseInventoryQuantity(
        @PathVariable inventoryId: UUID
    ): ResponseEntity<InventoryDto?> {
        return ResponseEntity.ok().body(null)
    }
}