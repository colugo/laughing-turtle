package test.java.diagnosticsTests;


import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.diagnostics.BaseInstanceLifecycleStage;
import main.java.avii.diagnostics.CreatedInstanceLifecycleStage;
import main.java.avii.diagnostics.Diagnostics;
import main.java.avii.diagnostics.InstanceLifecycle;
import main.java.avii.diagnostics.ProcedureDiagnostics;
import main.java.avii.diagnostics.StatementExecutedDiagnosticsType;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.InitialTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorProcedure;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import main.java.avii.simulator.simulatedTypes.TestVectorSimulatedInstance;
import test.java.helper.TestHelper;
import test.java.mock.MockInstanceLifecycleStage;
import test.java.mock.MockSimulatedActionLanguage;
import test.java.mock.MockSimulatedClass;
import test.java.mock.MockSimulatedInstance;
import test.java.tests.SimulatedTestScenario;
import test.java.tests.TestHarness;
import test.java.weaveModelTests.DomainStore;

public class DiagnosticsTests extends TestCase {
	public DiagnosticsTests(String name) {
		super(name);
	}
	
	public void test_can_increase_number_of_instructions_executed()
	{
		Diagnostics diagnostics = new Diagnostics();
		Assert.assertEquals(0, diagnostics.countOfInstructionsExecuted());
		diagnostics.instructionExecuted(null);
		Assert.assertEquals(1, diagnostics.countOfInstructionsExecuted());
	}
		
	public void test_can_count_number_of_instances_in_trace()
	{
		Diagnostics diagnostics = new Diagnostics();
		Assert.assertEquals(0, diagnostics.countOfInstanceLifeCycleStages());
		
		MockSimulatedClass mockClass = new MockSimulatedClass("MockClass");
		MockSimulatedInstance instance1 = (MockSimulatedInstance) mockClass.createInstance();
		MockSimulatedInstance instance2 = (MockSimulatedInstance) mockClass.createInstance();

		MockInstanceLifecycleStage instance1Event = new MockInstanceLifecycleStage(instance1);
		MockInstanceLifecycleStage instance2Event = new MockInstanceLifecycleStage(instance2);
		
		
		diagnostics.registerInstanceStage(instance1Event);
		Assert.assertEquals(1, diagnostics.countOfInstanceLifeCycleStages());
		Assert.assertEquals(1, diagnostics.getInstanceCount());
		
		diagnostics.registerInstanceStage(instance2Event);
		Assert.assertEquals(2, diagnostics.countOfInstanceLifeCycleStages());
		Assert.assertEquals(2, diagnostics.getInstanceCount());
		
		diagnostics.registerInstanceStage(instance2Event);
		Assert.assertEquals(3, diagnostics.countOfInstanceLifeCycleStages());
		Assert.assertEquals(2, diagnostics.getInstanceCount());
	}
	
	public void test_can_get_instance_trace_from_diagnostics()
	{
		Diagnostics diagnostics = new Diagnostics();
		Assert.assertEquals(0, diagnostics.countOfInstanceLifeCycleStages());
		
		MockSimulatedClass mockClass = new MockSimulatedClass("MockClass");
		MockSimulatedInstance instance1 = (MockSimulatedInstance) mockClass.createInstance();
		MockSimulatedInstance instance2 = (MockSimulatedInstance) mockClass.createInstance();
		MockInstanceLifecycleStage instance1Event = new MockInstanceLifecycleStage(instance1);
		MockInstanceLifecycleStage instance2Event = new MockInstanceLifecycleStage(instance2);
		
		diagnostics.registerInstanceStage(instance1Event);
		diagnostics.registerInstanceStage(instance2Event);
		diagnostics.registerInstanceStage(instance2Event);
		
		InstanceLifecycle instance1LifeCycle = diagnostics.getInstanceLifecycle(instance1Event.getInstance().getIdentifier());
		Assert.assertEquals(1, instance1LifeCycle.getStageCount());
		
		InstanceLifecycle instance2LifeCycle = diagnostics.getInstanceLifecycle(instance2Event.getInstance().getIdentifier());
		Assert.assertEquals(2, instance2LifeCycle.getStageCount());
	}
	
	public void test_instance_trace_can_handles_ignored_events()
	{
		Diagnostics diagnostics = new Diagnostics();
		Assert.assertEquals(0, diagnostics.countOfInstanceLifeCycleStages());
		
		EntityState doorOpen = new EntityState("DoorOpen");
		EntityState doorClosed = new EntityState("DoorClosed");
		
		MockSimulatedClass mockClass = new MockSimulatedClass("MockClass");
		MockSimulatedInstance instance1 = (MockSimulatedInstance) mockClass.createInstance();

		MockInstanceLifecycleStage stage2 = new MockInstanceLifecycleStage(instance1);
		MockInstanceLifecycleStage stage3 = new MockInstanceLifecycleStage(instance1);
		
		SimulatedState simulatedDoorClosed = new SimulatedState(doorClosed, null);
		SimulatedState simulatedDoorOpen = new SimulatedState(doorOpen, null);
		
		instance1.setSimulatingState(simulatedDoorClosed);
		BaseInstanceLifecycleStage createStage = new CreatedInstanceLifecycleStage(instance1);
		diagnostics.registerInstanceStage(createStage);
		
		stage2.setEventName("openDoor");
		diagnostics.registerInstanceStage(stage2);
		instance1.setSimulatingState(simulatedDoorOpen);
		stage2.setCurrentState(instance1.getSimulatedState().getConcreteState());

		stage3.setEventName("openDoor");
		stage3.setCurrentState(instance1.getSimulatedState().getConcreteState());
		stage3.ignore();
		diagnostics.registerInstanceStage(stage3);
		
		
		InstanceLifecycle lifeCycle = diagnostics.getInstanceLifecycle(instance1.getIdentifier());
		Assert.assertEquals("DoorClosed", lifeCycle.getCurrentState());
		lifeCycle.forward();
		Assert.assertEquals("openDoor()", lifeCycle.getTriggeringEvent());
		Assert.assertEquals("DoorOpen", lifeCycle.getCurrentState());
		lifeCycle.forward();
		Assert.assertEquals("DoorOpen", lifeCycle.getCurrentState());
		Assert.assertEquals(true, lifeCycle.wasIgnored());
		lifeCycle.back();
		Assert.assertEquals("openDoor()", lifeCycle.getTriggeringEvent());
		Assert.assertEquals("DoorOpen", lifeCycle.getCurrentState());
		
		
		String expected = "MockClass[Mocking]0 : Created in DoorClosed\nMockClass[Mocking]0 : openDoor() -> DoorOpen\nMockClass[Mocking]0 : openDoor() was ignored\n";
		Assert.assertEquals(expected, lifeCycle.toString());
		
		
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
	
	public void test_check_diagnostics_can_track_instance_through_execution() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		/*
		 * can get list of 'stages' of execution for an instance
		 * for each stage, can check the previous state, the current state, and the triggering event
		 */
		
		DomainStore domainStore = new DomainStore();
		EntityDomain domain = domainStore.domain;
		domain.getScenarios().clear();
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);;
		
		String generateText ="GENERATE restock() TO bread;\n" +
				"GENERATE sellItem(itemName=\"Bread\", quantity=5) TO bread;\n";
		
		domainStore.addBreadAndMilkToIGA(vector, 100, 100, generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.state != \"Selling Item\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'Selling Item' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 95 THEN\n";
		assertionProcedureText += "	FAIL \"bread can be sold when back in stock, it should be 95 but is \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";

		
		assertionProcedure.setProcedure(assertionProcedureText);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		Diagnostics diagnostics = simulator.getDiagnostics();
		
		simVector.executeTestSetupVector();

		SimulatedInstanceIdentifier breadId = simVector.getInstanceWithName("bread").getIdentifier();
		
		// restock
		simVector.executeNextStateProcedure();

		// sell 5 bread
		simVector.executeNextStateProcedure();
		
		resetSimulator(simulator, simVector);
		simVector.executeAssertionVector();
	
		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);
		
		Assert.assertEquals("Initial", breadLifecycle.getCurrentState());
		Assert.assertEquals(true, breadLifecycle.isInitial());
		breadLifecycle.forward();
		Assert.assertEquals("Initial", breadLifecycle.getCurrentState());
		Assert.assertEquals(true, breadLifecycle.wasIgnored());
		breadLifecycle.forward();
		Assert.assertEquals("Selling Item", breadLifecycle.getCurrentState());
		Assert.assertEquals("sellItem(itemName=\"Bread\", quantity=5)", breadLifecycle.getTriggeringEvent());
		Assert.assertEquals(false, breadLifecycle.wasIgnored());
		
		
		String expected = "\n\n---------------InstanceLifecycleLog-----------------\n" +
				"Store[Store Domain]0 : Created\n" +
				"Item[Store Domain]0 : Created in Initial\n" +
				"Item[Store Domain]1 : Created in Initial\n" +
				"Item[Store Domain]0 : restock() was ignored\n" +
				"Item[Store Domain]0 : sellItem(itemName=\"Bread\", quantity=5) -> Selling Item\n\n\n";
		
		Assert.assertEquals(expected, diagnostics.getInstanceLifecycleLog());
		
		Assert.assertEquals(false, simVector.hasAssertionFailed());
		
	}
	
	
	public void test_eventLifecycleEvent_knows_what_other_events_are_waiting_for_the_instance() throws CannotSimulateDomainThatIsInvalidException, NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		DomainStore domainStore = new DomainStore();
		EntityDomain domain = domainStore.domain;
		domain.getScenarios().clear();
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);;
		
		String generateText ="bread.ReOrderQuantity = 100;\n" +
				"GENERATE restock() TO bread;\n" +
				"GENERATE sellItem(itemName=\"Bread\", quantity=5) TO bread;\n";
		
		domainStore.addBreadAndMilkToIGA(vector, 100, 100, generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.state != \"No stock\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'No stock' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 95 THEN\n";
		assertionProcedureText += "	FAIL \"bread can be sold when back in stock, it should be 95 but is \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		Diagnostics diagnostics = simulator.getDiagnostics();
		
		simVector.executeTestSetupVector();

		SimulatedInstanceIdentifier breadId = simVector.getInstanceWithName("bread").getIdentifier();
		
		// restock
		simVector.executeNextStateProcedure();

		// sell 5 bread
		simVector.executeNextStateProcedure();
		
		// out of stock
		simVector.executeNextStateProcedure();
		
		resetSimulator(simulator, simVector);
		simVector.executeAssertionVector();
	
		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);
		
		Assert.assertEquals("Initial", breadLifecycle.getCurrentState());
		Assert.assertEquals(true, breadLifecycle.isInitial());
		Assert.assertEquals(false, breadLifecycle.hasWaitingEvents());
		breadLifecycle.forward();
		Assert.assertEquals("Initial", breadLifecycle.getCurrentState());
		Assert.assertEquals(true, breadLifecycle.wasIgnored());
		Assert.assertEquals(true, breadLifecycle.hasWaitingEvents());
		Assert.assertEquals(1, breadLifecycle.getWaitingEventsForInstance(breadId).size());
		Assert.assertEquals("sellItem(itemName=\"Bread\", quantity=5)", breadLifecycle.getWaitingEventsForInstance(breadId).get(0).describe());
		breadLifecycle.forward();
		Assert.assertEquals("Selling Item", breadLifecycle.getCurrentState());
		Assert.assertEquals("sellItem(itemName=\"Bread\", quantity=5)", breadLifecycle.getTriggeringEvent());
		Assert.assertEquals(false, breadLifecycle.wasIgnored());
		
		Assert.assertEquals(false, breadLifecycle.hasWaitingEvents());
		
		String expected = "\n\n---------------InstanceLifecycleLogWithProcedures-----------------\n" +
				"Store[Store Domain]0 : Created\n" +
				"Item[Store Domain]0 : Created in Initial\n" +
				"Item[Store Domain]1 : Created in Initial\n" +
				"Item[Store Domain]0 : restock() was ignored\n" +
				"Item[Store Domain]0 : sellItem(itemName=\"Bread\", quantity=5) -> Selling Item\n" +
				"	1 : self.Quantity = self.Quantity - rcvd_event.quantity;\n" +
				"	2 : IF self.Quantity < self.ReOrderQuantity THEN\n" +
				"	3 : GENERATE outOfStock() TO self;\n" +
				"	4 : END IF;\n\n" +
				"Item[Store Domain]0 : outOfStock() -> No stock\n\n\n";
		
		Assert.assertEquals(expected, diagnostics.getInstanceLifecycleLogWithProcedures());
		Assert.assertEquals(simVector.hasAssertionFailed(), false);
			
	}
	
	public void test_can_track_waiting_events_for_every_action_language_statement() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException,	CannotSimulateDomainThatIsInvalidException, NameNotFoundException 
	{
		DomainStore domainStore = new DomainStore();
		EntityDomain domain = domainStore.domain;
		domain.getScenarios().clear();

		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);

		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		;

		String generateText = "bread.ReOrderQuantity = 100;\n" + "GENERATE restock() TO bread;\n"
				+ "GENERATE sellItem(itemName=\"Bread\", quantity=5) TO bread;\n";

		domainStore.addBreadAndMilkToIGA(vector, 100, 100, generateText);

		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.state != \"Selling Item\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'Selling Item' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 95 THEN\n";
		assertionProcedureText += "	FAIL \"bread can be sold when back in stock, it should be 95 but is \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";

		assertionProcedure.setProcedure(assertionProcedureText);

		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();

		Simulator simulator = initSimulatorForTests(simVector);
		Diagnostics diagnostics = simulator.getDiagnostics();

		simVector.executeTestSetupVector();

		SimulatedInstanceIdentifier breadId = simVector.getInstanceWithName("bread").getIdentifier();

		// restock
		simVector.executeNextStateProcedure();

		// sell 5 bread
		simVector.executeNextStateProcedure();

		resetSimulator(simulator, simVector);
		simVector.executeAssertionVector();

		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);

		Assert.assertEquals("Initial", breadLifecycle.getCurrentState());
		Assert.assertEquals(true, breadLifecycle.isInitial());
		Assert.assertEquals(false, breadLifecycle.hasWaitingEvents());
		breadLifecycle.forward();
		Assert.assertEquals("Initial", breadLifecycle.getCurrentState());
		Assert.assertEquals(true, breadLifecycle.wasIgnored());
		Assert.assertEquals(true, breadLifecycle.hasWaitingEvents());
		Assert.assertEquals(1, breadLifecycle.getWaitingEventsForInstance(breadId).size());
		Assert.assertEquals("sellItem(itemName=\"Bread\", quantity=5)", breadLifecycle.getWaitingEventsForInstance(breadId).get(0).describe());
		breadLifecycle.forward();
		Assert.assertEquals("Selling Item", breadLifecycle.getCurrentState());
		Assert.assertEquals("sellItem(itemName=\"Bread\", quantity=5)", breadLifecycle.getTriggeringEvent());
		Assert.assertEquals(false, breadLifecycle.wasIgnored());

		Assert.assertEquals(false, breadLifecycle.hasWaitingEvents());
		ProcedureDiagnostics procedureDiagnostics = breadLifecycle.getProcedureDiagnostics();
		Assert.assertEquals("self.Quantity = self.Quantity - rcvd_event.quantity;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(0, procedureDiagnostics.getWaitingEventsForInstance(breadId).size());
		Assert.assertEquals(true, procedureDiagnostics.forward());
		Assert.assertEquals("IF self.Quantity < self.ReOrderQuantity THEN", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(0, procedureDiagnostics.getWaitingEventsForInstance(breadId).size());
		Assert.assertEquals(true, procedureDiagnostics.forward());
		Assert.assertEquals("GENERATE outOfStock() TO self;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(1, procedureDiagnostics.getWaitingEventsForInstance(breadId).size());
		Assert.assertEquals("outOfStock()", procedureDiagnostics.getWaitingEventsForInstance(breadId).get(0).describe());
		Assert.assertEquals(true, procedureDiagnostics.forward());
		Assert.assertEquals("END IF;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(1, procedureDiagnostics.getWaitingEventsForInstance(breadId).size());
		Assert.assertEquals(false, procedureDiagnostics.forward());

		Assert.assertEquals(false, simVector.hasAssertionFailed());
	}
	
	public void test_simulatedstate_diagnostics()
	{
		ProcedureDiagnostics pd = new ProcedureDiagnostics();
		MockSimulatedActionLanguage statement1 = new MockSimulatedActionLanguage("CREATE Fred;\n",1);
		MockSimulatedActionLanguage statement2 = new MockSimulatedActionLanguage("DELETE Fred;\n",2);
		
		StatementExecutedDiagnosticsType type1 = new StatementExecutedDiagnosticsType();
		type1.setStatement(statement1);
		
		StatementExecutedDiagnosticsType type2 = new StatementExecutedDiagnosticsType();
		type2.setStatement(statement2);
		
		StatementExecutedDiagnosticsType type3 = new StatementExecutedDiagnosticsType();
		type3.setStatement(statement1);
		
		pd.addStatement(type1);
		pd.addStatement(type2);
		pd.addStatement(type3);
		
		Assert.assertEquals("CREATE Fred;", pd.getSyntaxString());
		Assert.assertEquals(1, pd.getSyntaxLineNumber());
		pd.forward();
		Assert.assertEquals("DELETE Fred;", pd.getSyntaxString());
		Assert.assertEquals(2, pd.getSyntaxLineNumber());
		pd.forward();
		Assert.assertEquals("CREATE Fred;", pd.getSyntaxString());
		Assert.assertEquals(1, pd.getSyntaxLineNumber());
		pd.back();
		Assert.assertEquals("DELETE Fred;", pd.getSyntaxString());
		Assert.assertEquals(2, pd.getSyntaxLineNumber());
	}
	
	public void test_instance_stage_has_list_of_statements_executed_with_line_numbers() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Age = (4 + 1) * 2;\n";
		procedureText += "temp = user1.Age;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		
		Simulator simulator = new Simulator(testDomain);
		Diagnostics diagnostics = simulator.getDiagnostics();
		simulator.setSimulatingState(initialUserState);
		
		MockSimulatedInstance instance = new MockSimulatedInstance();
		simulator.setSimulatingInstance(instance);
		MockInstanceLifecycleStage mockStage = new MockInstanceLifecycleStage(instance);
		diagnostics.registerInstanceStage(mockStage);

		SimulatedState state = simulator.getSimulatingState();
		
		state.simulate();
		
		SimulatedInstanceIdentifier breadId = instance.getIdentifier();

		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);
		ProcedureDiagnostics procedureDiagnostics = breadLifecycle.getProcedureDiagnostics();
		
		Assert.assertEquals("CREATE user1 FROM User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(1, procedureDiagnostics.getSyntaxLineNumber());
		procedureDiagnostics.forward();
		Assert.assertEquals("user1.Age = (4 + 1) * 2;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(2, procedureDiagnostics.getSyntaxLineNumber());
		procedureDiagnostics.forward();
		Assert.assertEquals("temp = user1.Age;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(3, procedureDiagnostics.getSyntaxLineNumber());
	}
	
	public void test_can_check_relations_for_each_action_language() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(user);
		testDomain.addClass(task);
		
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(user, CardinalityType.ZERO_TO_MANY);
		r1.setEndB(task, CardinalityType.ZERO_TO_MANY);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n" +
				"CREATE task1 FROM Task;\n" +
				"RELATE user1 TO task1 ACROSS R1;\n" +
				"temp = 1;\n" +
				"UNRELATE user1 FROM task1 ACROSS R1;\n" +
				"temp = 2;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		
		Simulator simulator = new Simulator(testDomain);
		SimulatedRelationship simR1 = simulator.getRelationshipWithName("R1");
		Diagnostics diagnostics = simulator.getDiagnostics();
		simulator.setSimulatingState(initialUserState);
		
		MockSimulatedInstance instance = new MockSimulatedInstance();
		simulator.setSimulatingInstance(instance);
		MockInstanceLifecycleStage mockStage = new MockInstanceLifecycleStage(instance);
		diagnostics.registerInstanceStage(mockStage);

		SimulatedState state = simulator.getSimulatingState();
		
		state.simulate();
		
		SimulatedInstanceIdentifier user1Id = state.getInstanceWithName("user1").getIdentifier();
		SimulatedInstanceIdentifier task1Id = state.getInstanceWithName("task1").getIdentifier();
		
		SimulatedInstanceIdentifier breadId = instance.getIdentifier();

		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);
		ProcedureDiagnostics procedureDiagnostics = breadLifecycle.getProcedureDiagnostics();
		
		Assert.assertEquals("CREATE user1 FROM User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(1, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(user1Id));
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(task1Id));
		procedureDiagnostics.forward();
		
		Assert.assertEquals("CREATE task1 FROM Task;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(2, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(user1Id));
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(task1Id));
		procedureDiagnostics.forward();
		
		Assert.assertEquals("RELATE user1 TO task1 ACROSS R1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(3, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(user1Id));
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(task1Id));
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(user1Id).hasRelationsOverRelation(simR1));
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(task1Id).hasRelationsOverRelation(simR1));
		Assert.assertEquals(1, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(user1Id).getRelationsOverRelation(simR1).size());
		Assert.assertEquals(1, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(task1Id).getRelationsOverRelation(simR1).size());
		Assert.assertEquals(task1Id, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(user1Id).getRelationsOverRelation(simR1).get(0).getRelatedInstance());
		Assert.assertEquals(user1Id, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(task1Id).getRelationsOverRelation(simR1).get(0).getRelatedInstance());
		procedureDiagnostics.forward();
		
		Assert.assertEquals("temp = 1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(4, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(user1Id));
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(task1Id));
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(user1Id).hasRelationsOverRelation(simR1));
		Assert.assertEquals(true, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(task1Id).hasRelationsOverRelation(simR1));
		Assert.assertEquals(1, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(user1Id).getRelationsOverRelation(simR1).size());
		Assert.assertEquals(1, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(task1Id).getRelationsOverRelation(simR1).size());
		Assert.assertEquals(task1Id, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(user1Id).getRelationsOverRelation(simR1).get(0).getRelatedInstance());
		Assert.assertEquals(user1Id, procedureDiagnostics.getRelaionshipDiagnostics().getRelationsForInstance(task1Id).getRelationsOverRelation(simR1).get(0).getRelatedInstance());	procedureDiagnostics.forward();
		
		Assert.assertEquals("UNRELATE user1 FROM task1 ACROSS R1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(5, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(user1Id));
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(task1Id));
		procedureDiagnostics.forward();
		
		Assert.assertEquals("temp = 2;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(6, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(user1Id));
		Assert.assertEquals(false, procedureDiagnostics.getRelaionshipDiagnostics().hasRelationsForInstance(task1Id));

}

	public void test_can_check_instance_attributes_for_each_action_language() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Age = (4 + 1) * 2;\n";
		procedureText += "temp = user1.Age;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		
		Simulator simulator = new Simulator(testDomain);
		Diagnostics diagnostics = simulator.getDiagnostics();
		simulator.setSimulatingState(initialUserState);
		
		MockSimulatedInstance instance = new MockSimulatedInstance();
		simulator.setSimulatingInstance(instance);
		MockInstanceLifecycleStage mockStage = new MockInstanceLifecycleStage(instance);
		diagnostics.registerInstanceStage(mockStage);

		SimulatedState state = simulator.getSimulatingState();
		
		state.simulate();
		
		SimulatedInstanceIdentifier breadId = instance.getIdentifier();
		SimulatedInstanceIdentifier user1Id = state.getInstanceWithName("user1").getIdentifier();

		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);
		ProcedureDiagnostics procedureDiagnostics = breadLifecycle.getProcedureDiagnostics();
		
		Assert.assertEquals("CREATE user1 FROM User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(1, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getInstanceDiagnostics().hasDiagnosticsForInstance(user1Id));
		Assert.assertEquals(false, procedureDiagnostics.getInstanceDiagnostics().hasDiagnosticsForInstance(breadId));
		Assert.assertEquals("0", procedureDiagnostics.getInstanceDiagnostics().getDiagnosticsForInstance(user1Id).getAttribute("Age"));
		procedureDiagnostics.forward();
		Assert.assertEquals("user1.Age = (4 + 1) * 2;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(2, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals("10", procedureDiagnostics.getInstanceDiagnostics().getDiagnosticsForInstance(user1Id).getAttribute("Age"));
		procedureDiagnostics.forward();
		Assert.assertEquals("temp = user1.Age;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(3, procedureDiagnostics.getSyntaxLineNumber());
	}
	
	public void test_can_map_instance_names_and_instanceset_names_and_variable_names_to_instance_diagnostic_for_each_line_of_action_language() throws Exception
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(task);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n" +
				"CREATE user2 FROM User;\n" +
				"CREATE user3 FROM User;\n" +
				"CREATE task1 FROM Task;\n" +
				"temp = 1;\n" +
				"SELECT MANY users FROM INSTANCES OF User;\n" +
				"FOR selectedUser IN users DO\n" +
				"	temp = temp + 1;\n" +
				"END FOR;\n" +
				"temp = 2;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		
		Simulator simulator = new Simulator(testDomain);
		Diagnostics diagnostics = simulator.getDiagnostics();
		simulator.setSimulatingState(initialUserState);
		
		MockSimulatedInstance instance = new MockSimulatedInstance();
		simulator.setSimulatingInstance(instance);
		MockInstanceLifecycleStage mockStage = new MockInstanceLifecycleStage(instance);
		diagnostics.registerInstanceStage(mockStage);

		SimulatedState state = simulator.getSimulatingState();
		
		state.simulate();

		SimulatedInstanceIdentifier user1 = state.getInstanceWithName("user1").getIdentifier();
		SimulatedInstanceIdentifier user2 = state.getInstanceWithName("user2").getIdentifier();
		SimulatedInstanceIdentifier user3 = state.getInstanceWithName("user3").getIdentifier();
		SimulatedInstanceIdentifier task1 = state.getInstanceWithName("task1").getIdentifier();
		
		
		SimulatedInstanceIdentifier breadId = instance.getIdentifier();

		InstanceLifecycle breadLifecycle = diagnostics.getInstanceLifecycle(breadId);
		ProcedureDiagnostics procedureDiagnostics = breadLifecycle.getProcedureDiagnostics();
		
		Assert.assertEquals("CREATE user1 FROM User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(1, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("self"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user1"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user2"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user3"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("task1"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("CREATE user2 FROM User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(2, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("self"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user2"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user3"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("task1"));
		
		
		procedureDiagnostics.forward();
		Assert.assertEquals("CREATE user3 FROM User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(3, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(4, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("self"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user3"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("task1"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("CREATE task1 FROM Task;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(4, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		Assert.assertEquals(5, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("self"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("task1"));
		Assert.assertEquals(0, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetNames().size());
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetNames().contains("users"));
		Assert.assertEquals(0, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().size());
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().contains("temp"));

		procedureDiagnostics.forward();
		Assert.assertEquals("temp = 1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(5, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().contains("temp"));
		Assert.assertEquals("1.0", procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableWithName("temp"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("SELECT MANY users FROM INSTANCES OF User;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(6, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetNames().contains("users"));

		procedureDiagnostics.forward();
		Assert.assertEquals("FOR selectedUser IN users DO", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(7, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(0, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(6, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("self"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceNames().contains("selectedUser"));
		

		
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("temp = temp + 1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(8, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(0, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().contains("temp"));
		Assert.assertEquals("2.0", procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableWithName("temp"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("END FOR;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(9, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(0, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("FOR selectedUser IN users DO", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(7, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		
		procedureDiagnostics.forward();
		Assert.assertEquals("temp = temp + 1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(8, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().contains("temp"));
		Assert.assertEquals("3.0", procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableWithName("temp"));

		
		procedureDiagnostics.forward();
		Assert.assertEquals("END FOR;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(9, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));

		
		procedureDiagnostics.forward();
		Assert.assertEquals("FOR selectedUser IN users DO", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(7, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));

		procedureDiagnostics.forward();
		Assert.assertEquals("temp = temp + 1;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(8, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().contains("temp"));
		Assert.assertEquals("4.0", procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableWithName("temp"));

		
		procedureDiagnostics.forward();
		Assert.assertEquals("END FOR;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(9, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("selectedUser"));
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));

		
		procedureDiagnostics.forward();
		Assert.assertEquals("FOR selectedUser IN users DO", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(7, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));

		
		procedureDiagnostics.forward();
		Assert.assertEquals("temp = 2;", procedureDiagnostics.getSyntaxString());
		Assert.assertEquals(10, procedureDiagnostics.getSyntaxLineNumber());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user2"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("user3"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("task1"));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceSetWithName("users"));
		Assert.assertEquals(3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").instanceCount());
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").isIterating());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user1));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user2));
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(user3));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").hasInstanceWithIdentifier(task1));
		Assert.assertEquals(false, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().hasInstanceWithName("selectedUser"));
		Assert.assertEquals(2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceSetWithName("users").iterationCount());
		Assert.assertEquals(user1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user1"));
		Assert.assertEquals(user2, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user2"));
		Assert.assertEquals(user3, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("user3"));
		Assert.assertEquals(task1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getInstanceWithName("task1"));
		Assert.assertEquals(1, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().size());
		Assert.assertEquals(true, procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableNames().contains("temp"));
		Assert.assertEquals("2.0", procedureDiagnostics.getNamedInstanceAndVariableDiagnostics().getVariableWithName("temp"));
		
	}
}
