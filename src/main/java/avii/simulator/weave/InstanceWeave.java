package main.java.avii.simulator.weave;

import java.util.Collection;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.weaving.AttributeWeave;
import main.java.avii.editor.metamodel.weaving.ClassWeave;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class InstanceWeave {

	private HashMap<SimulatedClass, SimulatedInstance> _instances = new HashMap<SimulatedClass, SimulatedInstance>();
	private ClassWeave _classWeave;
	private HashMap<AttributeWeave, Object> _attributes = new HashMap<AttributeWeave, Object>();
	
	public InstanceWeave(ClassWeave classWeave) {
		this._classWeave = classWeave;
	}

	public void putInstance(SimulatedInstance theInstance) {
		SimulatedClass simulatedClass = theInstance.getSimulatedClass();
		theInstance.setWeave(this);
		this._instances.put(simulatedClass, theInstance);
	}

	public SimulatedInstance getInstanceForClass(SimulatedClass simulatedClass) {
		return this._instances.get(simulatedClass);
	}

	public Collection<SimulatedInstance> getInstances()
	{
		return this._instances.values();
	}

	public ClassWeave getClassWeave() {
		return this._classWeave;
	}

	public void setAttribute(AttributeWeave attributeWeave, Object convertedAttributeValue) {
		this._attributes.put(attributeWeave, convertedAttributeValue);
	}

	public Object getAttribute(AttributeWeave attributeWeave) {
		return this._attributes.get(attributeWeave);
	}

	public void setupInitialValuesOfAttribtues() {
		for(AttributeWeave attributeWeave : this._classWeave.getAttributeWeaves()){
			IEntityDatatype type = attributeWeave.getIEntityDatatTypeForWeave();
			Object defaultValue = type.getDefaultValueAsObject();
			this._attributes.put(attributeWeave,defaultValue);
		}
	}

}
