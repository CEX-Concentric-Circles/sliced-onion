package concentric.circles.sliced_onion.product

import concentric.circles.sliced_onion.product.internal.Product
import concentric.circles.sliced_onion.product.internal.ProductDto
import concentric.circles.sliced_onion.product.internal.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*


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
    fun createProduct(@RequestBody productDto: ProductDto, ucb: UriComponentsBuilder): ResponseEntity<ProductDto?> {
        val product = productService.createProduct(productDto) ?: return ResponseEntity.badRequest().body(null)
        val uriComponents = ucb.path("/product/{productId}").buildAndExpand(product.productId)
        return ResponseEntity.created(uriComponents.toUri()).body(ProductDto(product))
    }

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: UUID): ResponseEntity<ProductDto?> {
        val product = productService.getProduct(productId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok().body(ProductDto(product))
    }

    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable productId: UUID, @RequestBody productDto: ProductDto): ResponseEntity<ProductDto?> {
        val product =
            productService.updateProduct(productId, productDto) ?: return ResponseEntity.badRequest().body(null)
        return ResponseEntity.ok().body(ProductDto(product))
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: UUID): ResponseEntity<ProductDto> {
        val product = productService.getProduct(productId) ?: return ResponseEntity.notFound().build()
        productService.deleteProduct(product)
        return ResponseEntity.noContent().build()
    }
}