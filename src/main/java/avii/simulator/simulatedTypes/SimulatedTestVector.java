package main.java.avii.simulator.simulatedTypes;

import java.io.InputStream;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Comment;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorProcedure;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.BaseException;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedActionLanguageReader;

public class SimulatedTestVector extends SimulatedState {

	private TestVector _vector;
	private Object _assertionFailValue;
	private enum VECTOR_TYPE {SETUP,ASSERTION};
	private VECTOR_TYPE _vectorType = null;
	private BaseException _exception = null;

	public SimulatedTestVector(TestVector vector) {
		this._vector = vector;
	}

	public TestVector getVector() {
		return this._vector;
	}

	public void executeTestSetupVector() throws CannotSimulateDomainThatIsInvalidException
	{
		this._vectorType = VECTOR_TYPE.SETUP;
		this.executeTestVector(this._vector.getInitialProcedure());
		this._vectorType = null;
	}
	
	public void executeAssertionVector() throws CannotSimulateDomainThatIsInvalidException {
		this._vectorType = VECTOR_TYPE.ASSERTION;
		this.insertInitialInstancesIntoLookup();
		this.executeTestVector(this._vector.getAssertionProcedure());
		this._vectorType = null;
	}
	
	private void changeStateOfInitialInstances() {
		TestVectorInstanceMap map = null;
		try {
			map = this.getVectorInstanceMap();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		for(TestVectorClassTable table : this._vector.getTables())
		{
			for(TestVectorClassInstance instanceData : table.getInstances())
			{
				if(instanceData.hasSpecifiedInitialState())
				{
					String instanceName = instanceData.getName();
					SimulatedInstanceIdentifier instanceIdentifier = map.getIdentifierForInstanceName(instanceName);
					SimulatedInstance simulatedInstance = this._simulator.getInstanceFromIdentifier(instanceIdentifier);
					
					String stateName = instanceData.getInitialState();
										
					simulatedInstance.setStateName(stateName);
				}
			}
		}
	}

	private void insertInitialInstancesIntoLookup() {
		try {
			TestVectorInstanceMap map = this.getVectorInstanceMap();
			for(String instanceName : map.getNamedInstances())
			{
				SimulatedInstanceIdentifier instanceIdentifier = map.getIdentifierForInstanceName(instanceName);
				SimulatedInstance simulatedInstance = this._simulator.getInstanceFromIdentifier(instanceIdentifier);
				this.registerInstance(instanceName, simulatedInstance);
			}
		} catch (NameNotFoundException e) {
		}
		
	}

	private void executeTestVector(TestVectorProcedure procedure) throws CannotSimulateDomainThatIsInvalidException {
		if(procedure.hasContent())
		{
			createInstructionManager();
			InputStream procedureStream = procedure.getInputStream();
			Simulator simulatorForLanguage = (Simulator) this._simulator; 
			SimulatedActionLanguageReader simulatedActionLanguageReader = new SimulatedActionLanguageReader(procedureStream, simulatorForLanguage, this._instructionCountManager);
			try {
				simulatedActionLanguageReader.read();
			} catch (InvalidActionLanguageSyntaxException e) {
				// as the domain has already been validated, this cant happen
			}
			
			// now execute the simulated action language
			
			while(this._instructionCountManager.hasMoreInstructions() && this.noAssertionFailed())
			{
				if(this.isSetupProcedure())
				{
					this.checkIfHookCommentIsFoundAndChangeInitialStates();
				}
				this._instructionCountManager.simulateCurrentSyntax();
			}
		}
	}

	private void checkIfHookCommentIsFoundAndChangeInitialStates() {
		IActionLanguageSyntax possibleComment = this._instructionCountManager.getCurrentSyntax().getConcreteSyntax();
		if(possibleComment instanceof Syntax_Comment)
		{
			this.changeStateOfInitialInstances();
		}
	}

	private boolean noAssertionFailed() {
		return !this.hasAssertionFailed();
	}

	public boolean canExecute()
	{
		return this._simulator.hasReadyEvent();
	}
	
	public void executeNextStateProcedure() {
		this._simulator.executeNextStateProcedure();
	}

	public TestVectorInstanceMap getVectorInstanceMap() throws NameNotFoundException {
		TestVectorInstanceMap map = new TestVectorInstanceMap();
		for(TestVectorClassTable table : this._vector.getTables())
		{
			for(TestVectorClassInstance instanceData : table.getInstances())
			{
				String instanceName = instanceData.getName();
				SimulatedInstance simulatedInstance = this.getInstanceWithName(instanceName);
				map.addNamedInstance(simulatedInstance, instanceName);
			}
		}
		return map;
	}

	public boolean hasAssertionFailed() {
		return this._assertionFailValue != null;
	}

	public Object getAssertionFail() {
		return this._assertionFailValue;
	}

	public void setAssertionFailValue(Object expressionValue) {
		this._assertionFailValue = expressionValue;		
	}
	
	private boolean isSetupProcedure()
	{
		return this._vectorType == VECTOR_TYPE.SETUP;
	}
	
	@Override
	public EntityProcedure getProcedure() {
		if(this.isSetupProcedure())
		{
			return this._vector.getInitialProcedure();
		}
		return this._vector.getAssertionProcedure();
	}

	public void setException(BaseException simulatedException) {
		this._exception  = simulatedException;		
	}

	public BaseException getException() {
		return this._exception;
	}

	public int getCurrentlyExecutingLine() {
		int lineNumber = this._simulator.getSimulatingState().getCurrentExecutingLineNumber();
		return lineNumber;
	}

	public String getCurrentlyExecutingSyntax() {
		String currentSyntax = this._simulator.getSimulatingState().getCurrentlyExecutingSytnax().asString();
		return currentSyntax;
	}

	public ISimulator getSimulator() {
		return this._simulator;
	}

	public boolean hasExceptionBeenRaised() {
		return this._exception != null;
	}

	public void setSimulator(ISimulator simulator) {
		this._simulator = simulator;		
	}

	public boolean needsAttention() {
		boolean needsAttention = this._exception != null || this._assertionFailValue != null;
		return needsAttention;
	}

	public boolean isApplicableWhenWoven() {
		return this._vector.isApplicableWhenWoven();
	}

	public boolean doesSyntaxAlterRelations() {
		boolean doesSyntaxAlterRelations = this._instructionCountManager.doesSyntaxAlterRelations();
		return doesSyntaxAlterRelations;
	}
	
}
