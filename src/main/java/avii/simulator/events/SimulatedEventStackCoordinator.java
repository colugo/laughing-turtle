package main.java.avii.simulator.events;

import java.util.ArrayList;

import main.java.avii.diagnostics.EventConsumedInstanceLifecycleStage;
import main.java.avii.diagnostics.EventQueueDiagnostics;
import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedEventStackCoordinator {

	private ArrayList<SimulatedEventStack> _stacks = null;
	private int _stackHead = 0;
	private SimulatedEventQueue _eventQueue;
	private ISimulator _simulator;

	public SimulatedEventStackCoordinator(int initialCapacity) {
		this._stacks = new ArrayList<SimulatedEventStack>();
		for (int i = 0; i < initialCapacity; i++) {
			SimulatedEventStack stack = new SimulatedEventStack();
			this._stacks.add(stack);
		}
	}

	public int getStackCount() {
		return this._stacks.size();
	}

	public ArrayList<SimulatedEventStack> getStacks() {
		return this._stacks;
	}

	public void setSimulatedEventQueue(SimulatedEventQueue queue) {
		this._eventQueue = queue;
	}

	/**
	 * 
	 * @return true if event was executed, false if event was ignored
	 */
	public boolean executeNextStateProcedure() {
		ArrayList<SimulatedInstanceIdentifier> instancesWithStacks = this.getInstancesWithStacks();
		this._eventQueue.setLockedInstances(instancesWithStacks);

		if (!this._eventQueue.hasReadyEventInstance()) {
		
			this._eventQueue.clearLockedInstances();
			// now try locked instances
			if (!this._eventQueue.hasReadyEventInstance()) {
				return false;
			}
		}

		SimulatedEventInstance event = this._eventQueue.getNextEventInstance();
		
		SimulatedEventStack nextStack = this.getNextStack();
		nextStack.setCurrentInstance(event.getDestination());

		boolean wasEventValid = false;
		if (this._simulator != null) {
			wasEventValid = configureSimulatorForExecutionOfEvent(event);
			boolean hasToSelf = this._simulator.hasToSelfEventsForInstanceIdentifier(event.getDestination());
			if (!hasToSelf) {
				nextStack.freeUp();
			}
		}
		return wasEventValid;
	}

	private boolean configureSimulatorForExecutionOfEvent(SimulatedEventInstance event) {
		SimulatedInstance instance = this._simulator.getInstanceFromIdentifier(event.getDestination());
		EntityState currentState = instance.getSimulatedState().getConcreteState();
		EntityState nextState = currentState.getNextStateForEventSpecification(event.getSpecification().getConcreteEvent());
		
		EventConsumedInstanceLifecycleStage stage = new EventConsumedInstanceLifecycleStage(event, instance);
		EventQueueDiagnostics eventQueueDiagnostics = this._eventQueue.createDiagnostics();
		stage.setEventDiagnostics(eventQueueDiagnostics);
		RelationshipDiagnostics relationshipDiagnostics = this._simulator.createRelationshipDiagnostics();
		stage.setRelationshipDiagnostics(relationshipDiagnostics);
		InstanceSetDiagnostics diagnosticInstanceSet = this._simulator.createInstanceDiagnostics();
		stage.setInstanceDiagnostics(diagnosticInstanceSet);

		
		
		this._simulator.getDiagnostics().registerInstanceStage(stage);
		
		if(nextState == null)
		{
			stage.ignore();
			return false;
		}
		
		this._simulator.setSimulatingState(nextState);
		this._simulator.setSimulatingInstance(instance);
		this._simulator.getSimulatingState().setTriggeringEvent(event);
		instance.setSimulatingState(this._simulator.getSimulatingState());
		this._simulator.getSimulatingState().simulate();
		stage.setCurrentState(nextState);
		diagnosticInstanceSet.changeStateOfInstance(instance, nextState);
		
		return true;
	}

	private ArrayList<SimulatedInstanceIdentifier> getInstancesWithStacks() {
		ArrayList<SimulatedInstanceIdentifier> instancesWithStacks = new ArrayList<SimulatedInstanceIdentifier>();
		for (SimulatedEventStack stack : this._stacks) {
			if (!stack.isFree()) {
				instancesWithStacks.add(stack.getCurrentInstance());
			}
		}
		return instancesWithStacks;
	}

	private SimulatedEventStack getNextStack() {
		SimulatedEventStack stack = this._stacks.get(this._stackHead);
		this._stackHead++;
		if (this._stackHead == this._stacks.size()) {
			this._stackHead = 0;
		}
		return stack;
	}

	public boolean canAdvance() {
		/*
		 * why cant I advance? queue has no ready events, considering locked
		 * classes ( there might be delayed events )
		 */
		boolean queueHasReadyEvents = this._eventQueue.hasReadyEventInstance();
		return queueHasReadyEvents;
	}

	public void setSimulator(ISimulator simulator) {
		this._simulator = simulator;
	}

	public boolean hasStackForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		for (SimulatedEventStack stack : this._stacks) {
			if (!stack.isFree()) {
				if (stack.getCurrentInstance().equals(instanceIdentifier)) {
					return true;
				}
			}
		}
		return false;
	}

	public EventQueueDiagnostics createDiagnostics() {
		return this._eventQueue.createDiagnostics();
	}

}
