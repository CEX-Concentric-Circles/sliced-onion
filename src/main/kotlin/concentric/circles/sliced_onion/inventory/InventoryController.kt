package concentric.circles.sliced_onion.inventory

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {

    @GetMapping
    fun getInventories(): ResponseEntity<List<InventoryDto>> {
        return ResponseEntity.ok().body(
            inventoryService.getInventories().map { inventory: Inventory ->
                InventoryDto(
                    inventory.inventoryId,
                    inventory.productId,
                    inventory.stockCount
                )
            })
    }
}