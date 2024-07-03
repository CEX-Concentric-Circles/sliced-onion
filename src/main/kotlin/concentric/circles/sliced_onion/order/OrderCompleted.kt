package concentric.circles.sliced_onion.order

import org.jmolecules.event.types.DomainEvent
import java.util.*

class OrderCompleted(val productId: UUID) : DomainEvent