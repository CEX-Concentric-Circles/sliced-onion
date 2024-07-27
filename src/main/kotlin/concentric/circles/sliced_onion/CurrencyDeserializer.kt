package concentric.circles.sliced_onion

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.util.Currency

class CurrencyDeserializer : JsonDeserializer<Currency>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Currency {
        val currencyCode = p.text
        return Currency.getInstance(currencyCode)
    }
}
