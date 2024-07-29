package concentric.circles.sliced_onion.product.internal

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class Price(
    @Column
    val amount: Double,

    @Column
    @Enumerated(EnumType.STRING)
    val currency: Currency = Currency.EUR
)