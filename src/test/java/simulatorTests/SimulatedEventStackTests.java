package test.java.simulatorTests;

import junit.framework.TestCase;

import org.junit.Assert;

import main.java.avii.simulator.events.SimulatedEventStack;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedEventStackTests extends TestCase {

	public SimulatedEventStackTests(String a)
	{
		super(a);
	}
	
	public void test_event_stack_is_for_an_instance()
	{
		SimulatedInstanceIdentifier instanceId = new SimulatedInstanceIdentifier(23, null);
		SimulatedEventStack eventStack = new SimulatedEventStack();
		eventStack.setCurrentInstance(instanceId);
		Assert.assertEquals(instanceId, eventStack.getCurrentInstance());
	}
	
	public void test_event_stack_can_be_free()
	{
		SimulatedInstanceIdentifier instanceId = new SimulatedInstanceIdentifier(23, null);
		SimulatedEventStack eventStack = new SimulatedEventStack();
		Assert.assertTrue(eventStack.isFree());
		eventStack.setCurrentInstance(instanceId);
		Assert.assertTrue(!eventStack.isFree());
	}
	
	public void test_event_stack_can_be_freed()
	{
		SimulatedInstanceIdentifier instanceId = new SimulatedInstanceIdentifier(23, null);
		SimulatedEventStack eventStack = new SimulatedEventStack();
		Assert.assertTrue(eventStack.isFree());
		eventStack.setCurrentInstance(instanceId);
		Assert.assertTrue(!eventStack.isFree());
		eventStack.freeUp();
		Assert.assertTrue(eventStack.isFree());
	}
	
}

