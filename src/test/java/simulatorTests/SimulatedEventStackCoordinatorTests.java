package test.java.simulatorTests;

import junit.framework.TestCase;

import org.junit.Assert;

import main.java.avii.simulator.events.SimulatedEventStack;
import main.java.avii.simulator.events.SimulatedEventStackCoordinator;

public class SimulatedEventStackCoordinatorTests extends TestCase {
	public SimulatedEventStackCoordinatorTests(String name)
	{
		super(name);
	}
	
	public void test_coordinator_can_be_set_to_have_a_specific_number_of_stacks()
	{
		SimulatedEventStackCoordinator coordinator = new SimulatedEventStackCoordinator(6);
		Assert.assertEquals(6, coordinator.getStackCount());
	}
	
	public void test_coordinator_has_all_stacks_free_after_create()
	{
		SimulatedEventStackCoordinator coordinator = new SimulatedEventStackCoordinator(6);
		for(SimulatedEventStack stack : coordinator.getStacks())
		{
			Assert.assertTrue(stack.isFree());
		}
	}

}

