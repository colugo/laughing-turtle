package main.java.avii.simulator.events;

import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedEventStack {
	
	private SimulatedInstanceIdentifier _instanceId;

	public void setCurrentInstance(SimulatedInstanceIdentifier instanceId) {
		this._instanceId = instanceId;
	}

	public SimulatedInstanceIdentifier getCurrentInstance() {
		return this._instanceId;
	}

	public boolean isFree() {
		return this._instanceId == null;
	}

	public void freeUp() {
		this._instanceId = null;
	}

}
