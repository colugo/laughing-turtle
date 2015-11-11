package main.java.avii.simulator.simulatedTypes;

import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.diagnostics.NamedInstanceAndVariableDiagnostics;

public class ScopeManager {

	private HashMap<String, SimulatedInstance> _inScopeInstances = new HashMap<String, SimulatedInstance>();
	private HashMap<String, Object> _inScopeTempVariables = new HashMap<String, Object>();
	private HashMap<String, SimulatedInstanceSet> _inScopeInstanceSets = new HashMap<String, SimulatedInstanceSet>();

	public String getNameForInstance(SimulatedInstanceIdentifier instanceId)
	{
		for(String instanceName : this._inScopeInstances.keySet())
		{
			SimulatedInstance instance = this._inScopeInstances.get(instanceName);
			if(instance.getIdentifier().equals(instanceId))
			{
				return instanceName;
			}
		}
		return null;
	}
	
	public SimulatedInstance getInstanceWithName(String instanceName) throws NameNotFoundException {
		if (this._inScopeInstances.containsKey(instanceName)) {
			return this._inScopeInstances.get(instanceName);
		}
		String list = "";
		for (String name : this._inScopeInstances.keySet()) {
			list += name + "\n";
		}
		throw new NameNotFoundException("Current SimulatingState does not have an instance name in scope named '" + instanceName + "' in scope names are :" + list);
	}

	public void registerInstance(String instanceName, SimulatedInstance theInstance) {
		this._inScopeInstances.put(instanceName, theInstance);
	}

	public Object getTempVariable(String tempName) {
		return this._inScopeTempVariables.get(tempName);
	}

	public void setTempVariable(String tempName, Object expressionValue) {
		this._inScopeTempVariables.put(tempName, expressionValue);
	}

	public void registerInstanceSet(String instanceSetName, SimulatedInstanceSet instanceSet) {
		this._inScopeInstanceSets.put(instanceSetName, instanceSet);
	}

	public SimulatedInstanceSet getInstanceSetWithName(String instanceSetName) {
		return this._inScopeInstanceSets.get(instanceSetName);
	}

	public NamedInstanceAndVariableDiagnostics getNamedInstanceDiagnostics() {
		NamedInstanceAndVariableDiagnostics namedDiagnostics = new NamedInstanceAndVariableDiagnostics();
		
		for(String instanceName : this._inScopeInstances.keySet())
		{
			namedDiagnostics.addInstance(instanceName, this._inScopeInstances.get(instanceName).getIdentifier());
		}
		for(String instanceSetName : this._inScopeInstanceSets.keySet())
		{
			namedDiagnostics.addInstanceSet(instanceSetName, this._inScopeInstanceSets.get(instanceSetName));
		}
		for(String variableName : this._inScopeTempVariables.keySet())
		{
			namedDiagnostics.addVariable(variableName, this._inScopeTempVariables.get(variableName).toString());
		}
		return namedDiagnostics;
	}

	public void deRegisterInstance(String instanceName) {
		this._inScopeInstances.remove(instanceName);
	}

}
