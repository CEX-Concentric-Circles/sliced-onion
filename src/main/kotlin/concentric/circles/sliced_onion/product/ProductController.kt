package concentric.circles.sliced_onion.product

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok().body(productService.getProducts().map { product: Product -> ProductDto(product) })
    }

    @PostMapping
    fun createProduct(@RequestBody productDto: ProductDto): ResponseEntity<ProductDto?> {
        val product = productService.createProduct(productDto) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(ProductDto(product))
    }
}