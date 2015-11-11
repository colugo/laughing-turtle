package main.java.avii.diagnostics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class EventQueueDiagnostics {

	private HashMap<SimulatedInstanceIdentifier, ArrayList<SimulatedEventInstance>> _events = new HashMap<SimulatedInstanceIdentifier, ArrayList<SimulatedEventInstance>>();

	public EventQueueDiagnostics(Collection<SimulatedEventInstance> events) {
		this.createEventMap(events);
	}

	private void createEventMap(Collection<SimulatedEventInstance> events) {
		for(SimulatedEventInstance event : events)
		{
			SimulatedInstanceIdentifier destination = event.getDestination();
			if(!this._events.containsKey(destination))
			{
				this._events.put(destination, new ArrayList<SimulatedEventInstance>());
			}
			ArrayList<SimulatedEventInstance> eventsForDestination = this._events.get(destination);
			eventsForDestination.add(event);
		}
	}

	public boolean containsEventsFor(SimulatedInstanceIdentifier identifier) {
		boolean hasEventsForInstance = this._events.containsKey(identifier);
		return hasEventsForInstance;
	}

	public ArrayList<SimulatedEventInstance> getEventsForInstance(SimulatedInstanceIdentifier id) {
		if(this._events.containsKey(id))
		{
			return this._events.get(id);
		}
		return new ArrayList<SimulatedEventInstance>();
	}

	public Node serialise() {
		IIOMetadataNode eventsNode = new IIOMetadataNode("events");
		for(SimulatedInstanceIdentifier identifier : this._events.keySet())
		{
			IIOMetadataNode instanceNode = new IIOMetadataNode("instance");
			instanceNode.setAttribute("instanceId", identifier.toString());
			
			for(SimulatedEventInstance eventInstance : this._events.get(identifier))
			{
				instanceNode.appendChild(eventInstance.serialise());
			}
			
			eventsNode.appendChild(instanceNode);
		}
		return eventsNode;
	}

}
