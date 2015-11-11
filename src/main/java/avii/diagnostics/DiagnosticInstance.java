package main.java.avii.diagnostics;

import java.util.HashMap;

import javax.imageio.metadata.IIOMetadataNode;

import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.simulator.simulatedTypes.SimulatedAttribute;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class DiagnosticInstance {

	private int _id = -1;
	private String _className;
	private String _stateName = "";
	private HashMap<String, String> _attributes = new HashMap<String,String>();
	private SimulatedInstanceIdentifier _identifier;
	
	public DiagnosticInstance(SimulatedInstance simulatedInstance) {
		this.setId(simulatedInstance.getIdentifier().getSequence());
		this.setClassName(simulatedInstance.getIdentifier().getSimulatedClass().getName());
		this._identifier = simulatedInstance.getIdentifier();
		if(simulatedInstance.getSimulatedClass().hasStates())
		{
			this.setStateName(simulatedInstance.getSimulatedState().getName());
		}
		for(SimulatedAttribute simulatedAttribute : simulatedInstance.getSimulatedClass().getAttributes())
		{
			String attributeName = simulatedAttribute.getName();
			if(attributeName.equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
			{
				continue;
			}
			this.addAttribute(attributeName, simulatedInstance.getAttribute(attributeName).toString());
		}
	}

	public void setId(int id) {
		this._id = id;
	}

	public void setClassName(String className) {
		this._className = className;
	}

	public void setStateName(String stateName) {
		this._stateName = stateName;
	}

	public void addAttribute(String attributeName, String attributeValue) {
		this._attributes.put(attributeName, attributeValue);
	}

	public int getId() {
		return this._id;
	}

	public String getClassName() {
		return this._className;
	}

	public String getStateName() {
		return this._stateName;
	}

	public HashMap<String,String> getAttributes() {
		return this._attributes;
	}

	public String getAttribute(String attributeName) {
		return this._attributes.get(attributeName);
	}

	public IIOMetadataNode serialise() {
		IIOMetadataNode node = new IIOMetadataNode("instance");
		node.setAttribute("instanceId", this._identifier.toString());
		//node.setAttribute("instanceClassName", this._className);
		//node.setAttribute("instanceSequence", Integer.toString(this._id));
		node.setAttribute("stateName", this._stateName);
		
		for(String attributeName : this._attributes.keySet())
		{
			String attributeValue = this._attributes.get(attributeName);
			IIOMetadataNode attributeNode = new IIOMetadataNode("Attribute");
			attributeNode.setAttribute("name", attributeName);
			attributeNode.setAttribute("value", attributeValue);
			node.appendChild(attributeNode);
		}
		return node;
	}

}
