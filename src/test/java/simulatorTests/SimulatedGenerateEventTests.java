package test.java.simulatorTests;

import test.java.helper.TestHelper;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.TestCase;

import test.java.mock.MockEventTimeProvider;

import org.junit.Assert;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;

public class SimulatedGenerateEventTests extends TestCase {
	public SimulatedGenerateEventTests(String name)
	{
		super(name);
	}
	
	public static EntityDomain get2StateDomain(String proc1, String proc2) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("testDomain");
		EntityClass class1 = new EntityClass("Class1");
		domain.addClass(class1);
		EntityAttribute tag = new EntityAttribute("Tag", StringEntityDatatype.getInstance());
		class1.addAttribute(tag);
		EntityAttribute value = new EntityAttribute("Value", IntegerEntityDatatype.getInstance());
		class1.addAttribute(value);
		EntityState firstState = new EntityState("First");
		EntityState secondState = new EntityState("Second");
		EntityState blackHole = new EntityState("BlackHole");
		class1.addState(firstState);
		class1.addState(secondState);
		class1.addState(blackHole);
		EntityEventSpecification spec1 = new EntityEventSpecification(class1, "Event1");
		EntityEventSpecification spec2 = new EntityEventSpecification(class1, "Event2");
		spec2.addEventParam(new EntityEventParam("number", IntegerEntityDatatype.getInstance()));
		spec2.addEventParam(new EntityEventParam("string", StringEntityDatatype.getInstance()));
		EntityEventInstance instance1 = new EntityEventInstance(spec1, firstState, secondState);
		EntityEventInstance instance2 = new EntityEventInstance(spec1, secondState, secondState);
		EntityEventInstance instance3 = new EntityEventInstance(spec2, firstState, blackHole);
		class1.addEventInstance(spec1, instance1);
		class1.addEventInstance(spec1, instance2);
		class1.addEventInstance(spec2, instance3);
		EntityProcedure firstProc = new EntityProcedure(firstState);
		firstProc.setProcedure(proc1);
		EntityProcedure secondProc = new EntityProcedure(secondState);
		secondProc.setProcedure(proc2);
		

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(class1);
		
		
		return domain;
	}
	
	public void test_can_generate_to_self_event() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE Event1() TO self;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
	}
	
	public void test_simulator_will_transition_to_generated_event() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "self.Tag=\"firstState\";\nGENERATE Event1() TO self;\n";
		String proc2 = "self.Tag=\"secondState\";\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		self.setSimulatingState(simulatedClass1.getStateWithName("First"));
		
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals("firstState", self.getAttribute("Tag"));
		Assert.assertEquals("First", self.getSimulatedState().getName());
		simulator.executeNextStateProcedure();
		Assert.assertEquals("Second", self.getSimulatedState().getName());
		Assert.assertEquals("secondState", self.getAttribute("Tag"));
		
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		Assert.assertEquals(3, simulator.getDiagnostics().countOfInstructionsExecuted());
	}
	
	public void test_simulator_will_transition_created_instance_to_generated_event() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "CREATE bob FROM Class1;\nbob.Tag=\"initialState\";\nGENERATE EventOne() TO bob;\n";
		String proc2 = "self.Tag=\"secondState\";\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		self.setSimulatingState(simulatedClass1.getStateWithName("First"));
		
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		
		SimulatedInstance bob = simulator.getSimulatingState().getInstanceWithName("bob");
		
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals("initialState", bob.getAttribute("Tag"));
		Assert.assertEquals("NewInitialState", bob.getSimulatedState().getName());
		simulator.executeNextStateProcedure();
		Assert.assertEquals("First", bob.getSimulatedState().getName());
		Assert.assertEquals("initialState", bob.getAttribute("Tag"));
		Assert.assertEquals("First", self.getSimulatedState().getName());
		Assert.assertEquals("", self.getAttribute("Tag"));
		
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(6, simulator.getDiagnostics().countOfInstructionsExecuted());
	}
	
	public void test_simulator_will_transition_to_generated_event_with_delay() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "self.Tag=\"firstState\";\nGENERATE EventOne() TO self DELAY 2Days;\n";
		String proc2 = "self.Tag=\"secondState\";\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(false, simulator.hasReadyEvent());

		simulator.getSimulatingState().simulate();
		
		MockEventTimeProvider mockProvider = new MockEventTimeProvider();
		mockProvider.addOffset(1, DelayUnits.Day);
		simulator.setMockTimeProvider(mockProvider);
		
		Assert.assertEquals(false, simulator.hasReadyEvent());
		
		mockProvider.addOffset(2, DelayUnits.Day);
		simulator.setMockTimeProvider(mockProvider);
		
		Assert.assertEquals(true, simulator.hasReadyEvent());
		Assert.assertEquals("firstState", self.getAttribute("Tag"));
		Assert.assertEquals("NewInitialState", self.getSimulatedState().getName());
		simulator.executeNextStateProcedure();
		Assert.assertEquals("First", self.getSimulatedState().getName());
		Assert.assertEquals("firstState", self.getAttribute("Tag"));
		
		Assert.assertEquals(true, simulator.hasReadyEvent());
		Assert.assertEquals(4, simulator.getDiagnostics().countOfInstructionsExecuted());
	}
	
	public void test_can_generate_creator_event_and_count_of_instances_increases_by_1_and_gevent_count_increases_by_1() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE EventOne() TO Class1 CREATOR;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(1, simulatedClass1.getInstances().size());
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(2, simulatedClass1.getInstances().size());
	}
	
	public void test_can_generate_creator_event_and_will_execute_event() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "GENERATE EventOne() TO Class1 CREATOR;\n";
		String proc2 = "GENERATE EventOne() TO Class1 CREATOR;\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(1, simulatedClass1.getInstances().size());
		Assert.assertEquals(0, simulator.getEventsInQueueSize());

		simulator.getSimulatingState().simulate();

		Assert.assertEquals(self, simulator.getSimulatingInstance());
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(2, simulatedClass1.getInstances().size());
		
		SimulatedInstance newlyCreatedInstance = simulatedClass1.getInstances().get(1);
		Assert.assertEquals(false, self.equals(newlyCreatedInstance));
		
		boolean wasExecuted = simulator.executeNextStateProcedure();
		Assert.assertEquals(newlyCreatedInstance, simulator.getSimulatingInstance());
		
		Assert.assertEquals(true, wasExecuted);
		
		Assert.assertEquals(3, simulatedClass1.getInstances().size());
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
	}
	
	public void test_can_generate_creator_delay_event_and_count_of_instances_increases_by_1_and_gevent_count_increases_by_1() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE EventOne() TO Class1 CREATOR DELAY 2Days;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(1, simulatedClass1.getInstances().size());
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		
		Assert.assertEquals(2, simulatedClass1.getInstances().size());
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(false, simulator.hasReadyEvent());
		
		MockEventTimeProvider mockProvider = new MockEventTimeProvider();
		mockProvider.addOffset(3, DelayUnits.Day);
		simulator.setMockTimeProvider(mockProvider);
		
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(2, simulatedClass1.getInstances().size());
		Assert.assertEquals(true, simulator.hasReadyEvent());
	}
	
	public void test_can_generate_creator_delay_event_and_will_execute_event() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "GENERATE EventOne() TO Class1 CREATOR DELAY 2Days;\n";
		String proc2 = "GENERATE EventOne() TO Class1 CREATOR DELAY 2Days;\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(1, simulatedClass1.getInstances().size());
		Assert.assertEquals(0, simulator.getEventsInQueueSize());

		simulator.getSimulatingState().simulate();

		Assert.assertEquals(self, simulator.getSimulatingInstance());
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(2, simulatedClass1.getInstances().size());
		
		SimulatedInstance newlyCreatedInstance = simulatedClass1.getInstances().get(1);
		Assert.assertEquals(false, self.equals(newlyCreatedInstance));
		
		boolean wasExecuted = simulator.executeNextStateProcedure();
		Assert.assertEquals(false, wasExecuted);

		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		Assert.assertEquals(2, simulatedClass1.getInstances().size());
		
		MockEventTimeProvider mockProvider = new MockEventTimeProvider();
		mockProvider.addOffset(3, DelayUnits.Day);
		simulator.setMockTimeProvider(mockProvider);
		
		wasExecuted = simulator.executeNextStateProcedure();
		Assert.assertEquals(true, wasExecuted);
		
		Assert.assertEquals(3, simulatedClass1.getInstances().size());
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
	}
	
	public void test_can_cancel_event_in_action_language() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE Event1() TO self;\nCANCEL Event1 FROM self TO self;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
	}	
	
	public void test_was_to_self_event_generated() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE Event1() TO self;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(false, simulator.hasToSelfEventsForInstanceIdentifier(self.getIdentifier()));
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(true, simulator.hasToSelfEventsForInstanceIdentifier(self.getIdentifier()));
	}
	
	public void test_was_to_self_event_generated_and_canceled() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE Event1() TO self;\n\nCANCEL Event1 FROM self TO self;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(false, simulator.hasToSelfEventsForInstanceIdentifier(self.getIdentifier()));
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(false, simulator.hasToSelfEventsForInstanceIdentifier(self.getIdentifier()));
	}
	
	
	public void test_can_generate_event_with_params() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "GENERATE Event2(number=1, string=\"fred\") TO self;\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		Simulator simulator = new Simulator(domain);
		SimulatedClass simulatedClass1 = simulator.getSimulatedClass("Class1");
		SimulatedInstance self = simulatedClass1.createInstance();
		EntityState initialState = simulatedClass1.getStateWithName("First").getConcreteState();
		
		simulator.setSimulatingState(initialState);
		simulator.setSimulatingInstance(self);
		
		Assert.assertEquals(0, simulator.getEventsInQueueSize());
		simulator.getSimulatingState().simulate();
		Assert.assertEquals(1, simulator.getEventsInQueueSize());
		SimulatedEventInstance eventInstance = simulator.getEventQueue().getNextEventInstance();
		Assert.assertEquals(1, eventInstance.getParam("number"));
		Assert.assertEquals("fred", eventInstance.getParam("string"));
	}
	
}

