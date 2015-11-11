package main.java.avii.simulator.simulatedTypes;

import java.util.ArrayList;

public class SimulatedInstanceSet {
	private ArrayList<SimulatedInstance> _instances = new ArrayList<SimulatedInstance>();
	private boolean _isIterating;
	private int _currentInstanceIndex;

	public int size() {
		return this._instances.size();
	}

	public void addInstance(SimulatedInstance simulatedInstance) {
		if(this._instances.contains(simulatedInstance))
		{
			return;
		}
		
		this._instances.add(simulatedInstance);
	}

	public SimulatedInstance getInstanceAt(int i) {
		return this._instances.get(i);
	}

	public boolean containsInstance(SimulatedInstance instance) {
		boolean contains = false;
		contains = this._instances.contains(instance);
		return contains;
	}

	public ArrayList<SimulatedInstance> getInstances() {
		return this._instances;
	}

	public void setIterating(boolean isIterating) {
		this._isIterating = isIterating;
	}

	public void setCurrentInstanceIndex(int index) {
		this._currentInstanceIndex = index;
	}

	public boolean isIterating() {
		return this._isIterating;
	}

	public int iterationCount() {
		return this._currentInstanceIndex;
	}

}
