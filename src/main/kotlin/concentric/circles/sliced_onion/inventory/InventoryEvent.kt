package concentric.circles.sliced_onion.inventory

import org.jmolecules.event.types.DomainEvent
import java.util.UUID

class InventoryEvent(val productId: UUID) : DomainEvent