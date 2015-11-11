package main.java.avii.diagnostics;

import java.util.Collection;
import java.util.HashMap;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;

public class NamedInstanceAndVariableDiagnostics {

	private HashMap<String,SimulatedInstanceIdentifier> _instances = new HashMap<String,SimulatedInstanceIdentifier>();
	private HashMap<String,DiagnosticInstanceSet> _instanceSets = new HashMap<String,DiagnosticInstanceSet>();
	private HashMap<String,String> _variables = new HashMap<String,String>();

	public boolean hasInstanceWithName(String instanceName) {
		boolean hasInstance = this._instances.containsKey(instanceName);
		return hasInstance;
	}

	public boolean hasInstanceSetWithName(String instanceSetName) {
		boolean hasInstanceSet = this._instanceSets.containsKey(instanceSetName);
		return hasInstanceSet;
	}

	public SimulatedInstanceIdentifier getInstanceWithName(String instanceName) {
		return this._instances.get(instanceName);
	}

	public DiagnosticInstanceSet getInstanceSetWithName(String instanceSetName) {
		return this._instanceSets.get(instanceSetName);
	}

	public void addInstance(String instanceName, SimulatedInstanceIdentifier identifier) {
		this._instances.put(instanceName, identifier);
	}

	public void addInstanceSet(String instanceSetName, SimulatedInstanceSet simulatedInstanceSet) {
		DiagnosticInstanceSet instanceSet = new DiagnosticInstanceSet(simulatedInstanceSet);
		this._instanceSets.put(instanceSetName, instanceSet);
	}

	public Collection<String> getInstanceNames() {
		return this._instances.keySet();
	}

	public Collection<String> getInstanceSetNames() {
		return this._instanceSets.keySet();
	}

	public Collection<String> getVariableNames() {
		return this._variables.keySet();
	}

	public String getVariableWithName(String variableName) {
		return this._variables.get(variableName);
	}

	public void addVariable(String variableName, String variableValue) {
		this._variables.put(variableName, variableValue);
	}

	public Node serialise() {
		IIOMetadataNode node = new IIOMetadataNode("localNames");
		serialiseInstances(node);
		serialiseInstanceSets(node);
		serialiseVariables(node);
		return node;
	}

	private void serialiseInstances(IIOMetadataNode node) {
		IIOMetadataNode instanceNames = new IIOMetadataNode("instances");
		for(String instanceName : this.getInstanceNames())
		{
			IIOMetadataNode instanceNode = new IIOMetadataNode("instance");
			instanceNode.setAttribute("localName", instanceName);
			instanceNode.setAttribute("instanceId", this._instances.get(instanceName).toString());
			instanceNames.appendChild(instanceNode);
		}
		node.appendChild(instanceNames);
	}
	
	private void serialiseVariables(IIOMetadataNode node) {
		IIOMetadataNode variableNames = new IIOMetadataNode("variables");
		for(String variableName : this.getVariableNames())
		{
			IIOMetadataNode variableNode = new IIOMetadataNode("variable");
			variableNode.setAttribute("localName", variableName);
			variableNode.setAttribute("value", this._variables.get(variableName));
			variableNames.appendChild(variableNode);
		}
		node.appendChild(variableNames);
	}
	
	private void serialiseInstanceSets(IIOMetadataNode node) {
		IIOMetadataNode instanceSetNames = new IIOMetadataNode("instanceSets");
		for(String instanceSetName : this.getInstanceSetNames())
		{
			IIOMetadataNode instanceSetNode = new IIOMetadataNode("instanceSet");
			instanceSetNode.setAttribute("localName", instanceSetName);
			DiagnosticInstanceSet instanceSet = this.getInstanceSetWithName(instanceSetName);
			instanceSetNode.setAttribute("isIterating", Boolean.toString(instanceSet.isIterating()));
			IIOMetadataNode instances = new IIOMetadataNode("instances");
			int instanceCount = 0;
			for(SimulatedInstanceIdentifier instanceId : instanceSet.instances())
			{
				IIOMetadataNode instanceNode = new IIOMetadataNode("instance");
				instanceNode.setAttribute("instanceId", instanceId.toString());
				if(instanceSet.isIterating())
				{
					if( instanceSet.iterationCount() == instanceCount)
					{
						instanceNode.setAttribute("currentInstance", Boolean.toString(true));
					}else
					{
						instanceNode.setAttribute("currentInstance", Boolean.toString(false));
					}
				}
				instances.appendChild(instanceNode);
				instanceCount++;
			}
			instanceSetNode.appendChild(instances);
			instanceSetNames.appendChild(instanceSetNode);
		}
		node.appendChild(instanceSetNames);
	}

}
