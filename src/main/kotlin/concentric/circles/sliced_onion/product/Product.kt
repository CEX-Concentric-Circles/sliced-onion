package concentric.circles.sliced_onion.product

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`product`")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    val productId: UUID,

    @Column
    var name: String,

    @Column
    var price: Double
) {
    constructor(productDto: ProductDto) : this(UUID.randomUUID(), productDto.name, productDto.price)
}