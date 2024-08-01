package concentric.circles.sliced_onion.product

import org.jmolecules.event.types.DomainEvent
import org.springframework.modulith.events.Externalized
import java.util.*

class ProductCreated(val productId: UUID) : DomainEvent

class ProductDeleted(val productId: UUID) : DomainEvent
