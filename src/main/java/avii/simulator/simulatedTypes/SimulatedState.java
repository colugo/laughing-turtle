package main.java.avii.simulator.simulatedTypes;

import java.io.InputStream;

import javax.naming.NameNotFoundException;

import main.java.avii.diagnostics.InstanceSetDiagnostics;
import main.java.avii.diagnostics.NamedInstanceAndVariableDiagnostics;
import main.java.avii.diagnostics.RelationshipDiagnostics;
import main.java.avii.diagnostics.StatementExecutedDiagnosticsType;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenRcvdEvent;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenTemp;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.InstructionManager;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.IExpressionTokenLookup;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.ISimulatedActionLanguage;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedActionLanguageReader;

public class SimulatedState implements IExpressionTokenLookup {

	private EntityState _concreteState;
	private ScopeManager _scopeManager = new ScopeManager();
	protected ISimulator _simulator;
	private SimulatedClass _owningClass = null;
	private SimulatedEventInstance _triggeringEvent;
	protected InstructionManager _instructionCountManager;

	public SimulatedState(EntityState concreteState, ISimulator simulator) {
		this._concreteState = concreteState;
		this._simulator = simulator;
	}

	public String toString()
	{
		return "Simulated_" + this._concreteState.getName();
	}
	
	protected SimulatedState()
	{
		// hidden constructor for SimulatedTestVector
	}

	public String getNameForInstanceId(SimulatedInstanceIdentifier instanceId)
	{
		return this._scopeManager.getNameForInstance(instanceId);
	}
	
	public String getName() {
		return this._concreteState.getName();
	}

	public SimulatedInstance getInstanceWithName(String instanceName) throws NameNotFoundException {
		return this._scopeManager.getInstanceWithName(instanceName);
	}

	public void registerInstance(String instanceName, SimulatedInstance theInstance) {
		this._scopeManager.registerInstance(instanceName, theInstance);
	}

	public void setTempVariable(String tempName, Object expressionValue) {
		this._scopeManager.setTempVariable(tempName,expressionValue);
	}
	
	public Object getTempVariable(String tempName) {
		return this._scopeManager.getTempVariable(tempName);
	}

	
	public void simulate() {
		createInstructionManager();
		
		EntityProcedure procedure = this._concreteState.getProcedure();
		
		if(procedure.hasContent())
		{
			InputStream procedureStream = procedure.getInputStream();
			SimulatedActionLanguageReader simulatedActionLanguageReader = new SimulatedActionLanguageReader(procedureStream, this._simulator, _instructionCountManager);
			try {
				simulatedActionLanguageReader.read();
			} catch (InvalidActionLanguageSyntaxException e) {
				// as the domain has already been validated, this cant happen
				e.printStackTrace();
			}
			
			// now execute the simulated action language
			while(_instructionCountManager.hasMoreInstructions())
			{
				
				ISimulatedActionLanguage currentSyntax = _instructionCountManager.getCurrentSyntax();
				_instructionCountManager.simulateCurrentSyntax();
				
				handleDiagnostics(currentSyntax);
			}
		}
	}

	private void handleDiagnostics(ISimulatedActionLanguage currentSyntax) {
		StatementExecutedDiagnosticsType type = new StatementExecutedDiagnosticsType();
		type.setStatement(currentSyntax);
		addProcedureDiagnostics(type);
		
		this._simulator.getDiagnostics().instructionExecuted(type);
	}

	private void addProcedureDiagnostics(StatementExecutedDiagnosticsType type) {
		type.setSimulatingInstance(this._simulator.getSimulatingInstance());
		type.setEventDiagnostics(this._simulator.getCoordinator().createDiagnostics());
		
		RelationshipDiagnostics relationshipDiagnostics = this._simulator.createRelationshipDiagnostics();
		type.setRelationshipDiagnostics(relationshipDiagnostics);
		
		InstanceSetDiagnostics diagnosticInstanceSet = this._simulator.createInstanceDiagnostics();
		type.setDiagnosticInstanceSet(diagnosticInstanceSet);
		
		NamedInstanceAndVariableDiagnostics namedInstanceDiagnostics = this._scopeManager.getNamedInstanceDiagnostics();
		type.setNamedInstanceAndVariablesDiagnostics(namedInstanceDiagnostics);
	}

	protected void createInstructionManager() {
		_instructionCountManager = new InstructionManager();
	}

	public String lookupExpressionToken(IActionLanguageToken token) {

		int currentLineNumber = this.getCurrentExecutingLineNumber();
		
		if(token instanceof ActionLanguageTokenAttribute)
		{
			ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) token;
			String instanceName = attributeToken.getInstanceName();
			String attributeName = attributeToken.getAttributeName();
			try {
				SimulatedInstance instance = this.getInstanceWithName(instanceName);
				IEntityDatatype datatype = this.getProcedure().getDatatypeForNameOnLine(instanceName + "." + attributeName, currentLineNumber);
				Object attributeValue = instance.getAttribute(attributeName);
				String attributeStringValue = datatype.getLookupValue(attributeValue.toString());
				return attributeStringValue;

			} catch (NameNotFoundException e) {
				// this cant fail, its been validated
			}
		}
		if(token instanceof ActionLanguageTokenTemp)
		{
			ActionLanguageTokenTemp tempToken = (ActionLanguageTokenTemp) token;
			String tempName = tempToken.getName();
			IEntityDatatype datatype = this.getProcedure().getDatatypeForNameOnLine(tempName, currentLineNumber);
			
			Object tempValue = this.getTempVariable(tempName);
			String tempStringValue = datatype.getLookupValue(tempValue.toString());
			return tempStringValue;
		}
		if(token instanceof ActionLanguageTokenRcvdEvent)
		{
			ActionLanguageTokenRcvdEvent eventToken = (ActionLanguageTokenRcvdEvent) token;
			String eventParamName = eventToken.getParamName();
			
			IEntityDatatype datatype = this.getProcedure().getDatatypeForNameOnLine("rcvd_event." + eventParamName, currentLineNumber);
			String paramValue = this._triggeringEvent.getParam(eventParamName).toString();
			paramValue = datatype.getLookupValue(paramValue);
			return paramValue;
		}
		
		return null;
	}

	public EntityProcedure getProcedure() {
		return this._concreteState.getProcedure();
	}

	public SimulatedInstanceSet getInstanceSetWithName(String instanceSetName) {
		return this._scopeManager.getInstanceSetWithName(instanceSetName);
	}

	public void registerInstanceSet(String instanceSetName, SimulatedInstanceSet instanceSet) {
		this._scopeManager.registerInstanceSet(instanceSetName, instanceSet);
	}

	public EntityState getConcreteState() {
		return this._concreteState;
	}

	public SimulatedClass getOwningClass() {
		return this._owningClass;
	}

	public void setOwningClass(SimulatedClass simulatedClass) {
		this._owningClass = simulatedClass;
	}

	public void setTriggeringEvent(SimulatedEventInstance event) {
		this._triggeringEvent = event;		
	}

	public int getCurrentExecutingLineNumber() {
		int lineNumber = _instructionCountManager.getProgramCounter();
		return lineNumber;
	}

	public ISimulatedActionLanguage getCurrentlyExecutingSytnax() {
		ISimulatedActionLanguage syntax = _instructionCountManager.getCurrentSyntax();
		return syntax;
	}

	public void deRegisterInstance(String instanceName) {
		this._scopeManager.deRegisterInstance(instanceName);
	}
	
}
