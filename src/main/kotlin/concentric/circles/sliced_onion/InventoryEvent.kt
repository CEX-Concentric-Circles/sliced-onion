package concentric.circles.sliced_onion

import org.jmolecules.event.types.DomainEvent
import java.util.*

class InventoryEvent(val productId: UUID) : DomainEvent