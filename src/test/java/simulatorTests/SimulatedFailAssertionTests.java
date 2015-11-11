package test.java.simulatorTests;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorProcedure;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import main.java.avii.simulator.simulatedTypes.TestVectorSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.ISimulatedActionLanguage;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.NoMatchingSimulatedActionLanguageException;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntaxFactory;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_FailAssertion;
import test.java.tests.SimulatedTestScenario;
import test.java.tests.TestHarness;

public class SimulatedFailAssertionTests extends TestCase {
	public SimulatedFailAssertionTests(String name)
	{
		super(name);
	}
	
	private Simulator getSimulator() throws CannotSimulateDomainThatIsInvalidException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		Simulator simulator = new Simulator(shoppingCartDomain);
		return simulator;
	}
	
	public void test_can_create_simulated_assertion() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException
	{
		Simulator simulator = getSimulator();
		IActionLanguageSyntax concreteFail = ActionLanguageSupportedSyntax.getSyntaxForLine("FAIL 5;");
		ISimulatedActionLanguage simulatedFail = SimulatedSyntaxFactory.getSimulatedSyntax(simulator, concreteFail);
		Assert.assertTrue(simulatedFail instanceof SimulatedSyntax_FailAssertion);
	}
	
	private Simulator initSimulatorForTests(SimulatedTestVector vector) throws CannotSimulateDomainThatIsInvalidException {
		Simulator simulator = new Simulator(vector.getVector().getScenario().getDomain());
		initSimulatorForTestsInner(simulator, vector);
		vector.setSimulator(simulator);
		return simulator;
	}

	private void resetSimulator(Simulator simulator, SimulatedTestVector vector)
	{
		initSimulatorForTestsInner(simulator, vector);
	}
	
	private void initSimulatorForTestsInner(Simulator simulator, SimulatedTestVector vector) {
		simulator.setSimulatingVector(vector);
		simulator.setSimulatingInstance(new TestVectorSimulatedInstance());
	}
	
	public void test_assertion_vector_will_halt_execution_when_assertion_fails() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		EntityClass car = new EntityClass("Car");
		
		domain.addScenario(scenario);
		domain.addClass(car);
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "temp = 1;\n";
		proc += "FAIL 3 + temp;\n";
		proc += "temp = 2;\n";
		procedure.setProcedure(proc);
		procedure.validate();
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();

		Simulator simulator = initSimulatorForTests(simVector);
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		Assert.assertEquals(1.0, simVector.getTempVariable("temp"));
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals(4.0, simVector.getAssertionFail());
	}
	
	public void test_can_evaluate_fail_message() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		EntityClass car = new EntityClass("Car");
		
		domain.addScenario(scenario);
		domain.addClass(car);
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "FAIL 3 + 2;\n";
		procedure.setProcedure(proc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals(5.0, simVector.getAssertionFail());
	}
	
	
	public void test_can_evaluate_complex_fail_message() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		EntityClass car = new EntityClass("Car");
		
		domain.addScenario(scenario);
		domain.addClass(car);
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "FAIL \"The added Body should have had an order of \" + (2 + 1) + \", but was : \" + 1;\n";
		procedure.setProcedure(proc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals("The added Body should have had an order of 3.0, but was : 1", simVector.getAssertionFail());
	}
}

