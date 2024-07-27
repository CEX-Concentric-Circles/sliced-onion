package concentric.circles.sliced_onion

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.persistence.Embeddable
import java.util.Currency

@Embeddable
class Price (
    val amount: Double,
    @JsonDeserialize(using = CurrencyDeserializer::class)
    val currency: Currency
)