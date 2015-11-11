package main.java.avii.editor.metamodel.weaving;

import java.util.Collection;
import java.util.HashMap;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class EventWeave {

	private HashMap<EntityClass, EntityEventSpecification> _events = new HashMap<EntityClass, EntityEventSpecification>();

	public boolean isEventInWeave(EntityEventSpecification eventSpec) {
		return this._events.values().contains(eventSpec);
	}

	public void addEvent(EntityEventSpecification eventSpec) {
		EntityClass theClass = eventSpec.getKlass();
		this._events .put(theClass, eventSpec);
	}

	public Collection<EntityEventSpecification> getEvents() {
		return this._events.values();
	}

}
