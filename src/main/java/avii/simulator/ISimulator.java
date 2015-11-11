package main.java.avii.simulator;

import java.util.Collection;

import main.java.avii.diagnostics.Diagnostics;
import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.events.SimulatedEventStackCoordinator;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;

public interface ISimulator {

	public SimulatedClass getSimulatedClass(String theSimulatedClassName);

	public void ensureDestinationOfEventInstanceIsRootOfAnyHierarchy(SimulatedEventInstance eventInstance);

	public Collection<String> getSimulatedClassNames();

	public Collection<SimulatedRelationship> getSimulatedRelations();

	public void setSimulatingState(EntityState state);

	public SimulatedState getSimulatingState();

	public boolean canIdentifyInstance(SimulatedInstanceIdentifier id);

	public SimulatedInstance getInstanceFromIdentifier(SimulatedInstanceIdentifier id);

	public int getSimulatedInstanceCount();

	public Collection<SimulatedClass> getSimulatedClasses();
	
	public IInstanceModifier getInstanceModifier();

	public boolean executeNextStateProcedure();

	public void setSimulatingInstance(SimulatedInstance simulatedInstance);
	
	public SimulatedInstance getSimulatingInstance();

	public boolean hasReadyEvent();

	public void setSimulatingVector(SimulatedTestVector simulatedTestVector);

	public void checkRelationsForCardinalityViolations();

	public void deleteAllEventsForInstance(SimulatedInstanceIdentifier _identifier);

	public SimulatedRelationship getRelationshipWithName(String name);

	public boolean hasToSelfEventsForInstanceIdentifier(SimulatedInstanceIdentifier destination);
	
	public void setDiagnostics(Diagnostics diagnostics);
	
	public Diagnostics getDiagnostics();
	
	public SimulatedEventStackCoordinator getCoordinator();

	public RelationshipDiagnostics createRelationshipDiagnostics();

	public InstanceSetDiagnostics createInstanceDiagnostics();

}