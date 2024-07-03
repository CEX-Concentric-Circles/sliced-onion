package concentric.circles.sliced_onion.product

import java.util.*

class ProductDto(
    val productId: UUID?,
    val name: String,
    val price: Double
) {
    constructor(product: Product) : this(product.productId, product.name, product.price)
}