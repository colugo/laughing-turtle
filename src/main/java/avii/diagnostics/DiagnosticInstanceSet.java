package main.java.avii.diagnostics;

import java.util.ArrayList;

import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class DiagnosticInstanceSet {
	
	private ArrayList<SimulatedInstanceIdentifier> _instances = new ArrayList<SimulatedInstanceIdentifier>();
	private boolean _isIterating;
	private int _iterationCount;
	
	public DiagnosticInstanceSet(SimulatedInstanceSet simulatedInstanceSet) {
		for(SimulatedInstance instance : simulatedInstanceSet.getInstances())
		{
			this._instances.add(instance.getIdentifier());
		}
		this._isIterating = simulatedInstanceSet.isIterating();
		this._iterationCount = simulatedInstanceSet.iterationCount();
	}

	public boolean hasInstanceWithIdentifier(SimulatedInstanceIdentifier identifier) {
		boolean hasInstance = this._instances.contains(identifier);
		return hasInstance;
	}

	public int instanceCount() {
		return this._instances.size();
	}

	public int iterationCount() {
		return this._iterationCount;
	}

	public boolean isIterating() {
		return this._isIterating;
	}

	public ArrayList<SimulatedInstanceIdentifier> instances() {
		return this._instances;
	}

}
