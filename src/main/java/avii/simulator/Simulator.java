package main.java.avii.simulator;

import java.util.Collection;
import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.diagnostics.BaseInstanceLifecycleStage;
import main.java.avii.diagnostics.CreatedInstanceLifecycleStage;
import main.java.avii.diagnostics.Diagnostics;
import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.events.IEventTimeProvider;
import main.java.avii.simulator.events.SimulatedEventQueue;
import main.java.avii.simulator.events.SimulatedEventStackCoordinator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;

public class Simulator implements ISimulator, IInstanceModifier {

	private EntityDomain _domainUnderSimulation;
	private HashMap<String, SimulatedClass> _classStorage = new HashMap<String, SimulatedClass>();
	private SimulatedState _simulatedState = null;
	private SimulatedEventQueue _queue;
	private SimulatedEventStackCoordinator _coordinator;
	private HashMap<String, SimulatedRelationship> _relationships;
	private IInstanceModifier _instanceModifier = this;
	private Diagnostics _diagnostics = new Diagnostics();

	protected Simulator() {
	}

	public Simulator(EntityDomain domainUnderSumulation) throws CannotSimulateDomainThatIsInvalidException {
		EntityDomainValidator validator = new EntityDomainValidator(domainUnderSumulation);
		if (!validator.validate()) {
			throw new CannotSimulateDomainThatIsInvalidException(validator);
		}
		this._domainUnderSimulation = domainUnderSumulation;

		this.createSimulatedClasses();

		this.createSimulatedRelationships();

		this.addRelationshipsToClasses();

		this.setupForEvents();
	}

	private void addRelationshipsToClasses() {
		for (SimulatedClass simulatedClass : this._classStorage.values()) {
			EntityClass entityClass = simulatedClass.getConcreteClass();
			for (EntityRelation entityRelation : entityClass.getRelations()) {
				SimulatedRelationship simulatedRelationship = this._relationships.get(entityRelation.getName());
				simulatedClass.addRelationship(simulatedRelationship);
			}
		}
	}

	private void createSimulatedRelationships() {
		this._relationships = new HashMap<String, SimulatedRelationship>();
		for (EntityRelation relation : this._domainUnderSimulation.getRelations()) {
			SimulatedRelationship simulatedRelationship = new SimulatedRelationship(relation, this);
			this._relationships.put(simulatedRelationship.getName(), simulatedRelationship);
		}
	}

	private void setupForEvents() {
		this._queue = new SimulatedEventQueue(this);
		this._coordinator = new SimulatedEventStackCoordinator(6);
		this._coordinator.setSimulatedEventQueue(this._queue);
		this._coordinator.setSimulator(this);
	}

	public SimulatedClass getSimulatedClass(String theSimulatedClassName) {
		return _classStorage.get(theSimulatedClassName);
	}

	public SimulatedClass getSimulatedClass(EntityClass entityClass) {
		return _classStorage.get(entityClass.getName());
	}

	private void createSimulatedClasses() {
		for (EntityClass theClass : this._domainUnderSimulation.getClasses()) {
			SimulatedClass simulatedClass = null;
			if (theClass.isGeneralisation()) {
				simulatedClass = new SimulatedHierarchyClass(theClass, this);
			} else {
				simulatedClass = new SimulatedClass(theClass, this);
			}
			this._classStorage.put(simulatedClass.getName(), simulatedClass);
		}
	}

	public Collection<String> getSimulatedClassNames() {
		return this._classStorage.keySet();
	}

	public void setSimulatingVector(SimulatedTestVector vector) {
		this._simulatedState = vector;
	}

	public void setSimulatingState(EntityState concreteState) {
		this._simulatedState = new SimulatedState(concreteState, this);
	}

	public SimulatedState getSimulatingState() {
		return this._simulatedState;
	}

	public int getSimulatedInstanceCount() {
		int count = 0;
		for (SimulatedClass simulatedClass : this._classStorage.values()) {
			count += simulatedClass.instanceCount();
		}
		return count;
	}

	public boolean canIdentifyInstance(SimulatedInstanceIdentifier identifier) {
		SimulatedInstance instanceForIdentifier = this.getInstanceFromIdentifier(identifier);
		boolean instanceExists = instanceForIdentifier != null && !(instanceForIdentifier instanceof NullSimulatedInstance);

		return instanceExists;
	}

	public SimulatedInstance getInstanceFromIdentifier(SimulatedInstanceIdentifier identifier) {
		SimulatedClass simulatedClass = identifier.getSimulatedClass();
		SimulatedInstance instance = simulatedClass.getInstanceFromIdentifier(identifier);
		return instance;
	}

	public void setSimulatingInstance(SimulatedInstance instance) {
		this._simulatedState.registerInstance("self", instance);
	}

	public SimulatedInstance getSimulatingInstance() {
		try {
			SimulatedInstance self = this._simulatedState.getInstanceWithName("self");
			return self;
		} catch (NameNotFoundException e) {
			SimulatedState state = this._simulatedState;
			SimulatedClass owningClass = state.getOwningClass();
			return new NullSimulatedInstance(owningClass);
		}
	}

	public void registerEventInstance(SimulatedEventInstance eventInstance) {
		this._instanceModifier.aboutToGenerateEvent(eventInstance);
		this._queue.registerEventInstance(eventInstance);
	}

	public boolean executeNextStateProcedure() {
		Boolean wasEventIgnored = this._coordinator.executeNextStateProcedure();
		return wasEventIgnored;
	}

	public int getEventsInQueueSize() {
		return this._queue.size();
	}

	public boolean hasReadyEvent() {
		return this._queue.hasReadyEventInstance();
	}

	public SimulatedRelationship getRelationshipWithName(String relationName) {
		return this._relationships.get(relationName);
	}

	public void setMockTimeProvider(IEventTimeProvider timeProvider) {
		this._queue.setEventTimeProvider(timeProvider);
	}

	public SimulatedEventInstance cancelEvent(String eventName, SimulatedInstance sender, SimulatedInstance destination) {
		return this._queue.removeEvent(eventName, sender.getIdentifier(), destination.getIdentifier());
	}

	public void deleteAllEventsForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		this._queue.removeAllEventsForInstance(instanceIdentifier);
	}

	public Collection<SimulatedRelationship> getSimulatedRelations() {
		return this._relationships.values();
	}

	public Collection<SimulatedClass> getSimulatedClasses() {
		return this._classStorage.values();
	}

	public SimulatedEventQueue getEventQueue() {
		return this._queue;
	}

	public void ensureDestinationOfEventInstanceIsRootOfAnyHierarchy(SimulatedEventInstance eventInstance) {
		SimulatedInstanceIdentifier destinationId = eventInstance.getDestination();
		boolean wasToSelf = eventInstance.isToSelf();

		SimulatedInstance destinationInstance = this.getInstanceFromIdentifier(destinationId);
		SimulatedClass destinationSimulatedClass = destinationInstance.getSimulatedClass();
		EntityClass destinationEntityClass = destinationSimulatedClass.getConcreteClass();
		// do nothing for non-generalisation destinations
		if (!destinationEntityClass.isGeneralisation()) {
			return;
		}

		// identify the 'root' from the hierarchy instance
		SimulatedHierarchyInstance rootInstance = ((SimulatedHierarchyInstance) destinationInstance).getRootInstance();
		eventInstance.setDestination(rootInstance.getIdentifier());
		if (wasToSelf) {
			eventInstance.setSender(rootInstance.getIdentifier());
		}

		// check for toSelf event
		SimulatedInstanceIdentifier senderId = eventInstance.getSender();
		if (rootInstance.getHierarchyIdentifiers().contains(senderId)) {
			eventInstance.setSender(rootInstance.getIdentifier());
		}
	}

	public boolean hasToSelfEventsForInstanceIdentifier(SimulatedInstanceIdentifier identifier) {
		return this._queue.hasToSelfEventsForInstanceIdentifier(identifier);
	}

	public void checkRelationsForCardinalityViolations() {
		for (SimulatedRelationship relationship : this._relationships.values()) {
			relationship.checkRelationsForCardinalityViolations();
		}
	}

	public SimulatedInstance createInstance(SimulatedClass simulatedClass) {
		SimulatedInstance instance = null;
		if (simulatedClass instanceof SimulatedHierarchyClass) {
			instance = new SimulatedHierarchyInstance(simulatedClass, this);
		} else {
			instance = new SimulatedInstance(simulatedClass, this);
		}
		simulatedClass.setupNewInstance(instance);
		
		BaseInstanceLifecycleStage createdStage = new CreatedInstanceLifecycleStage(instance);
		this._diagnostics.registerInstanceStage(createdStage);
		
		return instance;
	}

	public SimulatedInstance createHierarchyInstance(SimulatedHierarchyClass simulatedClass) {
		HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance> hierarchyInstances = new HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance>();
		SimulatedInstance instance = simulatedClass.createInstance(hierarchyInstances);
		return instance;
	}

	public void setInstanceModifier(IInstanceModifier instanceModifier) {
		this._instanceModifier = instanceModifier;
	}

	public IInstanceModifier getInstanceModifier() {
		return this._instanceModifier;
	}

	public void aboutToDeleteInstance(SimulatedInstance instance) {
		// no action
	}

	public void setEventQueue(SimulatedEventQueue queue) {
		this._queue = queue;
	}

	public void setEventStackCoordinator(SimulatedEventStackCoordinator coordinator) {
		this._coordinator = coordinator;
	}

	public void aboutToGenerateEvent(SimulatedEventInstance eventInstance) {
		// no action
	}

	public void setDiagnostics(Diagnostics diagnostics) {
		this._diagnostics = diagnostics;
	}

	public Diagnostics getDiagnostics() {
		return _diagnostics;
	}

	public SimulatedEventStackCoordinator getCoordinator() {
		return this._coordinator;
	}

	public RelationshipDiagnostics createRelationshipDiagnostics() {
		RelationshipDiagnostics relationshipDiagnostics = new RelationshipDiagnostics();
		relationshipDiagnostics.calculateRelationships(this._relationships.values());
		return relationshipDiagnostics;
	}

	public InstanceSetDiagnostics createInstanceDiagnostics() {
		InstanceSetDiagnostics diagnosticInstanceSet = new InstanceSetDiagnostics();
		for(SimulatedClass simulatedClass : this._classStorage.values())
		{
			for(SimulatedInstance simulatedInstance : simulatedClass.getInstances())
			{
				diagnosticInstanceSet.addInstance(simulatedInstance);
			}
		}
		return diagnosticInstanceSet;
	}

}
