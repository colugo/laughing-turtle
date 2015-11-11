package main.java.avii.simulator.weave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import main.java.avii.diagnostics.BaseInstanceLifecycleStage;
import main.java.avii.diagnostics.CreatedInstanceLifecycleStage;
import main.java.avii.diagnostics.Diagnostics;
import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.weaving.ClassWeave;
import main.java.avii.editor.metamodel.weaving.DomainWeave;
import main.java.avii.editor.metamodel.weaving.EventWeave;
import main.java.avii.simulator.IInstanceModifier;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.events.SimulatedEventQueue;
import main.java.avii.simulator.events.SimulatedEventStackCoordinator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import main.java.avii.simulator.simulatedTypes.TestVectorSimulatedInstance;

public class WeaveSimulator implements ISimulator, IInstanceModifier {

	private DomainWeave _weave;
	private HashMap<String, Simulator> _simulators = new HashMap<String, Simulator>();
	private String _currentDomainName;
	private SimulatedEventQueue _queue;
	private SimulatedEventStackCoordinator _coordinator;
	private Diagnostics _diagnostics = new Diagnostics();

	public WeaveSimulator(DomainWeave weave) throws CannotSimulateDomainThatIsInvalidException {
		this._weave = weave;
		setupForEvents();
		createSimulatorStorage();
	}

	private void createSimulatorStorage() throws CannotSimulateDomainThatIsInvalidException {
		for (EntityDomain domain : this._weave.getWovenDomains()) {
			String domainName = domain.getName();
			Simulator simulator = new Simulator(domain);
			simulator.setEventQueue(this._queue);
			simulator.setEventStackCoordinator(this._coordinator);
			simulator.setInstanceModifier(this);
			simulator.setDiagnostics(_diagnostics);
			this._simulators.put(domainName, simulator);
		}
	}

	public void setCurrentDomain(EntityDomain currentDomain) {
		_currentDomainName = currentDomain.getName();
	}

	private Simulator currentSimulator() {
		return this._simulators.get(this._currentDomainName);
	}

	public Collection<String> getSimulatedClassNames() {
		Collection<String> classNames = currentSimulator().getSimulatedClassNames();
		return classNames;
	}

	public Collection<SimulatedRelationship> getSimulatedRelations() {
		Collection<SimulatedRelationship> simulatedRelations = currentSimulator().getSimulatedRelations();
		return simulatedRelations;
	}

	public Collection<SimulatedClass> getSimulatedClasses() {
		Collection<SimulatedClass> classes = currentSimulator().getSimulatedClasses();
		return classes;
	}

	public SimulatedInstance createInstance(SimulatedClass simulatedClass) {
		EntityClass concreteClass = simulatedClass.getConcreteClass();
		ClassWeave weaveContainingClass = this._weave.getClassWeaveContainingClass(concreteClass);
		if (weaveContainingClass == null) {
			SimulatedInstance instance = new SimulatedInstance(simulatedClass, this);
			simulatedClass.setupNewInstance(instance);
			
			BaseInstanceLifecycleStage createdStage = new CreatedInstanceLifecycleStage(instance);
			this._diagnostics.registerInstanceStage(createdStage);
			
			return instance;
		}

		Collection<EntityClass> classesInWeave = getClassesInWeaveWithoutExceptions(concreteClass, weaveContainingClass);
		InstanceWeave instanceWeave = new InstanceWeave(weaveContainingClass);

		SimulatedInstance instanceForRequestedClass = null;

		for (EntityClass theClass : classesInWeave) {
			String domainName = theClass.getDomain().getName();

			Simulator simulator = this._simulators.get(domainName);
			SimulatedClass simulatedWeaveClass = simulator.getSimulatedClass(theClass);

			SimulatedInstance newInstance = createInstanceAsHierarchyIfNeeded(simulatedClass, simulator, simulatedWeaveClass);
			instanceWeave.putInstance(newInstance);

			if (domainName.equals(this._currentDomainName)) {
				instanceForRequestedClass = newInstance;
			}
		}

		instanceWeave.setupInitialValuesOfAttribtues();
		return instanceForRequestedClass;

	}

	private Collection<EntityClass> getClassesInWeaveWithoutExceptions(EntityClass concreteClass, ClassWeave weaveContainingClass) {
		Collection<EntityClass> classesInWeave = null;
		classesInWeave = weaveContainingClass.getWovenClasses();
		return classesInWeave;
	}

	private SimulatedInstance createInstanceAsHierarchyIfNeeded(SimulatedClass simulatedClass, Simulator simulator, SimulatedClass simulatedWeaveClass) {
		SimulatedInstance newInstance;
		if (simulatedWeaveClass instanceof SimulatedHierarchyClass) {
			newInstance = simulator.createHierarchyInstance((SimulatedHierarchyClass) simulatedWeaveClass);
		} else {
			newInstance = simulator.createInstance(simulatedWeaveClass);
		}
				
		return newInstance;
	}

	public SimulatedInstance createHierarchyInstance(SimulatedHierarchyClass simulatedClass) {
		SimulatedHierarchyInstance instance = new SimulatedHierarchyInstance(simulatedClass, this);
		simulatedClass.setupNewInstance(instance);
		return instance;
	}

	public SimulatedClass getSimulatedClass(String theSimulatedClassName) {
		return currentSimulator().getSimulatedClass(theSimulatedClassName);
	}

	public void setSimulatingState(EntityState state) {
		EntityClass theClass = state.getOwningClass();
		EntityDomain domain = theClass.getDomain();
		this.setCurrentDomain(domain);
		Simulator simulator = this.getSimulatorForClass(theClass);
		simulator.setSimulatingState(state);
	}
	
	@Override
	public SimulatedInstance getSimulatingInstance() {
		SimulatedInstance simulatingInstance = currentSimulator().getSimulatingInstance();
		return simulatingInstance;
	}

	public void setSimulatingVector(SimulatedTestVector simulatedTestVector) {
		EntityDomain domain = simulatedTestVector.getVector().getScenario().getDomain();
		this.setCurrentDomain(domain);
		Simulator simulator = this._simulators.get(domain.getName());
		simulator.setSimulatingVector(simulatedTestVector);
	}

	public SimulatedState getSimulatingState() {
		SimulatedState simulatingState = currentSimulator().getSimulatingState();
		return simulatingState;
	}

	public int getSimulatedInstanceCount() {
		int count = 0;
		for (Simulator simulator : this._simulators.values()) {
			count += simulator.getSimulatedInstanceCount();
		}
		return count;
	}

	public void aboutToDeleteInstance(SimulatedInstance instance) {
		// lookup the other instances, and call their delete method
		if (!instance.isInWeave()) {
			return;
		}
		InstanceWeave weave = instance.getInstanceWeave();

		Collection<SimulatedInstance> instancesInWeave = weave.getInstances();
		for (SimulatedInstance instanceInWeave : instancesInWeave) {
			if (instanceInWeave == instance) {
				continue;
			}
			instanceInWeave.deleteCAMInstance();
		}
	}

	private void setupForEvents() {
		this._queue = new SimulatedEventQueue(this);
		this._coordinator = new SimulatedEventStackCoordinator(6);
		this._coordinator.setSimulatedEventQueue(this._queue);
		this._coordinator.setSimulator(this);
	}

	public SimulatedInstance getInstanceFromIdentifier(SimulatedInstanceIdentifier id) {
		SimulatedClass simulatedClass = id.getSimulatedClass();
		EntityClass entityClass = simulatedClass.getConcreteClass();
		Simulator simulator = getSimulatorForClass(entityClass);
		SimulatedInstance instance = simulator.getInstanceFromIdentifier(id);

		return instance;
	}

	private SimulatedClass getSimulatedClassFromEntityClass(EntityClass theEntityClass) {
		Simulator simulatorForClass = getSimulatorForClass(theEntityClass);
		SimulatedClass simulatedClassForClass = simulatorForClass.getSimulatedClass(theEntityClass);
		return simulatedClassForClass;
	}

	private Simulator getSimulatorForClass(EntityClass theEntityClass) {
		EntityDomain domainForClass = theEntityClass.getDomain();
		Simulator simulatorForClass = this._simulators.get(domainForClass.getName());
		return simulatorForClass;
	}

	private SimulatedEvent getSimulatedEventFromEntityEventSpec(EntityEventSpecification eventSpec) {
		EntityClass theEntityClass = eventSpec.getKlass();
		SimulatedClass simulatedClass = getSimulatedClassFromEntityClass(theEntityClass);
		SimulatedEvent simulatedEvent = simulatedClass.getEventWithName(eventSpec.getName());
		return simulatedEvent;
	}

	@Override
	public void aboutToGenerateEvent(SimulatedEventInstance eventInstance) {

		EntityEventSpecification eventSpec = eventInstance.getSpecification().getConcreteEvent();

		EntityDomain originatingDomain = eventSpec.getKlass().getDomain();
		Simulator originatingSimulator = this._simulators.get(originatingDomain.getName());
		SimulatedInstance instance = originatingSimulator.getInstanceFromIdentifier(eventInstance.getDestination());

		if (!instance.isInWeave()) {
			return;
		}

		registerWovenEvents(eventSpec, instance);
	}

	private void registerWovenEvents(EntityEventSpecification eventSpec, SimulatedInstance instance) {
		InstanceWeave instanceWeave = instance.getInstanceWeave();
		ClassWeave classWeave = instanceWeave.getClassWeave();

		EventWeave eventWeave = classWeave.getEventWeaveContainingAttribute(eventSpec);

		if (eventWeave == null) {
			return;
		}

		for (EntityEventSpecification eventSpecInWeave : eventWeave.getEvents()) {
			if (eventSpecInWeave == eventSpec) {
				continue;
			}

			EntityClass entityClassForEvent = eventSpecInWeave.getKlass();
			SimulatedClass simulatedClassForEvent = getSimulatedClassFromEntityClass(entityClassForEvent);
			SimulatedInstance instanceOfClassForEvent = instanceWeave.getInstanceForClass(simulatedClassForEvent);
			SimulatedEvent simulatedEventForEvent = getSimulatedEventFromEntityEventSpec(eventSpecInWeave);

			String eventName = eventSpecInWeave.getName();
			SimulatedEventInstance eventInstanceForEvent = new SimulatedEventInstance(simulatedEventForEvent, instance.getIdentifier(),
					instanceOfClassForEvent.getIdentifier());
			eventInstanceForEvent.setName(eventName);

			this._queue.registerEventInstance(eventInstanceForEvent);
		}
	}

	public void ensureDestinationOfEventInstanceIsRootOfAnyHierarchy(SimulatedEventInstance eventInstance) {
		SimulatedEvent simulatedEvent = eventInstance.getSpecification();
		EntityEventSpecification eventSpec = simulatedEvent.getConcreteEvent();
		EntityClass entityClass = eventSpec.getKlass();
		EntityDomain domian = entityClass.getDomain();
		Simulator simulator = this._simulators.get(domian.getName());
		simulator.ensureDestinationOfEventInstanceIsRootOfAnyHierarchy(eventInstance);
	}

	public boolean executeNextStateProcedure() {
		Boolean wasEventIgnored = this._coordinator.executeNextStateProcedure();
		return wasEventIgnored;
	}

	public void setSimulatingInstance(SimulatedInstance simulatedInstance) {
		Simulator simulator = getSimulatorFromSimulatedInstance(simulatedInstance);
		simulator.setSimulatingInstance(simulatedInstance);
	}

	private Simulator getSimulatorFromSimulatedInstance(SimulatedInstance simulatedInstance) {

		if (simulatedInstance instanceof TestVectorSimulatedInstance) {
			return currentSimulator();
		}

		SimulatedClass simulatedClass = simulatedInstance.getSimulatedClass();
		EntityClass entityClass = simulatedClass.getConcreteClass();
		Simulator simulator = this.getSimulatorForClass(entityClass);
		return simulator;
	}

	public boolean hasReadyEvent() {
		boolean hasReadyEvent = this._queue.hasReadyEventInstance();
		return hasReadyEvent;
	}

	@Override
	public boolean hasToSelfEventsForInstanceIdentifier(SimulatedInstanceIdentifier identifier) {
		return this._queue.hasToSelfEventsForInstanceIdentifier(identifier);
	}

	public void checkRelationsForCardinalityViolations() {
		for (Simulator simulator : this._simulators.values()) {
			simulator.checkRelationsForCardinalityViolations();
		}
	}

	public Simulator getSimulatorForDomain(EntityDomain domain) {
		return this._simulators.get(domain.getName());
	}

	public SimulatedEventStackCoordinator getCoordinator() {
		return this._coordinator;
	}
	
	public RelationshipDiagnostics createRelationshipDiagnostics() {
		ArrayList<SimulatedRelationship> relationships = new ArrayList<SimulatedRelationship>();
		for(Simulator simulator : this._simulators.values())
		{
			relationships.addAll(simulator.getSimulatedRelations());
		}
		RelationshipDiagnostics relationshipDiagnostics = new RelationshipDiagnostics();
		relationshipDiagnostics.calculateRelationships(relationships);
		return relationshipDiagnostics;
	}
	
	public InstanceSetDiagnostics createInstanceDiagnostics() {
		InstanceSetDiagnostics diagnosticInstanceSet = new InstanceSetDiagnostics();
		for(Simulator simulator : this._simulators.values())
		{
			for(SimulatedClass simulatedClass : simulator.getSimulatedClasses())
			{
				for(SimulatedInstance simulatedInstance : simulatedClass.getInstances())
				{
					diagnosticInstanceSet.addInstance(simulatedInstance);
				}
			}
		}
		return diagnosticInstanceSet;
	}
	
	public void setDiagnostics(Diagnostics diagnostics) {
		this._diagnostics = diagnostics;
	}

	public Diagnostics getDiagnostics() {
		return _diagnostics;
	}
	
	public boolean canIdentifyInstance(SimulatedInstanceIdentifier id) {
		// TODO Auto-generated method stub
		throw new NullPointerException();
	}

	public IInstanceModifier getInstanceModifier() {
		// TODO Auto-generated method stub
		throw new NullPointerException();
	}

	public void deleteAllEventsForInstance(SimulatedInstanceIdentifier _identifier) {
		// TODO Auto-generated method stub
		throw new NullPointerException();
	}

	public SimulatedRelationship getRelationshipWithName(String name) {
		// TODO Auto-generated method stub
		throw new NullPointerException();
	}

}
