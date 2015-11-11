package main.java.avii.simulator.simulatedTypes;

import java.util.Collection;
import java.util.HashMap;

public class TestVectorInstanceMap {
	private HashMap<String, SimulatedInstanceIdentifier> _map = new HashMap<String, SimulatedInstanceIdentifier>();

	public SimulatedInstanceIdentifier getIdentifierForInstanceName(String instanceName) {
		return this._map.get(instanceName);
	}

	public void addNamedInstance(SimulatedInstance simulatedInstance, String instanceName) {
		this._map.put(instanceName, simulatedInstance.getIdentifier());
	}
	
	public Collection<String> getNamedInstances()
	{
		return this._map.keySet();
	}

}
