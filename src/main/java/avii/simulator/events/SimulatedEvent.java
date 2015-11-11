package main.java.avii.simulator.events;

import java.util.Collection;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class SimulatedEvent {

	private EntityEventSpecification _concreteEvent;
	private HashMap<String, SimulatedEventParam> _params = new HashMap<String, SimulatedEventParam>();

	public String toString()
	{
		return "Simulated_" + this._concreteEvent.getName();
	}
	
	public SimulatedEvent(EntityEventSpecification theConcreteEvent) {
		this._concreteEvent = theConcreteEvent;
		this.createSimulatedParams();
	}

	public Collection<SimulatedEventParam> getParams() {
		return this._params.values();
	}
	
	private void createSimulatedParams()
	{
		for(EntityEventParam entityParam : this._concreteEvent.getEventParams())
		{
			SimulatedEventParam simulatedParam = new SimulatedEventParam(entityParam);
			this._params.put(simulatedParam.getName(), simulatedParam);
		}
	}

	public EntityEventSpecification getConcreteEvent() {
		return this._concreteEvent;
	}
	
	public Object convertNewAttributeValueToCorrectDatatype(String paramName, Object newValue)
	{
		SimulatedEventParam theAttribute = this._params.get(paramName);
		IEntityDatatype theType = theAttribute.getType();
		Object correctTypeValue = theType.correctTypeOf(newValue);
		return correctTypeValue;
	}

}
