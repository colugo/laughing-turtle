package main.java.avii.simulator.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import main.java.avii.diagnostics.EventQueueDiagnostics;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedEventQueue {

	private PriorityQueue<SimulatedEventInstance> _eventInstanceQueue = new PriorityQueue<SimulatedEventInstance>(50, new SimulatedEventInstanceComparitor());
	private IEventTimeProvider _eventTimeProvider = new SimulatedEventTimeHelper();
	private ArrayList<SimulatedInstanceIdentifier> _lockedInstances = new ArrayList<SimulatedInstanceIdentifier>();
	private ISimulator _simulator;

	public SimulatedEventQueue(ISimulator simulator) {
		this._simulator = simulator;
	}

	public boolean selfTestQueue() {
		long lastTime = Long.MIN_VALUE;
		for (SimulatedEventInstance event : this._eventInstanceQueue) {
			if (event.getGoTime() < lastTime) {
				return false;
			}
			lastTime = event.getGoTime();
		}
		return true;
	}

	public void setEventTimeProvider(IEventTimeProvider timeProvider) {
		this._eventTimeProvider = timeProvider;
	}

	public int size() {
		return this._eventInstanceQueue.size();
	}

	public void registerEventInstance(SimulatedEventInstance eventInstance) {
		// callback to simulator, to ensure that the destination of the
		// eventInstance is the root of the hierarchy
		this._simulator.ensureDestinationOfEventInstanceIsRootOfAnyHierarchy(eventInstance);
		this._eventInstanceQueue.add(eventInstance);
	}

	private void removeFirstOccuranceOfEventInstanceFromQueue(SimulatedEventInstance removeEventInstance) {
		boolean alreadyRemoved = false;
		PriorityQueue<SimulatedEventInstance> eventInstanceQueue = new PriorityQueue<SimulatedEventInstance>(50, new SimulatedEventInstanceComparitor());
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (!alreadyRemoved && eventInstance.equals(removeEventInstance)) {
				alreadyRemoved = true;
			} else {
				eventInstanceQueue.add(eventInstance);
			}
		}
		this._eventInstanceQueue = eventInstanceQueue;
	}

	public SimulatedEventInstance getNextEventInstance() {
		if (this.hasReadyEventInstance()) {
			SimulatedEventInstance nextEventInstance = getNextEventInstanceConsideringLockedInstances();
			removeFirstOccuranceOfEventInstanceFromQueue(nextEventInstance);
			return nextEventInstance;
		}
		return null;
	}

	private SimulatedEventInstance getNextEventInstanceConsideringLockedInstances() {
		// toSelfEvents, ignoring lockedInstances
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			SimulatedInstanceIdentifier destination = eventInstance.getDestination();
			if (!this._lockedInstances.contains(destination)) {
				if (eventInstance.isToSelf() && isEventInstanceGoTimeValid(eventInstance)) {
					return eventInstance;
				}
			}
		}

		// non toSelf events, ignoring lockedInstances
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			SimulatedInstanceIdentifier destination = eventInstance.getDestination();
			if (!this._lockedInstances.contains(destination)) {
				if (isEventInstanceGoTimeValid(eventInstance)) {
					return eventInstance;
				}
			}
		}

		// there are no events witha valid go time
		return null;
	}

	public boolean hasReadyEventInstance() {
		if (!hasEventInstances()) {
			return false;
		}

		SimulatedEventInstance top = this.getNextEventInstanceConsideringLockedInstances();
		if (top == null) {
			// if there are no ready events ignoring locked instances,
			// look for ready events not ignoring locked instances
			this.clearLockedInstances();
			if(this.getNextEventInstanceConsideringLockedInstances() == null)
			{
				return false;
			}
		}

		return true;
	}

	private boolean isEventInstanceGoTimeValid(SimulatedEventInstance eventInstance) {
		long currentTime = this._eventTimeProvider.getCurrentTime();
		long eventInstanceGoTime = eventInstance.getGoTime();

		boolean eventInstanceIsReady = eventInstanceGoTime <= currentTime;
		return eventInstanceIsReady;
	}

	public boolean hasEventInstances() {
		return !this._eventInstanceQueue.isEmpty();
	}

	public void setLockedInstances(ArrayList<SimulatedInstanceIdentifier> lockedClasses) {
		this._lockedInstances = lockedClasses;
	}
	
	public void clearLockedInstances()
	{
		this._lockedInstances.clear();
	}

	public SimulatedEventInstance getNextToSelfEventForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		if (this.hasReadyToSelfEventInstanceForInstance(instanceIdentifier)) {
			SimulatedEventInstance nextEventInstance = findNextToSelfEventInstanceForInstance(instanceIdentifier);
			this._eventInstanceQueue.remove(nextEventInstance);
			return nextEventInstance;
		}
		return null;
	}

	public boolean hasReadyToSelfEventInstanceForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		if (!hasEventInstances()) {
			return false;
		}

		SimulatedEventInstance top = this.findNextToSelfEventInstanceForInstance(instanceIdentifier);
		if (top == null) {
			return false;
		}

		if (isEventInstanceGoTimeValid(top)) {
			return true;
		}
		return false;
	}

	private SimulatedEventInstance findNextToSelfEventInstanceForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (eventInstance.isToSelf()) {
				SimulatedInstanceIdentifier destination = eventInstance.getDestination();
				if (destination == instanceIdentifier) {
					if (isEventInstanceGoTimeValid(eventInstance)) {
						return eventInstance;
					}
				}
			}
		}
		return null;
	}

	public SimulatedEventInstance removeEvent(String eventName, SimulatedInstanceIdentifier sender, SimulatedInstanceIdentifier destination) {
		SimulatedEventInstance cancelEvent = new SimulatedEventInstance(sender, destination);
		cancelEvent.setName(eventName);

		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (eventInstance.isSameEventForCancellation(cancelEvent)) {
				this.removeFirstOccuranceOfEventInstanceFromQueue(eventInstance);
				return eventInstance;
			}
		}
		return null;
	}

	public void removeAllEventsForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (eventInstance.getDestination().equals(instanceIdentifier)) {
				this.removeFirstOccuranceOfEventInstanceFromQueue(eventInstance);
			}
		}
	}

	public boolean anyEventsForInstanceIdentifier(SimulatedInstanceIdentifier destination) {
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (eventInstance.getDestination().equals(destination)) {
				return true;
			}
		}
		return false;
	}

	public void redirectHierarchyEvents(Collection<SimulatedInstanceIdentifier> hirarchyInstanceIdentifiers, SimulatedInstanceIdentifier destination) {
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (hirarchyInstanceIdentifiers.contains(eventInstance.getDestination())) {
				boolean wasToSelf = eventInstance.isToSelf();
				eventInstance.setDestination(destination);
				if (wasToSelf) {
					eventInstance.setSender(destination);
				}
			}
		}
	}

	public boolean hasToSelfEventsForInstanceIdentifier(SimulatedInstanceIdentifier identifier) {
		for (SimulatedEventInstance eventInstance : this._eventInstanceQueue) {
			if (eventInstance.getDestination().equals(identifier) && eventInstance.isToSelf()) {
				if (!eventInstance.hasDelay()) {
					return true;
				}
			}
		}
		return false;
	}

	public EventQueueDiagnostics createDiagnostics() {
		EventQueueDiagnostics diagnostics = new EventQueueDiagnostics(this._eventInstanceQueue);
		return diagnostics;
	}

}
