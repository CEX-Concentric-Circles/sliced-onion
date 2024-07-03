package concentric.circles.sliced_onion.product

import org.jmolecules.event.types.DomainEvent
import java.util.*

class ProductCreated(val productId: UUID) : DomainEvent