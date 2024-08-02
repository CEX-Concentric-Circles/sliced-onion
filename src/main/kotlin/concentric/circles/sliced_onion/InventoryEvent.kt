package concentric.circles.sliced_onion

import org.jmolecules.event.types.DomainEvent
import java.util.UUID

class InventoryEvent(val productId: UUID) : DomainEvent