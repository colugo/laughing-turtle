package test.java.tests;

import java.util.ArrayList;

import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.TestScenario;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.exceptions.RelationCardinalityException;
import main.java.avii.simulator.exceptions.SimulationException;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import main.java.avii.simulator.simulatedTypes.TestVectorSimulatedInstance;

public class TestHarness {

	private EntityDomain _domain;
	private ArrayList<SimulatedTestScenario> _scenarios = new ArrayList<SimulatedTestScenario>();
	private boolean _haveAllAssertionsPassed = true;
	private boolean _wereExceptionsRaised;

	protected TestHarness() {
		// hidden constructor for WeaveTestHarness
	}

	public TestHarness(EntityDomain domain) throws CannotSimulateDomainThatIsInvalidException {
		this._domain = domain;
		EntityDomainValidator validator = new EntityDomainValidator(domain);
		if (!validator.validate()) {
			throw new CannotSimulateDomainThatIsInvalidException(validator);
		}
		this.createSimulatedScenariosForDomain(this._domain);
	}

	protected void createSimulatedScenariosForDomain(EntityDomain domain) {
		for (TestScenario scenario : domain.getScenarios()) {
			SimulatedTestScenario simulatedScenario = new SimulatedTestScenario(scenario);
			this._scenarios.add(simulatedScenario);
		}
	}

	public ArrayList<SimulatedTestScenario> getScenarios() {
		return this._scenarios;
	}

	public void execute() throws CannotSimulateDomainThatIsInvalidException {
		for (SimulatedTestScenario scenario : this._scenarios) {
			for (SimulatedTestVector vector : scenario.getSimulatedVectors()) {
				if(vector.isApplicableWhenWoven())
				{
					executeVector(vector);
				}
			}
		}
	}
	
	public void executeVector(SimulatedTestVector vector) throws CannotSimulateDomainThatIsInvalidException {
		try {
			ISimulator simulator = createSimulatorForExecution(vector);
			
			simulator.setSimulatingVector(vector);
			simulator.setSimulatingInstance(new TestVectorSimulatedInstance());
			vector.setSimulator(simulator);
			
			vector.executeTestSetupVector();
			checkRelationsAreValidRegardlessOfAlteringStatements(vector);

			while (vector.canExecute()) {
				vector.executeNextStateProcedure();
				checkRelationsAreValid(vector);
			}

			simulator.setSimulatingVector(vector);
			simulator.setSimulatingInstance(new TestVectorSimulatedInstance());
			
			vector.executeAssertionVector();
			checkRelationsAreValidRegardlessOfAlteringStatements(vector);
			
			if (vector.hasAssertionFailed()) {
				this._haveAllAssertionsPassed = false;
			}
		} catch (SimulationException simulatedException) {
			setupException(vector, simulatedException);
		} catch (RelationCardinalityException relationException) {
			setupException(vector, relationException);
		}
	}

	protected ISimulator createSimulatorForExecution(SimulatedTestVector vector) throws CannotSimulateDomainThatIsInvalidException {
		Simulator simulator = new Simulator(vector.getVector().getScenario().getDomain());
		return simulator;
	}

	private void setupException(SimulatedTestVector vector, RelationCardinalityException relationException) {
		vector.setException(relationException);
		this._wereExceptionsRaised = true;
	}

	private void checkRelationsAreValid(SimulatedTestVector vector) {
		if (vector.doesSyntaxAlterRelations()) {
			vector.getSimulator().checkRelationsForCardinalityViolations();
		}
	}

	private void checkRelationsAreValidRegardlessOfAlteringStatements(SimulatedTestVector vector) {
		vector.getSimulator().checkRelationsForCardinalityViolations();
	}

	public void setupException(SimulatedTestVector vector, SimulationException simulatedException) {
		int lineNumber = vector.getCurrentlyExecutingLine();
		simulatedException.setLineNumber(lineNumber);
		String currentSyntax = vector.getCurrentlyExecutingSyntax();
		simulatedException.setSyntax(currentSyntax);
		vector.setException(simulatedException);
		this._wereExceptionsRaised = true;
	}

	public boolean allAssertionsPassed() {
		return this._haveAllAssertionsPassed;
	}

	public boolean wereExceptionsRaised() {
		return this._wereExceptionsRaised;
	}

}
