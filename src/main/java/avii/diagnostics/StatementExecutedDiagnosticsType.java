package main.java.avii.diagnostics;

import java.util.ArrayList;

import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.ISimulatedActionLanguage;

public class StatementExecutedDiagnosticsType {

	private ISimulatedActionLanguage _statement;
	private SimulatedInstance _instance;
	private EventQueueDiagnostics _eventDiagnostics;
	private RelationshipDiagnostics _relationshipDiagnostics;
	private InstanceSetDiagnostics _instanceSet;
	private NamedInstanceAndVariableDiagnostics _namedInstancesAndVariables;

	public void setStatement(ISimulatedActionLanguage statement) {
		this._statement = statement;
	}

	public ISimulatedActionLanguage getStatement() {
		return this._statement;
	}

	@Override
	public String toString()
	{
		return this._statement.getLineNumber() + " : " + this._statement.asString();
	}

	public void setSimulatingInstance(SimulatedInstance instance) {
		this._instance = instance;		
	}
	
	public void setEventDiagnostics(EventQueueDiagnostics eventQueue)
	{
		this._eventDiagnostics = eventQueue;
	}
	
	public SimulatedInstance getInstance()
	{
		return this._instance;
	}

	public ArrayList<SimulatedEventInstance> getWaitingEventsForInstance(SimulatedInstanceIdentifier id) {
		return _eventDiagnostics.getEventsForInstance(id);
	}

	public void setRelationshipDiagnostics(RelationshipDiagnostics relationshipDiagnostics) {
		this._relationshipDiagnostics = relationshipDiagnostics;
	}

	public RelationshipDiagnostics getRelationshipDiagnostics() {
		return this._relationshipDiagnostics;
	}

	public InstanceSetDiagnostics getInstanceDiagnostics() {
		return this._instanceSet;
	}
	
	public void setDiagnosticInstanceSet(InstanceSetDiagnostics instanceSet)
	{
		this._instanceSet = instanceSet;
	}

	public NamedInstanceAndVariableDiagnostics getNamedInstanceAndVariableDiagnostics() {
		return this._namedInstancesAndVariables;
	}
	
	public void setNamedInstanceAndVariablesDiagnostics(NamedInstanceAndVariableDiagnostics namedInstancesAndVariables)
	{
		this._namedInstancesAndVariables = namedInstancesAndVariables;
	}

	public EventQueueDiagnostics getEventQueueDiagnostics() {
		return this._eventDiagnostics;
	}
}
