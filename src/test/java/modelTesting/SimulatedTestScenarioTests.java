package test.java.modelTesting;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.TestScenario;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import test.java.tests.SimulatedTestScenario;
import test.java.tests.TestHarness;

public class SimulatedTestScenarioTests extends TestCase {
	public SimulatedTestScenarioTests(String name) {
		super(name);
	}

	public void test_can_create_simulated_test_scenario_from_test_scenario()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		
		SimulatedTestScenario simulated = new SimulatedTestScenario(scenario);
		Assert.assertEquals(scenario, simulated.getScenario());
	}
	
	public void test_can_create_test_harness_for_domain() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		
		TestHarness harness = new TestHarness(domain);
		Assert.assertEquals(1, harness.getScenarios().size());
	}
	
}
