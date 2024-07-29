package concentric.circles.sliced_onion.product.internal

import java.util.*

class ProductDto(
    val productId: UUID?,
    val name: String,
    val price: Price
) {
    constructor(product: Product) : this(product.productId, product.name, product.price)
}