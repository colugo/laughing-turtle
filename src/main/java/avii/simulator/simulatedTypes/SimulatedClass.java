package main.java.avii.simulator.simulatedTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.relations.SimulatedRelationship;

public class SimulatedClass {

	protected EntityClass _classToSimulate;
	protected HashMap<String,SimulatedAttribute> _attributes = new HashMap<String,SimulatedAttribute>();
	protected ArrayList<SimulatedInstance> _instances = new ArrayList<SimulatedInstance>();
	protected HashMap<String,SimulatedState> _simulatedStates = new HashMap<String,SimulatedState>();
	protected HashMap<String,SimulatedEvent> _simulatedEvents = new HashMap<String,SimulatedEvent>();
	protected ISimulator _simulator;
	private int _instanceSequence = 0;
	private HashMap<String,SimulatedRelationship> _relationships = new HashMap<String,SimulatedRelationship>();

	public SimulatedClass(EntityClass theClassToSimulate, ISimulator simulator) {
		this._classToSimulate = theClassToSimulate;
		this._simulator = simulator;
		this.createSimulatedAttributes();
		this.createSimulatedStates();
		this.createSimulatedEvents();
	}
	
	protected SimulatedClass() {
	}
	
	@Override
	public String toString()
	{
		return "Simulated_" +this._classToSimulate.getDomain().getName() + ":" + this._classToSimulate.getName();
	}
	
	private void createSimulatedEvents() {
		for(EntityEventSpecification entityEventSpec : this._classToSimulate.getEventSpecifications())
		{
			this._simulatedEvents .put(entityEventSpec.getName(),new SimulatedEvent(entityEventSpec));
		}
	}
	
	private void createSimulatedStates() {
		for(EntityState entityState : this._classToSimulate.getStates())
		{
			SimulatedState state = new SimulatedState(entityState,this._simulator);
			state.setOwningClass(this);
			this._simulatedStates.put(entityState.getName(),state);
		}
	}

	private void createSimulatedAttributes()
	{
		for(EntityAttribute entityAttribute : this._classToSimulate.getAttributes())
		{
			SimulatedAttribute simulatedAttribute = new SimulatedAttribute(entityAttribute);
			this._attributes.put(simulatedAttribute.getName(), simulatedAttribute);
		}
	}

	public String getName() {
		return _classToSimulate.getName();
	}

	public Collection<SimulatedAttribute> getAttributes() {
		return this._attributes.values();
	}

	protected SimulatedInstanceIdentifier getNextSimulatedInstanceIdentifier()
	{
		SimulatedInstanceIdentifier identifier = new SimulatedInstanceIdentifier(this._instanceSequence++, this);
		return identifier;
	}
	
	private EntityState getInitialState()
	{
		return this._classToSimulate.getInitialState();
	}
	
	public boolean hasStates()
	{
		return this._classToSimulate.hasStates();
	}
	
	public SimulatedState getSimulatedStateForEntityState(EntityState theState)
	{
		SimulatedState theSimulatedState = this._simulatedStates.get(theState.getName());
		return theSimulatedState;
	}
	
	public SimulatedInstance createInstance() {
		SimulatedInstance instance = this._simulator.getInstanceModifier().createInstance(this);
		return instance;
	}

	public void setupNewInstance(SimulatedInstance instance) {
		instance.setIdentifier(getNextSimulatedInstanceIdentifier());
		addInitialStateToNewInstance(instance);
		this._instances.add(instance);
	}
	
	public void cleanupRemovedInstance(SimulatedInstance instance)
	{
		this._instances.remove(instance);
	}

	public void addInitialStateToNewInstance(SimulatedInstance instance) {
		if(this.hasStates())
		{
			SimulatedState initialSimulatedState = this.getSimulatedStateForEntityState(this.getInitialState());
			instance.setSimulatingState(initialSimulatedState);
		}
	}

	public EntityClass getConcreteClass() {
		return this._classToSimulate;
	}

	public int instanceCount() {
		return _instances.size();
	}

	public Collection<SimulatedState> getStates() {
		return this._simulatedStates.values();
	}
	
	public Object convertNewAttributeValueToCorrectDatatype(String attributeName, Object newValue)
	{
		SimulatedAttribute theAttribute = this._attributes.get(attributeName);
		IEntityDatatype theType = theAttribute.getType();
		Object correctTypeValue = theType.correctTypeOf(newValue);
		return correctTypeValue;
	}

	public ArrayList<SimulatedInstance> getInstances() {
		return this._instances;
	}
	
	public ArrayList<SimulatedInstanceIdentifier> getInstanceIds()
	{
		ArrayList<SimulatedInstanceIdentifier> ids = new ArrayList<SimulatedInstanceIdentifier>();
		for(SimulatedInstance instance : this._instances)
		{
			ids.add(instance.getIdentifier());
		}
		return ids;
	}

	public Collection<SimulatedEvent> getEvents() {
		return this._simulatedEvents.values();
	}

	public SimulatedInstance getInstanceFromIdentifier(SimulatedInstanceIdentifier identifier) {
		for(SimulatedInstance instance : this._instances)
		{
			if(instance.getIdentifier().equals(identifier))
			{
				return instance;
			}
		}
		return new NullSimulatedInstance(identifier.getSimulatedClass());
	}

	public SimulatedEvent getEventWithName(String eventName) {
		return this._simulatedEvents.get(eventName);
	}

	public SimulatedState getStateWithName(String stateName) {
		return this._simulatedStates.get(stateName);
	}

	public ISimulator getSimulator() {
		return this._simulator;
	}

	public Collection<SimulatedRelationship> getSimulatedRelationships() {
		return this._relationships.values();
	}

	public void addRelationship(SimulatedRelationship simulatedRelationship) {
		this._relationships.put(simulatedRelationship.getName(), simulatedRelationship);
	}

	public int getAndIncreaseInstanceSequence() {
		return this._instanceSequence++;
	}

}
