package concentric.circles.sliced_onion

import org.jmolecules.event.types.DomainEvent
import java.util.*

class ProductCreated(val productId: UUID) : DomainEvent

class ProductDeleted(val productId: UUID) : DomainEvent
