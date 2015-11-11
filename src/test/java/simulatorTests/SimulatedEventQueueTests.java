package test.java.simulatorTests;

import test.java.helper.DomainTTD;

import java.util.ArrayList;

import junit.framework.TestCase;

import test.java.mock.MockEventTimeProvider;

import org.junit.Assert;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.events.SimulatedEventQueue;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedEventQueueTests extends TestCase{

	public SimulatedEventQueueTests(String name) {
		super(name);
	}

	public void test_event_storage_can_register_an_event() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event = new SimulatedEventInstance(sender, destination);
		
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		
		Assert.assertEquals(0, queue.size());
		
		queue.registerEventInstance(event);
		
		Assert.assertEquals(1, queue.size());
	
	}
	
	public void test_event_storage_can_get_the_next_event_without_considering_delay() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		SimulatedEventInstance event1 = new SimulatedEventInstance(sender, destination);
		
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		queue.registerEventInstance(event1);
		
		
		SimulatedEventInstance registered0 = queue.getNextEventInstance();
		SimulatedEventInstance registered1 = queue.getNextEventInstance();
		
		Assert.assertEquals(event0, registered0);
		Assert.assertEquals(event1, registered1);
		
	}
	
	public void test_event_storage_can_get_the_next_event_considering_delay() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		SimulatedEventInstance event1 = new SimulatedEventInstance(sender, destination);
		event0.setDelay(100, DelayUnits.MilliSecond);
		
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		queue.registerEventInstance(event1);
		
		
		SimulatedEventInstance registered0 = queue.getNextEventInstance();
		SimulatedEventInstance registered1 = queue.getNextEventInstance();
		
		Assert.assertEquals(event1, registered0);
		Assert.assertEquals(null, registered1);
	}
	
	public void test_will_only_get_events_with_valid_go_times() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		event0.setDelay(100, DelayUnits.Day);
		SimulatedEventInstance event1 = new SimulatedEventInstance(sender, destination);
		event1.setDelay(500, DelayUnits.Day);
		
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		queue.registerEventInstance(event1);
	
		Assert.assertTrue(!queue.hasReadyEventInstance());
		Assert.assertEquals(null, queue.getNextEventInstance());
		
		// change the time of day using a mocked time
		MockEventTimeProvider mockTimeProvider = new MockEventTimeProvider();
		mockTimeProvider.addOffset(200, DelayUnits.Day);
		queue.setEventTimeProvider(mockTimeProvider);
		
		Assert.assertTrue(queue.hasReadyEventInstance());
		Assert.assertEquals(event0, queue.getNextEventInstance());
	}
	
	public void test_getting_next_event_doesnt_remove_all_occurances_of_the_event() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		Assert.assertEquals(1, queue.size());
		
		queue.registerEventInstance(event0);
		Assert.assertEquals(2, queue.size());
		
		queue.registerEventInstance(event0);
		Assert.assertEquals(3, queue.size());
		
		queue.getNextEventInstance();
		Assert.assertEquals(2, queue.size());
		
		queue.getNextEventInstance();
		Assert.assertEquals(1, queue.size());
		
		queue.getNextEventInstance();
		Assert.assertEquals(0, queue.size());
	}
	
	
	public void test_get_to_self_event_for_instance() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		SimulatedEventInstance event1 = new SimulatedEventInstance(destination, destination);
		SimulatedEventInstance event2 = new SimulatedEventInstance(sender, sender);
		event2.setDelay(1, DelayUnits.Day);
		SimulatedEventInstance event3 = new SimulatedEventInstance(sender, sender);
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		queue.registerEventInstance(event1);
		queue.registerEventInstance(event2);
		queue.registerEventInstance(event3);
		Assert.assertTrue(queue.hasReadyToSelfEventInstanceForInstance(sender));
		SimulatedEventInstance recieved = queue.getNextToSelfEventForInstance(sender);
		Assert.assertEquals(event3, recieved);
	}
	
	
	public void test_will_process_to_self_ready_events_before_other_ready_events() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		event0.setDelay(1, DelayUnits.Minute);
		event0.setName("Event0");
		
		SimulatedEventInstance event1 = new SimulatedEventInstance(destination, destination);
		event1.setDelay(2, DelayUnits.Minute);
		event1.setName("Event1");
		
		SimulatedEventInstance event2 = new SimulatedEventInstance(sender, sender);
		event2.setDelay(3, DelayUnits.Minute);
		event2.setName("Event2");
		
		SimulatedEventInstance event3 = new SimulatedEventInstance(sender, sender);
		event3.setDelay(4, DelayUnits.Minute);
		event3.setName("Event3");
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		
		// change the time of day using a mocked time
		MockEventTimeProvider mockTimeProvider = new MockEventTimeProvider();
		mockTimeProvider.addOffset(800, DelayUnits.Minute);
		queue.setEventTimeProvider(mockTimeProvider);
		
		Assert.assertTrue(event0.getGoTime() < event1.getGoTime());
		Assert.assertTrue(event1.getGoTime() < event2.getGoTime());
		Assert.assertTrue(event2.getGoTime() < event3.getGoTime());
		
		Assert.assertTrue(queue.selfTestQueue());
		queue.registerEventInstance(event0);
		Assert.assertTrue(queue.selfTestQueue());
		queue.registerEventInstance(event1);
		Assert.assertTrue(queue.selfTestQueue());
		queue.registerEventInstance(event2);
		Assert.assertTrue(queue.selfTestQueue());
		queue.registerEventInstance(event3);
		Assert.assertTrue(queue.selfTestQueue());
		
		SimulatedEventInstance rec0 = queue.getNextEventInstance();
		Assert.assertTrue(queue.selfTestQueue());
		SimulatedEventInstance rec1 = queue.getNextEventInstance();
		Assert.assertTrue(queue.selfTestQueue());
		SimulatedEventInstance rec2 = queue.getNextEventInstance();
		Assert.assertTrue(queue.selfTestQueue());
		SimulatedEventInstance rec3 = queue.getNextEventInstance();
		Assert.assertTrue(queue.selfTestQueue());
		
		
		Assert.assertEquals(event1, rec0);
		Assert.assertEquals(event2, rec1);
		Assert.assertEquals(event3, rec2);
		Assert.assertEquals(event0, rec3);
	}
	
	public void test_cancelling_an_event_doesnt_remove_all_occurances_of_the_event() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		event0.setName("event0");
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		Assert.assertEquals(1, queue.size());
		
		queue.registerEventInstance(event0);
		Assert.assertEquals(2, queue.size());
		
		queue.removeEvent("event0", sender, destination);
		Assert.assertEquals(1, queue.size());
		
		queue.getNextEventInstance();
		Assert.assertEquals(0, queue.size());
	}

	public void test_event_queue_can_update_all_events_for_a_list_of_ids_to_point_to_a_new_id() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance user02 = userSimulated.createInstance();
		SimulatedInstance user03 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		
		SimulatedInstanceIdentifier sender0 = user01.getIdentifier();
		SimulatedInstanceIdentifier sender1 = user02.getIdentifier();
		SimulatedInstanceIdentifier sender2 = user03.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(destination, sender0);
		SimulatedEventInstance event1 = new SimulatedEventInstance(destination, sender1);
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		Assert.assertEquals(1, queue.size());
		
		queue.registerEventInstance(event1);
		Assert.assertEquals(2, queue.size());
		
		queue.registerEventInstance(event1);
		Assert.assertEquals(3, queue.size());
		
		Assert.assertEquals(true, queue.anyEventsForInstanceIdentifier(sender0));
		Assert.assertEquals(true, queue.anyEventsForInstanceIdentifier(sender1));
		Assert.assertEquals(false, queue.anyEventsForInstanceIdentifier(sender2));
		
		ArrayList<SimulatedInstanceIdentifier> fromList = new ArrayList<SimulatedInstanceIdentifier>();
		fromList.add(sender0);
		fromList.add(sender1);
		
		queue.redirectHierarchyEvents(fromList, sender2);
		
		Assert.assertEquals(false, queue.anyEventsForInstanceIdentifier(sender0));
		Assert.assertEquals(false, queue.anyEventsForInstanceIdentifier(sender1));
		Assert.assertEquals(true, queue.anyEventsForInstanceIdentifier(sender2));
	}

	public void test_registering_event_when_destination_is_hierarchy_event_is_registered_for_root() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass superClass = new EntityClass ("Super");
		ttdDomain.addClass(superClass);
		EntityClass task = ttdDomain.getEntityClassWithName("Task");
		superClass.addSubClass(task);
		
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		SimulatedClass superSimulated = simulator.getSimulatedClass("Super");
		
		SimulatedInstance user01 = userSimulated.createInstance();
		SimulatedInstance task01 = taskSimulated.createInstance();
		SimulatedInstance super01 = superSimulated.getInstances().get(0);
		
		SimulatedInstanceIdentifier sender = user01.getIdentifier();
		SimulatedInstanceIdentifier destination = task01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		event0.setName("event0");
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		Assert.assertEquals(1, queue.size());

		Assert.assertEquals(superSimulated, ((SimulatedHierarchyInstance)task01).getRootInstance().getSimulatedClass());
		
		Assert.assertEquals(false, queue.anyEventsForInstanceIdentifier(destination));
		Assert.assertEquals(true, queue.anyEventsForInstanceIdentifier(super01.getIdentifier()));
	}
	
	public void test_registering_event_when_destination_is_hierarchy_can_identify_to_self() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass superClass = new EntityClass ("Super");
		ttdDomain.addClass(superClass);
		EntityClass task = ttdDomain.getEntityClassWithName("Task");
		superClass.addSubClass(task);
		
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		SimulatedClass superSimulated = simulator.getSimulatedClass("Super");
		
		SimulatedInstance task01 = taskSimulated.createInstance();
		SimulatedInstance super01 = superSimulated.getInstances().get(0);
		
		SimulatedInstanceIdentifier sender = task01.getIdentifier();
		SimulatedInstanceIdentifier destination = super01.getIdentifier();
		
		SimulatedEventInstance event0 = new SimulatedEventInstance(sender, destination);
		event0.setName("event0");
		
		SimulatedEventQueue queue = new SimulatedEventQueue(simulator);
		queue.registerEventInstance(event0);
		Assert.assertEquals(1, queue.size());
		SimulatedEventInstance eventInstance = queue.getNextEventInstance();
		Assert.assertEquals(true, eventInstance.isToSelf());
	}
	
}
