package concentric.circles.sliced_onion.order

import org.jmolecules.event.types.DomainEvent
import java.util.*

class OrderEvent(val productIds: List<UUID>, val status: String) : DomainEvent