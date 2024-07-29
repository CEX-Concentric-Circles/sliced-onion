package concentric.circles.sliced_onion.inventory.internal

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryService: InventoryService) {

    @GetMapping
    fun getInventories(): ResponseEntity<List<InventoryDto>> = ResponseEntity.ok()
        .body(inventoryService.getInventories().map { inventory: Inventory -> InventoryDto(inventory) })

    @PostMapping
    fun createInventory(@RequestBody inventoryDto: InventoryDto): ResponseEntity<InventoryDto?> {
        val inventory = inventoryService.createInventory(inventoryDto) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(InventoryDto(inventory))
    }

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
        val inventory = inventoryService.increaseInventoryQuantity(inventoryId, quantity)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(InventoryDto(inventory))
    }
}