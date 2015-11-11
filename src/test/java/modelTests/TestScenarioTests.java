package test.java.modelTests;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.TestScenario;

public class TestScenarioTests extends TestCase {
	public TestScenarioTests(String name) {
		super(name);
	}

	public void test_scenario_has_a_name()
	{
		String scenarioName = "Adding Selection To Order";
		TestScenario scenario = new TestScenario();
		scenario.setName(scenarioName);
		Assert.assertEquals(scenarioName, scenario.getName());
	}
	
	public void test_scenario_has_a_description()
	{
		String scenarioDescription = "When adding selection to an order, the total quantity should increase by the requested amount";
		TestScenario scenario = new TestScenario();
		scenario.setDescription(scenarioDescription);
		Assert.assertEquals(scenarioDescription, scenario.getDescription());
	}
	
	public void test_when_adding_a_scenario_to_a_domain_the_scenario_knows_what_domain_its_for()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		Assert.assertEquals(domain, scenario.getDomain());
	}
	
	public void test_when_adding_a_scenario_to_a_domain_the_count_increases_by_1()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		Assert.assertEquals(0, domain.getScenarios().size());
		domain.addScenario(scenario);
		Assert.assertEquals(1, domain.getScenarios().size());
	}
	
	public void test_when_adding_a_scenario_to_a_domain_the_domain_can_retrieve_the_added_scenario()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		Assert.assertEquals(0, domain.getScenarios().size());
		domain.addScenario(scenario);
		Assert.assertEquals(true, domain.getScenarios().contains(scenario));
	}
}
