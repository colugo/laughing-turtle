package main.java.avii.diagnostics;

import java.util.ArrayList;

import javax.imageio.metadata.IIOMetadataNode;

import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public abstract class BaseInstanceLifecycleStage {

	protected SimulatedInstance _instance;
	protected EntityState _currentState;
	protected ProcedureDiagnostics _procedureDiagnostics = new ProcedureDiagnostics();

	public BaseInstanceLifecycleStage(SimulatedInstance instance) {
		this._instance = instance;
		if (instance.getSimulatedState() != null) {
			this._currentState = instance.getSimulatedState().getConcreteState();
		}
	}

	public SimulatedInstance getInstance() {
		return this._instance;
	}

	public EntityState getCurrentState() {
		return this._currentState;
	}

	public void setCurrentState(EntityState currentState) {
		this._currentState = currentState;
	}

	protected String getStateName() {
		String currentStateName = "";
		if (this._currentState != null) {
			return this._currentState.getName();
		}
		return currentStateName;
	}

	public boolean hasWaitingEvents() {
		return false;
	}

	public ProcedureDiagnostics getProcedureDiagnostics() {
		return _procedureDiagnostics;
	}

	public void registerStatement(StatementExecutedDiagnosticsType type) {
		this._procedureDiagnostics.addStatement(type);		
	}

	public abstract String getStageLog();

	public ArrayList<SimulatedEventInstance> getWaitingEventsForInstance(SimulatedInstanceIdentifier id) {
		return new ArrayList<SimulatedEventInstance>();
	}

	public abstract IIOMetadataNode serialise(int sequence);

	protected IIOMetadataNode createBaseNode(String type, int sequence)
	{
		IIOMetadataNode node = new IIOMetadataNode("stage");
		node.setAttribute("sequence", Integer.toString(sequence));
		node.setAttribute("type", type);
		return node;
	}
}