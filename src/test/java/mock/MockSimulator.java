package test.java.mock;

import java.util.Collection;
import java.util.HashMap;

import main.java.avii.diagnostics.Diagnostics;
import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.IInstanceModifier;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.InstructionManager;
import main.java.avii.simulator.events.SimulatedEventStackCoordinator;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;

public class MockSimulator implements ISimulator {
	private HashMap<String, MockSimulatedClass> _classes = new HashMap<String, MockSimulatedClass>();
	private IInstanceModifier instanceCreator = new MockInstanceCreator();
	private InstructionManager _programInstructionCountManager = new InstructionManager();

	public SimulatedClass getSimulatedClass(String theSimulatedClassName) {
		return this._classes.get(theSimulatedClassName);
	}

	public InstructionManager getProgramInstructionCountManager() {

		return this._programInstructionCountManager;
	}

	public void addMockClass(MockSimulatedClass mockSimulatedClass) {
		this._classes.put(mockSimulatedClass.getName(), mockSimulatedClass);
	}

	public void ensureDestinationOfEventInstanceIsRootOfAnyHierarchy(SimulatedEventInstance eventInstance) {
		// do nothing
	}

	public Collection<String> getSimulatedClassNames() {
		return null;
	}

	public Collection<SimulatedRelationship> getSimulatedRelations() {
		return null;
	}

	public void setSimulatingState(EntityState state) {
	}

	public SimulatedState getSimulatingState() {
		return null;
	}

	public boolean canIdentifyInstance(SimulatedInstanceIdentifier id) {
		return false;
	}

	public SimulatedInstance getInstanceFromIdentifier(SimulatedInstanceIdentifier id) {
		return null;
	}

	public int getSimulatedInstanceCount() {
		return 0;
	}

	public Collection<SimulatedClass> getSimulatedClasses() {
		return null;
	}

	public IInstanceModifier getInstanceModifier() {
		return this.instanceCreator;
	}

	public boolean executeNextStateProcedure() {

		return false;
	}

	public void setSimulatingInstance(SimulatedInstance simulatedInstance) {

	}

	public boolean hasReadyEvent() {

		return false;
	}

	public void resetProgramInstructionCountManager() {

	}

	public void setSimulatingVector(SimulatedTestVector simulatedTestVector) {

	}

	public void checkRelationsForCardinalityViolations() {

	}

	public void deleteAllEventsForInstance(SimulatedInstanceIdentifier _identifier) {

	}

	public SimulatedRelationship getRelationshipWithName(String name) {

		return null;
	}

	public boolean hasToSelfEventsForInstanceIdentifier(SimulatedInstanceIdentifier destination) {
		return false;
	}

	public void setDiagnostics(Diagnostics diagnostics) {
	}

	public Diagnostics getDiagnostics() {
		return null;
	}

	public SimulatedInstance getSimulatingInstance() {
		return null;
	}

	public SimulatedEventStackCoordinator getCoordinator() {
		return null;
	}

	public RelationshipDiagnostics createRelationshipDiagnostics() {
		return null;
	}

	public InstanceSetDiagnostics createInstanceDiagnostics() {
		return null;
	}

}
